package com.example.gestorRecursos.services;

import com.example.gestorRecursos.models.Valoracion;
import com.example.gestorRecursos.repositories.ValoracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ValoracionService {

    @Autowired
    private ValoracionRepository valoracionRepository;

    public List<Valoracion> obtenerTodas() {
        return valoracionRepository.findAll();
    }

    public Optional<Valoracion> obtenerPorId(Long id) {
        return valoracionRepository.findById(id);
    }

    public Valoracion crearValoracion(Valoracion nueva) throws Exception {
        try {
            // Spring Boot intentará guardar. Si la base de datos (Trigger) rechaza la operación 
            // porque el usuario es dueño del documento, atrapará el error aquí.
            return valoracionRepository.save(nueva);
        } catch (Exception e) {
            throw new Exception("Error al guardar: No puedes valorar tu propio documento o el documento no existe.");
        }
    }

    public Valoracion actualizarValoracion(Long id, Valoracion detalles) throws Exception {
        Optional<Valoracion> existente = valoracionRepository.findById(id);
        if (existente.isPresent()) {
            Valoracion val = existente.get();
            val.setPuntuacion_numerica(detalles.getPuntuacion_numerica());
            return valoracionRepository.save(val);
        }
        throw new Exception("Valoración no encontrada");
    }

    public void eliminarValoracion(Long id) throws Exception {
        if (!valoracionRepository.existsById(id)) {
            throw new Exception("Valoración no encontrada");
        }
        valoracionRepository.deleteById(id);
    }
}