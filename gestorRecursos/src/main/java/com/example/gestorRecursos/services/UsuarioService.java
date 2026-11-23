package com.example.gestorRecursos.services;

import com.example.gestorRecursos.dto.LoginResponseDTO;
import com.example.gestorRecursos.models.Usuario;
import com.example.gestorRecursos.repositories.UsuarioRepository;
import com.example.gestorRecursos.security.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public Usuario registrarUsuario(Usuario nuevoUsuario) throws Exception {
        if (usuarioRepository.findByCorreo(nuevoUsuario.getCorreo()).isPresent()) {
            throw new Exception("El correo ya está registrado.");
        }
        // Encriptar contraseña con BCrypt antes de guardar en MySQL
        nuevoUsuario.setContrasena(passwordEncoder.encode(nuevoUsuario.getContrasena()));
        return usuarioRepository.save(nuevoUsuario);
    }

    public LoginResponseDTO autenticarUsuario(Usuario credenciales) throws Exception {
        Optional<Usuario> usuarioDb = usuarioRepository.findByCorreo(credenciales.getCorreo());

        if (usuarioDb.isPresent() && passwordEncoder.matches(credenciales.getContrasena(), usuarioDb.get().getContrasena())) {
            Usuario u = usuarioDb.get();
            String token = jwtUtil.generarToken(u.getCorreo());
            // Devuelve el DTO seguro sin contraseña
            return new LoginResponseDTO(token, u.getIdUsuario(), u.getNombre(), u.getCorreo());
        } else {
            throw new Exception("Correo o contraseña incorrectos.");
        }
    }

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void solicitarRecuperacion(String correo) throws Exception {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new Exception("No existe ninguna cuenta registrada con ese correo."));

        // Generar código numérico seguro de 6 dígitos
        String codigo = String.format("%06d", new SecureRandom().nextInt(999999));
        usuario.setResetToken(codigo);
        usuario.setResetTokenExpiracion(LocalDateTime.now().plusMinutes(15));
        usuarioRepository.save(usuario);

        // Enviar correo (con respaldo en consola por si Railway bloquea puertos SMTP)
        if (mailSender == null) {
            System.out.println("ERROR: JavaMailSender es NULL. Maven no ha cargado spring-boot-starter-mail.");
        } else {
            try {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setFrom("tu_correo@gmail.com"); // Pon aquí el mismo correo de application.properties
                mensaje.setTo(correo);
                mensaje.setSubject("Apunta - Código de recuperación de contraseña");
                mensaje.setText("Hola " + usuario.getNombre() + ",\n\n"
                        + "Tu código para restablecer tu contraseña es: " + codigo + "\n"
                        + "Este código expirará en 15 minutos.");
                mailSender.send(mensaje);
                System.out.println("Correo enviado exitosamente a: " + correo);
            } catch (Exception e) {
                System.out.println("ERROR SMTP al enviar correo:");
                e.printStackTrace();
            }
        }
    }

    public void restablecerContrasena(String correo, String codigo, String nuevaContrasena) throws Exception {
        if (nuevaContrasena == null || nuevaContrasena.length() < 8) {
            throw new Exception("La nueva contraseña debe tener al menos 8 caracteres.");
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new Exception("Usuario no encontrado."));

        if (usuario.getResetToken() == null || !usuario.getResetToken().equals(codigo)) {
            throw new Exception("El código de verificación es incorrecto.");
        }

        if (usuario.getResetTokenExpiracion().isBefore(LocalDateTime.now())) {
            throw new Exception("El código ha expirado. Solicita uno nuevo.");
        }

        // Encriptar la nueva contraseña e invalidar el código usado
        usuario.setContrasena(passwordEncoder.encode(nuevaContrasena));
        usuario.setResetToken(null);
        usuario.setResetTokenExpiracion(null);
        usuarioRepository.save(usuario);
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