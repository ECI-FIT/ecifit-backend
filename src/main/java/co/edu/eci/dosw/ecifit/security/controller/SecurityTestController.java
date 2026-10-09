package co.edu.eci.dosw.ecifit.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class SecurityTestController {

    @GetMapping("/publico")
    public ResponseEntity<Map<String, String>> publico() {
        return ResponseEntity.ok(Map.of("mensaje", "Endpoint público accesible"));
    }

    @GetMapping("/protegido")
    public ResponseEntity<Map<String, String>> protegido() {
        return ResponseEntity.ok(Map.of("mensaje", "Endpoint protegido accesible para autenticados"));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Map<String, String>> soloAdmin() {
        return ResponseEntity.ok(Map.of("mensaje", "Acceso concedido a Administrador"));
    }

    @GetMapping("/estudiante")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<Map<String, String>> soloEstudiante() {
        return ResponseEntity.ok(Map.of("mensaje", "Acceso concedido a Estudiante"));
    }
}
