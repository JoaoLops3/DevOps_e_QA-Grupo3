package org.example.bdd_teste.service;

import org.example.bdd_teste.domain.Aluno;
import org.example.bdd_teste.domain.CalculadoraProgresso;
import org.example.bdd_teste.domain.CursoConcluido;
import org.example.bdd_teste.domain.Progresso;
import org.example.bdd_teste.domain.TipoAssinatura;
import org.example.bdd_teste.dto.AlunoRequest;
import org.example.bdd_teste.dto.AlunoResponse;
import org.example.bdd_teste.dto.ConclusaoRequest;
import org.example.bdd_teste.dto.ProgressoResponse;
import org.example.bdd_teste.entity.AlunoEntity;
import org.example.bdd_teste.entity.ConclusaoCursoEntity;
import org.example.bdd_teste.exception.RecursoNaoEncontradoException;
import org.example.bdd_teste.repository.AlunoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AlunoService {

    private final AlunoRepository alunoRepository;
    private final CalculadoraProgresso calculadoraProgresso;

    public AlunoService(AlunoRepository alunoRepository) {
        this.alunoRepository = alunoRepository;
        this.calculadoraProgresso = new CalculadoraProgresso();
    }

    @Transactional
    public AlunoResponse criar(AlunoRequest request) {
        AlunoEntity entity = new AlunoEntity();
        entity.setNome(request.nome().trim());
        entity.setTipoAssinatura(TipoAssinatura.BASICA);
        entity.setMoedas(0);
        return toResponse(alunoRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<AlunoResponse> listar() {
        return alunoRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AlunoResponse buscarPorId(Long id) {
        return toResponse(buscarEntity(id));
    }

    @Transactional
    public ProgressoResponse registrarConclusao(Long id, ConclusaoRequest request) {
        AlunoEntity entity = buscarEntity(id);
        Aluno dominio = toDomain(entity);

        dominio.concluirCurso(new CursoConcluido(request.media()));

        ConclusaoCursoEntity conclusao = new ConclusaoCursoEntity();
        conclusao.setTituloCurso(request.tituloCurso().trim());
        conclusao.setMedia(request.media());
        entity.adicionarConclusao(conclusao);

        entity.setTipoAssinatura(dominio.getTipoAssinatura());
        entity.setMoedas(dominio.getMoedas());
        alunoRepository.save(entity);

        return toProgressoResponse(dominio);
    }

    @Transactional(readOnly = true)
    public ProgressoResponse obterProgresso(Long id) {
        AlunoEntity entity = buscarEntity(id);
        return toProgressoResponse(toDomain(entity));
    }

    private AlunoEntity buscarEntity(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Aluno não encontrado: " + id));
    }

    /**
     * Reconstrói o agregado a partir do estado persistido.
     * Usa restauração de histórico (sem promoção) para a leitura refletir o banco.
     */
    private Aluno toDomain(AlunoEntity entity) {
        Aluno aluno = new Aluno(entity.getTipoAssinatura(), entity.getMoedas());
        for (ConclusaoCursoEntity conclusao : entity.getConclusoes()) {
            aluno.restaurarCursoDoHistorico(new CursoConcluido(conclusao.getMedia()));
        }
        return aluno;
    }

    private AlunoResponse toResponse(AlunoEntity entity) {
        return new AlunoResponse(
                entity.getId(),
                entity.getNome(),
                entity.getTipoAssinatura(),
                entity.getMoedas()
        );
    }

    private ProgressoResponse toProgressoResponse(Aluno dominio) {
        Progresso progresso = calculadoraProgresso.calcular(dominio);
        return new ProgressoResponse(
                progresso.cursosConcluidos(),
                progresso.cursosFaltantes(),
                dominio.getTipoAssinatura(),
                dominio.getMoedas()
        );
    }
}
