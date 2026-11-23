package com.example.gestorRecursos.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.gestorRecursos.models.Documento;
import com.example.gestorRecursos.repositories.DocumentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class DocumentoService {

    @Autowired
    private DocumentoRepository documentoRepository;

    private final Cloudinary cloudinary;
    private final long TAMAÑO_MAXIMO = 5 * 1024 * 1024; // Límite de 5MB

    // El constructor inyecta las credenciales de tu application.properties automáticamente
    public DocumentoService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true));
    }

    public Documento guardarDocumentoEnLaNube(MultipartFile archivo, Long idUsuario, Long idAsignatura) throws Exception {
        
        // 1. Validaciones de seguridad (Mantenemos tu estándar 5.0)
        if (archivo.isEmpty()) {
            throw new Exception("El archivo está vacío.");
        }
        if (archivo.getSize() > TAMAÑO_MAXIMO) {
            throw new Exception("El archivo excede el tamaño máximo permitido de 5MB.");
        }
        String tipoArchivo = archivo.getContentType();
        if (tipoArchivo == null || (!tipoArchivo.equals("application/pdf") && !tipoArchivo.contains("word"))) {
            throw new Exception("Formato inválido. Solo se permiten archivos PDF o Word.");
        }

        // 2. Subir el archivo a Cloudinary
        // Configuramos resource_type en "auto" para que acepte PDFs y Words, no solo imágenes
        Map uploadResult = cloudinary.uploader().upload(archivo.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto",
                "folder", "apunta_documentos" // Se creará esta carpeta en tu Cloudinary
        ));

        // 3. Obtener el URL público seguro (https) de Cloudinary
        String urlEnLaNube = uploadResult.get("secure_url").toString();
        String nombreOriginal = archivo.getOriginalFilename();

        // 4. Guardar metadatos y el URL en MySQL
        Documento nuevoDocumento = new Documento();
        nuevoDocumento.setIdUsuario(idUsuario);
        nuevoDocumento.setIdAsignatura(idAsignatura);
        nuevoDocumento.setTitulo(nombreOriginal); 
        
        // Magia: en vez de "miArchivo.pdf", ahora guardamos "https://res.cloudinary.com/.../miArchivo.pdf"
        nuevoDocumento.setRuta_archivo(urlEnLaNube); 
        
        nuevoDocumento.setNombreArchivoOriginal(nombreOriginal);
        nuevoDocumento.setPesoArchivo(archivo.getSize());
        nuevoDocumento.setTipoArchivo(tipoArchivo);

        return documentoRepository.save(nuevoDocumento);
    }

    public List<Documento> obtenerPorAsignatura(Long idAsignatura) {
        return documentoRepository.findByIdAsignatura(idAsignatura);
    }
}