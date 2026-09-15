package org.example.bdd_teste.repository;

import org.example.bdd_teste.entity.AlunoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlunoRepository extends JpaRepository<AlunoEntity, Long> {
}
