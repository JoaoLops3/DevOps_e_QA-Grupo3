package com.educont.gamification.domain;

/**
 * Domain Core — Java puro, sem Spring e sem JPA.
 * Regra AC1: média >= 7.0 conta; 12 cursos válidos → Premium + 3 moedas.
 */
public class RewardCalculator {

    public static final double MEDIA_MINIMA = 7.0;
    public static final int META_PREMIUM = 12;
    public static final int MOEDAS_PREMIUM = 3;

    public RewardResult calcular(int cursosValidosAtuais, double mediaNova) {
        if (mediaNova < MEDIA_MINIMA) {
            return RewardResult.ignorado(cursosValidosAtuais);
        }

        int novosValidos = cursosValidosAtuais + 1;
        boolean promoveu = cursosValidosAtuais < META_PREMIUM && novosValidos >= META_PREMIUM;
        int moedas = promoveu ? MOEDAS_PREMIUM : 0;
        String assinatura = novosValidos >= META_PREMIUM ? "PREMIUM" : "BASICA";

        return new RewardResult(novosValidos, assinatura, moedas, true, promoveu);
    }
}
