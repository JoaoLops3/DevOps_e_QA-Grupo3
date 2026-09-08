package org.example.bdd_teste;

public class Progresso {

    private final int cursosConcluidos;
    private final int cursosFaltantes;

    public Progresso(int cursosConcluidos, int cursosFaltantes) {
        this.cursosConcluidos = cursosConcluidos;
        this.cursosFaltantes = cursosFaltantes;
    }

    public int getCursosConcluidos() {
        return cursosConcluidos;
    }

    public int getCursosFaltantes() {
        return cursosFaltantes;
    }
}
