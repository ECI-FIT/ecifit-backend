package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.controller.docs.HealthApi;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class HealthController implements HealthApi {

    @Override
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ECI FIT backend is running");
    }
}
