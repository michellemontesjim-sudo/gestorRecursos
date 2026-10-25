package com.example.gestorRecursos.repositories;

import com.example.gestorRecursos.models.Documento;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    List<Documento> findByIdAsignatura(Long idAsignatura);
}
