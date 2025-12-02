package com.skinstore.pagamento.service;

import com.skinstore.pagamento.entity.Pagamento;
import com.skinstore.pagamento.repository.PagamentoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Service
public class PagamentoService {

    private final PagamentoRepository repo;
    private final SecureRandom random = new SecureRandom();

    @Value("${pagamento.mock.approval-rate:0.8}")
    private double approvalRate;

    public PagamentoService(PagamentoRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Pagamento criarEProcessar(Pagamento p) {
        // status inicial
        p.setStatus(Pagamento.Status.PENDENTE);
        Pagamento salvo = repo.save(p);

        // processa mock
        boolean aprovado = random.nextDouble() < approvalRate;
        salvo.setStatus(aprovado ? Pagamento.Status.APROVADO : Pagamento.Status.RECUSADO);
        salvo.setProcessadoEm(OffsetDateTime.now());

        return repo.save(salvo);
    }

    @Transactional(readOnly = true)
    public Pagamento buscar(Long id) { return repo.findById(id).orElse(null); }

    @Transactional(readOnly = true)
    public java.util.List<Pagamento> listar() { return repo.findAll(); }

    @Transactional
    public Pagamento reprocessar(Long id) {
        Pagamento p = repo.findById(id).orElseThrow();
        boolean aprovado = random.nextDouble() < approvalRate;
        p.setStatus(aprovado ? Pagamento.Status.APROVADO : Pagamento.Status.RECUSADO);
        p.setProcessadoEm(OffsetDateTime.now());
        return repo.save(p);
    }

    @Transactional
    public void deletar(Long id) { repo.deleteById(id); }
}
