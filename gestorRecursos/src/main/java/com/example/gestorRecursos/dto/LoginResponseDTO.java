package com.example.gestorRecursos.dto;

public class LoginResponseDTO {
    private String token;
    private Long idUsuario;
    private String nombre;
    private String correo;

    public LoginResponseDTO(String token, Long idUsuario, String nombre, String correo) {
        this.token = token;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
    }


    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
}