package org.example.bdd_teste.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Agregado do aluno: assinatura, moedas e histórico de cursos concluídos.
 * A promoção para Premium e a contagem de progresso ficam encapsuladas aqui
 * para o domínio não depender de Service/Controller.
 */
public class Aluno {

    /** Meta da história 3QA: 12 cursos válidos para virar Premium. */
    static final int CURSOS_PARA_PREMIUM = 12;

    /** Moedas concedidas na promoção básica → Premium. */
    static final int MOEDAS_PREMIUM = 3;

    private TipoAssinatura tipoAssinatura;
    private int moedas;
    private final List<CursoConcluido> cursosConcluidos = new ArrayList<>();

    public Aluno(TipoAssinatura tipoAssinatura) {
        this.tipoAssinatura = tipoAssinatura;
        this.moedas = 0;
    }

    public Aluno(TipoAssinatura tipoAssinatura, int moedas) {
        this.tipoAssinatura = tipoAssinatura;
        this.moedas = moedas;
    }

    public void concluirCurso(CursoConcluido curso) {
        cursosConcluidos.add(curso);
        promoverSeAtingiuMeta();
    }

    /**
     * Restaura curso já persistido sem reavaliar promoção/moedas.
     * Evita divergência entre leitura da API e estado gravado no banco.
     */
    public void restaurarCursoDoHistorico(CursoConcluido curso) {
        cursosConcluidos.add(curso);
    }

    /**
     * Só conta média ≥ 7,0 (BDD Miguel). Ao atingir 12 válidos,
     * promove para Premium e concede 3 moedas (BDD Hector).
     */
    private void promoverSeAtingiuMeta() {
        if (tipoAssinatura == TipoAssinatura.BASICA
                && contarCursosValidos() >= CURSOS_PARA_PREMIUM) {
            tipoAssinatura = TipoAssinatura.PREMIUM;
            moedas += MOEDAS_PREMIUM;
        }
    }

    public int contarCursosValidos() {
        int total = 0;
        for (CursoConcluido curso : cursosConcluidos) {
            if (curso.temAproveitamento()) {
                total++;
            }
        }
        return total;
    }

    public TipoAssinatura getTipoAssinatura() {
        return tipoAssinatura;
    }

    public int getMoedas() {
        return moedas;
    }

    public List<CursoConcluido> getCursosConcluidos() {
        return Collections.unmodifiableList(cursosConcluidos);
    }
}
