package com.example.gestorRecursos.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "valoracion") 
public class Valoracion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idValoracion")
    private Long idValoracion;

    @Column(nullable = false)
    private Integer puntuacion_numerica;

    @Column(name = "idUsuario", nullable = false)
    private Long idUsuario;

    @Column(name = "idDocumento", nullable = false)
    private Long idDocumento;

    @Column(name = "fecha_valoracion")
    private LocalDateTime fecha_valoracion = LocalDateTime.now(); 

    public Valoracion() {}

    // Getters y Setters
    public Long getIdValoracion() { return idValoracion; }
    public void setIdValoracion(Long idValoracion) { this.idValoracion = idValoracion; }

    public Integer getPuntuacion_numerica() { return puntuacion_numerica; }
    public void setPuntuacion_numerica(Integer puntuacion_numerica) { this.puntuacion_numerica = puntuacion_numerica; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Long getIdDocumento() { return idDocumento; }
    public void setIdDocumento(Long idDocumento) { this.idDocumento = idDocumento; }

    public LocalDateTime getFecha_valoracion() { return fecha_valoracion; }
    public void setFecha_valoracion(LocalDateTime fecha_valoracion) { this.fecha_valoracion = fecha_valoracion; }
}