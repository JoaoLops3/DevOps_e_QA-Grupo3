package org.example.bdd_teste;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    public List<CursoConcluido> getCursosConcluidos() {
        return Collections.unmodifiableList(cursosConcluidos);
    }
}
