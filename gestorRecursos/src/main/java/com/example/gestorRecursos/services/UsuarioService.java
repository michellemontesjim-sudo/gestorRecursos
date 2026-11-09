package com.example.gestorRecursos.services;

import com.example.gestorRecursos.models.Usuario;
import com.example.gestorRecursos.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Lógica de negocio para registrar
    public Usuario registrarUsuario(Usuario nuevoUsuario) throws Exception {
        Optional<Usuario> existente = usuarioRepository.findByCorreo(nuevoUsuario.getCorreo());
        if (existente.isPresent()) {
            throw new Exception("El correo ya está registrado.");
        }
        return usuarioRepository.save(nuevoUsuario);
    }

    // Lógica de negocio para el login
    public Usuario autenticarUsuario(Usuario credenciales) throws Exception {
        Optional<Usuario> usuarioDb = usuarioRepository.findByCorreo(credenciales.getCorreo());
        
        if (usuarioDb.isPresent() && usuarioDb.get().getContrasena().equals(credenciales.getContrasena())) {
            return usuarioDb.get();
        } else {
            throw new Exception("Correo o contraseña incorrectos.");
        }
    }

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public void eliminarUsuario(Long id) throws Exception {
        if (!usuarioRepository.existsById(id)) {
            throw new Exception("Usuario no encontrado.");
        }
        usuarioRepository.deleteById(id);
    }
}