package com.studyhub.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public ResponseEntity<String> checkHealth() {
        try {
            // Consulta súper básica para probar la conexión a la base de datos
            jdbcTemplate.execute("SELECT 1");
            return ResponseEntity.ok("{\"status\": \"OK\", \"database\": \"Connected\"}");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("{\"status\": \"ERROR\", \"database\": \"Disconnected\"}");
        }
    }
}