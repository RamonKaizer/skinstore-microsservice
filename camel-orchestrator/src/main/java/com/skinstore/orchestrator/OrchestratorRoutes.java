package com.skinstore.orchestrator;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class OrchestratorRoutes extends RouteBuilder {

    @Override
    public void configure() {

        // === ERROR HANDLER PADRÃO ===
        onException(Exception.class)
                .handled(true)
                .setHeader(Exchange.HTTP_RESPONSE_CODE, constant(502))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("{\"error\":\"bad_gateway\",\"message\":\"${exception.message}\"}"));

        restConfiguration()
                .component("platform-http")
                .contextPath("/")
                .dataFormatProperty("prettyPrint", "true");

        // === HEALTH ===
        rest("/actuator")
                .get("/health").produces("application/json")
                .to("direct:health");

        from("direct:health")
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(constant("{\"status\":\"UP\"}"));

        // === CHECKOUT ===
        // POST /orq/checkout/{carrinhoId}?metodo=PIX
        rest("/orq")
                .post("/checkout/{carrinhoId}")
                .produces("application/json")
                .to("direct:checkout");

        from("direct:checkout")
                // lê path/query
                .log("START ROTA CHECKOUT")
                .setProperty("carrinhoId", header("carrinhoId"))
                .setProperty("metodo", simple("${header.metodo}"))

                // 1) busca carrinho
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .log("START BUSCA CARRINHO")
                .toD("{{svc.carrinho}}/api/v1/carrinhos/${exchangeProperty.carrinhoId}")
                .log("END BUSCA CARRINHO")
                .setProperty("carrinhoJson").body()
                .log("BUSCA DE CARRINHO COM SUCESSO ${body}")

                //2) fechar carrinho
                .setHeader(org.apache.camel.Exchange.HTTP_METHOD, constant("PATCH"))
                .toD("{{svc.carrinho}}/api/v1/carrinhos/${exchangeProperty.carrinhoId}/status/FECHADO")

                // 3) monta pedido com preços atuais (bean chama produto-service)
                .to("bean:checkoutAssembler?method=montarPedido")
                .setProperty("pedidoRequestJson").body()
                // 3.1) cria pedido
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .to("{{svc.pedido}}/api/v1/pedidos")
                .setProperty("pedidoJson").body()

                // 4) Debitar estoque dos itens
                .to("direct:debitar-estoque-itens")

                // 5) monta pagamento (usa total do pedido)
                .to("bean:checkoutAssembler?method=montarPagamento")
                // 5.1) cria/processa pagamento
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .log("INICIANDO PROCESSAMENTO PEDIDO ${body}")
                .to("{{svc.pagamento}}/api/v1/pagamentos")
                .setProperty("pagamentoJson").body()

                // 6) decide: aprovado x recusado
                .choice()
                .when().jsonpath("$[?(@.status == 'APROVADO')]")
                // pedido -> FINALIZADO
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("PATCH"))
                .toD("{{svc.pedido}}/api/v1/pedidos/${bean:checkoutAssembler?method=pedidoId}/status/FINALIZADO")
                // abrir carrinho
                .removeHeaders("CamelHttp*")
                .setHeader(org.apache.camel.Exchange.HTTP_METHOD, constant("PATCH"))
                .toD("{{svc.carrinho}}/api/v1/carrinhos/${exchangeProperty.carrinhoId}/status/ABERTO")
                // resposta agregada
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("{\"pedidoId\":${bean:checkoutAssembler?method=pedidoId},\"statusPedido\":\"FINALIZADO\",\"pagamento\":${exchangeProperty.pagamentoJson}}"))
                .otherwise()
                // pedido -> CANCELADO
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("PATCH"))
                .toD("{{svc.pedido}}/api/v1/pedidos/${bean:checkoutAssembler?method=pedidoId}/status/CANCELADO")
                // abrir carrinho
                .removeHeaders("CamelHttp*")
                .setHeader(org.apache.camel.Exchange.HTTP_METHOD, constant("PATCH"))
                .toD("{{svc.carrinho}}/api/v1/carrinhos/${exchangeProperty.carrinhoId}/status/ABERTO")
                // resposta
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("{\"pedidoId\":${bean:checkoutAssembler?method=pedidoId},\"statusPedido\":\"CANCELADO\",\"pagamento\":${exchangeProperty.pagamentoJson}}"))
                .end();

        // Debita estoque de cada item do carrinho
        from("direct:debitar-estoque-itens")
                .routeId("debitar-estoque-itens")
                .log("DEBITAR ESTOQUE - usando carrinhoJson")
                .setBody(exchangeProperty("carrinhoJson"))
                .unmarshal().json()                        // body -> Map
                .setBody().simple("${body[itens]}")        // body = lista de itens
                .split().body()                            // itera itens
                .setProperty("produtoId").simple("${body[produtoId]}")
                .setProperty("quantidade").simple("${body[quantidade]}")
                .log("Debitando estoque do produto ${exchangeProperty.produtoId} qtd ${exchangeProperty.quantidade}")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("PATCH"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("{\"quantidade\": ${exchangeProperty.quantidade}}"))
                .toD("{{svc.produto}}/api/v1/produtos/${exchangeProperty.produtoId}/estoque/debitar")
                .end();

        // Reposição de estoque
        from("direct:repor-estoque-itens")
                .routeId("repor-estoque-itens")
                .log("REPOR ESTOQUE - usando carrinhoJson")
                .setBody(exchangeProperty("carrinhoJson"))
                .unmarshal().json()
                .setBody().simple("${body[itens]}")
                .split().body()
                .setProperty("produtoId").simple("${body[produtoId]}")
                .setProperty("quantidade").simple("${body[quantidade]}")
                .log("Repondo estoque do produto ${exchangeProperty.produtoId} qtd ${exchangeProperty.quantidade}")
                .removeHeaders("CamelHttp*")
                .setHeader(Exchange.HTTP_METHOD, constant("PATCH"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .setBody(simple("{\"quantidade\": ${exchangeProperty.quantidade}}"))
                .toD("{{svc.produto}}/api/v1/produtos/${exchangeProperty.produtoId}/estoque/repor")
                .end();

    }
}
