package com.example.gestorRecursos.repositories;

import com.example.gestorRecursos.models.Valoracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ValoracionRepository extends JpaRepository<Valoracion, Long> {
}