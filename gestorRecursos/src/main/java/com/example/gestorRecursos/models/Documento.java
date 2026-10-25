package com.example.gestorRecursos.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documento") 
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDocumento")
    private Long idDocumento;

    @Column(name = "idUsuario", nullable = false)
    private Long idUsuario;

    @Column(name = "idAsignatura", nullable = false)
    private Long idAsignatura;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "ruta_archivo")
    private String ruta_archivo;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fecha_publicacion = LocalDateTime.now(); // Se llena sola

    @Column(name = "num_valoraciones")
    private Integer num_valoraciones = 0; // Inicia en 0 por defecto

    @Column(name = "promedio_valoracion")
    private Double promedio_valoracion = 0.0; // Inicia en 0.0 por defecto

    @Column(name = "nombreArchivoOriginal")
    private String nombreArchivoOriginal;

    @Column(name = "pesoArchivo")
    private Long pesoArchivo; // Usamos Long porque los archivos pueden pesar muchos bytes

    @Column(name = "tipoArchivo")
    private String tipoArchivo;

    public Documento() {}

    // --- GETTERS Y SETTERS ---

    public Long getIdDocumento() { return idDocumento; }
    public void setIdDocumento(Long idDocumento) { this.idDocumento = idDocumento; }

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Long getIdAsignatura() { return idAsignatura; }
    public void setIdAsignatura(Long idAsignatura) { this.idAsignatura = idAsignatura; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getRuta_archivo() { return ruta_archivo; }
    public void setRuta_archivo(String ruta_archivo) { this.ruta_archivo = ruta_archivo; }

    public LocalDateTime getFecha_publicacion() { return fecha_publicacion; }
    public void setFecha_publicacion(LocalDateTime fecha_publicacion) { this.fecha_publicacion = fecha_publicacion; }

    public Integer getNum_valoraciones() { return num_valoraciones; }
    public void setNum_valoraciones(Integer num_valoraciones) { this.num_valoraciones = num_valoraciones; }

    public Double getPromedio_valoracion() { return promedio_valoracion; }
    public void setPromedio_valoracion(Double promedio_valoracion) { this.promedio_valoracion = promedio_valoracion; }

    public String getNombreArchivoOriginal() { return nombreArchivoOriginal; }
    public void setNombreArchivoOriginal(String nombreArchivoOriginal) { this.nombreArchivoOriginal = nombreArchivoOriginal; }

    public Long getPesoArchivo() { return pesoArchivo; }
    public void setPesoArchivo(Long pesoArchivo) { this.pesoArchivo = pesoArchivo; }

    public String getTipoArchivo() { return tipoArchivo; }
    public void setTipoArchivo(String tipoArchivo) { this.tipoArchivo = tipoArchivo; }
}