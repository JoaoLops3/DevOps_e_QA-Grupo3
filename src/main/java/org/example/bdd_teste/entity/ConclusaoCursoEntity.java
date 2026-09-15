package org.example.bdd_teste.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "conclusoes_curso")
public class ConclusaoCursoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String tituloCurso;

    @Column(nullable = false)
    private double media;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "aluno_id", nullable = false)
    private AlunoEntity aluno;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTituloCurso() {
        return tituloCurso;
    }

    public void setTituloCurso(String tituloCurso) {
        this.tituloCurso = tituloCurso;
    }

    public double getMedia() {
        return media;
    }

    public void setMedia(double media) {
        this.media = media;
    }

    public AlunoEntity getAluno() {
        return aluno;
    }

    public void setAluno(AlunoEntity aluno) {
        this.aluno = aluno;
    }
}
