package com.skinstore.orchestrator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component("checkoutAssembler")
public class CheckoutAssembler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ProducerTemplate template;

    // base URLs vindas do application-*.yml
    private final String produtoBase;
    private final String pedidoBase;

    public CheckoutAssembler(
            ProducerTemplate template,
            @Value("${svc.produto}") String produtoBase,
            @Value("${svc.pedido}")  String pedidoBase) {
        this.template   = template;
        this.produtoBase = produtoBase;
        this.pedidoBase  = pedidoBase;
    }

    /**
     * Camel pega da exchangeProperty "carrinhoJson" (payload do carrinho)
     * e da property/header "metodo" (PIX/CARTAO/BOLETO).
     * Aqui montamos o JSON do Pedido consultando o preço atual de cada produto
     * via camel-http (ProducerTemplate).
     */
    public String montarPedido(
            String body,
            @org.apache.camel.Header("metodo") String metodo,
            @org.apache.camel.ExchangeProperty("carrinhoJson") String carrinhoJson
    ) throws Exception {

        JsonNode carrinho = mapper.readTree(carrinhoJson);
        Long usuarioId = carrinho.path("usuarioId").asLong();

        List<Map<String,Object>> itensPedido = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (JsonNode it : carrinho.path("itens")) {
            long produtoId = it.path("produtoId").asLong();
            int qtd = it.path("quantidade").asInt(1);

            // ===== chama produto-service: GET /api/v1/produtos/{id}
            String url = produtoBase + "/api/v1/produtos/" + produtoId;

            Map<String, Object> headers = new HashMap<>();
            headers.put(Exchange.HTTP_METHOD, "GET");

            String produtoJson = template.requestBodyAndHeaders(url, null, headers, String.class);
            JsonNode produto = mapper.readTree(produtoJson);

            BigDecimal preco = readBigDecimal(produto.path("preco"));
            BigDecimal linha = preco.multiply(BigDecimal.valueOf(qtd));
            total = total.add(linha);

            Map<String,Object> item = new HashMap<>();
            item.put("produtoId", produtoId);
            item.put("quantidade", qtd);
            item.put("preco", preco);
            itensPedido.add(item);
        }

        Map<String,Object> pedido = new HashMap<>();
        pedido.put("usuarioId", usuarioId);
        pedido.put("valorTotal", total);
        pedido.put("status", "PENDENTE");
        pedido.put("itens", itensPedido);

        return mapper.writeValueAsString(pedido);
    }

    /**
     * Monta JSON do pagamento a partir do pedido recém-criado (em pedidoJson)
     */
    public String montarPagamento(
            @org.apache.camel.ExchangeProperty("pedidoJson") String pedidoJson,
            @org.apache.camel.ExchangeProperty("metodo") String metodo
    ) throws Exception {
        JsonNode pedido = mapper.readTree(pedidoJson);
        Long pedidoId = pedido.path("id").asLong();
        BigDecimal total = readBigDecimal(pedido.path("valorTotal"));

        Map<String,Object> pagamento = new HashMap<>();
        pagamento.put("pedidoId", pedidoId);
        pagamento.put("metodo", metodo);
        pagamento.put("valor", total);

        return mapper.writeValueAsString(pagamento);
    }

    /**
     * Reuso quando já temos o pedido carregado (rota de reprocessamento).
     */
    public String montarPagamentoFromPedido(
            @org.apache.camel.ExchangeProperty("pedidoJson") String pedidoJson,
            @org.apache.camel.ExchangeProperty("metodo") String metodo
    ) throws Exception {
        return montarPagamento(pedidoJson, metodo);
    }

    /**
     * Extrai ID do pedido do JSON de resposta.
     */
    public Long pedidoId(@org.apache.camel.ExchangeProperty("pedidoJson") String pedidoJson) throws Exception {
        JsonNode pedido = mapper.readTree(pedidoJson);
        return pedido.path("id").asLong();
    }

    // ===== helpers =====

    private static BigDecimal readBigDecimal(JsonNode node) {
        if (node == null || node.isNull()) return BigDecimal.ZERO;
        if (node.isNumber()) return node.decimalValue();
        String txt = node.asText("0");
        try {
            return new BigDecimal(txt);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
