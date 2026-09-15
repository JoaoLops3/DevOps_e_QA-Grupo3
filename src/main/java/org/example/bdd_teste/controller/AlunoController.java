package org.example.bdd_teste.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.bdd_teste.dto.AlunoRequest;
import org.example.bdd_teste.dto.AlunoResponse;
import org.example.bdd_teste.dto.ConclusaoRequest;
import org.example.bdd_teste.dto.ProgressoResponse;
import org.example.bdd_teste.service.AlunoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@Tag(name = "Alunos", description = "Progresso da assinatura básica rumo ao Premium")
public class AlunoController {

    private final AlunoService alunoService;

    public AlunoController(AlunoService alunoService) {
        this.alunoService = alunoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Cria aluno com assinatura BASICA")
    public AlunoResponse criar(@Valid @RequestBody AlunoRequest request) {
        return alunoService.criar(request);
    }

    @GetMapping
    @Operation(summary = "Lista alunos")
    public List<AlunoResponse> listar() {
        return alunoService.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca aluno por id")
    public AlunoResponse buscar(@PathVariable Long id) {
        return alunoService.buscarPorId(id);
    }

    @PostMapping("/{id}/conclusoes")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra conclusão de curso e recalcula promoção")
    public ProgressoResponse registrarConclusao(
            @PathVariable Long id,
            @Valid @RequestBody ConclusaoRequest request) {
        return alunoService.registrarConclusao(id, request);
    }

    @GetMapping("/{id}/progresso")
    @Operation(summary = "Consulta progresso (concluídos, faltantes, assinatura, moedas)")
    public ProgressoResponse progresso(@PathVariable Long id) {
        return alunoService.obterProgresso(id);
    }
}
