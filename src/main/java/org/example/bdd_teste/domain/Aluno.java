package org.example.bdd_teste.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agregado do aluno: assinatura atual e histórico de cursos concluídos.
 * Lista encapsulada (imutável para fora) para o progresso não ser adulterado por fora.
 */
public class Aluno {

    private final TipoAssinatura tipoAssinatura;
    private final List<CursoConcluido> cursosConcluidos = new ArrayList<>();

    public Aluno(TipoAssinatura tipoAssinatura) {
        this.tipoAssinatura = tipoAssinatura;
    }

    public void concluirCurso(CursoConcluido curso) {
        cursosConcluidos.add(curso);
    }

    public TipoAssinatura getTipoAssinatura() {
        return tipoAssinatura;
    }

    /** Stub para o RED compilar; ainda não concede moedas (GREEN virá depois). */
    public int getMoedas() {
        return 0;
    }

    public List<CursoConcluido> getCursosConcluidos() {
        return Collections.unmodifiableList(cursosConcluidos);
    }
}
