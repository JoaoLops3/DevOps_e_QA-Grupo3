package org.example.bdd_teste.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import org.example.bdd_teste.domain.TipoAssinatura;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "alunos")
public class AlunoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoAssinatura tipoAssinatura;

    @Column(nullable = false)
    private int moedas;

    @OneToMany(mappedBy = "aluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConclusaoCursoEntity> conclusoes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public TipoAssinatura getTipoAssinatura() {
        return tipoAssinatura;
    }

    public void setTipoAssinatura(TipoAssinatura tipoAssinatura) {
        this.tipoAssinatura = tipoAssinatura;
    }

    public int getMoedas() {
        return moedas;
    }

    public void setMoedas(int moedas) {
        this.moedas = moedas;
    }

    public List<ConclusaoCursoEntity> getConclusoes() {
        return conclusoes;
    }

    public void setConclusoes(List<ConclusaoCursoEntity> conclusoes) {
        this.conclusoes = conclusoes;
    }

    public void adicionarConclusao(ConclusaoCursoEntity conclusao) {
        conclusoes.add(conclusao);
        conclusao.setAluno(this);
    }
}
