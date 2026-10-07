package com.educont;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

/**
 * Hipótese: ApplicationModules.verify() falha o build se courses/badges
 * dependerem de tipos internos de ranking sem passar pela API nomeada.
 */
@SpringBootTest
class ModularityTests {

    private final ApplicationModules modules = ApplicationModules.of(Application.class);

    @Test
    void verificaFronteirasEntreModulos() {
        modules.verify();
    }

    @Test
    void geraDocumentacaoDeModulos() {
        new Documenter(modules).writeDocumentation();
    }
}
