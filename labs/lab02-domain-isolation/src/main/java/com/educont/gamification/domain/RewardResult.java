package com.educont.gamification.domain;

public record RewardResult(
        int cursosValidos,
        String assinatura,
        int moedasCreditadas,
        boolean contabilizou,
        boolean promoveuParaPremium
) {
    public static RewardResult ignorado(int cursosValidosAtuais) {
        String assinatura = cursosValidosAtuais >= RewardCalculator.META_PREMIUM ? "PREMIUM" : "BASICA";
        return new RewardResult(cursosValidosAtuais, assinatura, 0, false, false);
    }
}
