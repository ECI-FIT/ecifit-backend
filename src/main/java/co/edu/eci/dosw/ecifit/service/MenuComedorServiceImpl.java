package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.MenuComedorEntityMapper;
import co.edu.eci.dosw.ecifit.model.MenuComedor;
import co.edu.eci.dosw.ecifit.model.PlatoMenu;
import co.edu.eci.dosw.ecifit.persistence.MenuComedorEntity;
import co.edu.eci.dosw.ecifit.repository.MenuComedorRepository;
import co.edu.eci.dosw.ecifit.validator.IMenuComedorValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuComedorServiceImpl implements IMenuComedorService {

    private final MenuComedorRepository menuComedorRepository;
    private final MenuComedorEntityMapper menuComedorEntityMapper;
    private final IMenuComedorValidator menuComedorValidator;

    @Override
    @Transactional
    public MenuComedor publicar(MenuComedor menu) {
        menuComedorValidator.validar(menu);

        if (menuComedorRepository.existsByFechaAndTipo(menu.getFecha(), menu.getTipo())) {
            throw new ConflictoException(String.format(
                    "Ya existe un menú de %s para la fecha %s.", menu.getTipo(), menu.getFecha()));
        }

        menu.setId(UUID.randomUUID().toString());
        for (PlatoMenu plato : menu.getPlatos()) {
            plato.setId(UUID.randomUUID().toString());
        }

        MenuComedorEntity guardado = menuComedorRepository.save(menuComedorEntityMapper.toEntity(menu));
        log.info("Menú del comedor publicado. Fecha: {}, tipo: {}, platos: {}",
                menu.getFecha(), menu.getTipo(), menu.getPlatos().size());
        return menuComedorEntityMapper.toDomain(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuComedor> obtenerPorFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new ReglaDeNegocioException("La fecha es obligatoria para consultar el menú.");
        }
        List<MenuComedor> menus = menuComedorRepository.findByFecha(fecha).stream()
                .map(menuComedorEntityMapper::toDomain)
                .sorted(Comparator.comparing(MenuComedor::getTipo))
                .toList();
        log.info("Menú del comedor consultado para {}: {} menús", fecha, menus.size());
        return menus;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MenuComedor> obtenerHoy() {
        return obtenerPorFecha(LocalDate.now());
    }
}
