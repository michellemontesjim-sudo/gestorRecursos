package com.example.gestorRecursos.services;

import com.example.gestorRecursos.models.Asignatura;
import com.example.gestorRecursos.repositories.AsignaturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AsignaturaService {

    @Autowired
    private AsignaturaRepository asignaturaRepository;

    public List<Asignatura> obtenerTodas() {
        return asignaturaRepository.findAll();
    }

    public Optional<Asignatura> obtenerPorId(Long id) {
        return asignaturaRepository.findById(id);
    }

    public Asignatura crearAsignatura(Asignatura nueva) {
        return asignaturaRepository.save(nueva);
    }

    public Asignatura actualizarAsignatura(Long id, Asignatura detalles) throws Exception {
        Optional<Asignatura> existente = asignaturaRepository.findById(id);
        if (existente.isPresent()) {
            Asignatura materia = existente.get();
            materia.setNombre(detalles.getNombre());
            materia.setSemestre(detalles.getSemestre());
            return asignaturaRepository.save(materia);
        }
        throw new Exception("Asignatura no encontrada");
    }

    public void eliminarAsignatura(Long id) throws Exception {
        if (!asignaturaRepository.existsById(id)) {
            throw new Exception("Asignatura no encontrada");
        }
        asignaturaRepository.deleteById(id);
    }
}