package co.edu.eci.dosw.ecifit.controller;

import co.edu.eci.dosw.ecifit.controller.docs.MenuComedorApi;
import co.edu.eci.dosw.ecifit.dto.request.CrearMenuComedorRequestDTO;
import co.edu.eci.dosw.ecifit.dto.response.MenuComedorResponseDTO;
import co.edu.eci.dosw.ecifit.mapper.MenuComedorMapper;
import co.edu.eci.dosw.ecifit.model.MenuComedor;
import co.edu.eci.dosw.ecifit.service.IMenuComedorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/comedor/menus")
@RequiredArgsConstructor
public class MenuComedorController implements MenuComedorApi {

    private final IMenuComedorService menuComedorService;
    private final MenuComedorMapper menuComedorMapper;

    @Override
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<MenuComedorResponseDTO> publicar(@Valid @RequestBody CrearMenuComedorRequestDTO dto) {
        MenuComedor publicado = menuComedorService.publicar(menuComedorMapper.toDomain(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(menuComedorMapper.toResponse(publicado));
    }

    @Override
    @GetMapping("/hoy")
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ENTRENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<MenuComedorResponseDTO>> obtenerHoy() {
        return ResponseEntity.ok(menuComedorMapper.toResponseList(menuComedorService.obtenerHoy()));
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAnyRole('ESTUDIANTE', 'ENTRENADOR', 'ADMINISTRADOR')")
    public ResponseEntity<List<MenuComedorResponseDTO>> obtenerPorFecha(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(menuComedorMapper.toResponseList(menuComedorService.obtenerPorFecha(fecha)));
    }
}
