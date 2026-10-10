package com.educont;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Hipótese: ApplicationModules.verify() falha o build se courses/badges
 * dependerem de tipos internos de ranking sem passar pela API nomeada.
 */
class ModularityTests {

    @Test
    void verificaFronteirasEntreModulos() {
        ApplicationModules.of(Application.class).verify();
    }
}
