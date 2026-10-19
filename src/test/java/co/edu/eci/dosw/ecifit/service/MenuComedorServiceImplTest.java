package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.exception.ConflictoException;
import co.edu.eci.dosw.ecifit.exception.ReglaDeNegocioException;
import co.edu.eci.dosw.ecifit.mapper.MenuComedorEntityMapper;
import co.edu.eci.dosw.ecifit.model.MenuComedor;
import co.edu.eci.dosw.ecifit.model.PlatoMenu;
import co.edu.eci.dosw.ecifit.model.TipoComida;
import co.edu.eci.dosw.ecifit.persistence.MenuComedorEntity;
import co.edu.eci.dosw.ecifit.repository.MenuComedorRepository;
import co.edu.eci.dosw.ecifit.validator.IMenuComedorValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuComedorServiceImplTest {

    @Mock
    private MenuComedorRepository menuComedorRepository;
    @Mock
    private MenuComedorEntityMapper menuComedorEntityMapper;
    @Mock
    private IMenuComedorValidator menuComedorValidator;
    @InjectMocks
    private MenuComedorServiceImpl service;

    private static MenuComedor menu(TipoComida tipo, LocalDate fecha) {
        return new MenuComedor(null, fecha, tipo,
                List.of(new PlatoMenu(null, "Pollo a la plancha", "Con arroz y ensalada", 12000)));
    }

    @Test
    void debePublicarMenuAsignandoIdentificadores() {
        LocalDate fecha = LocalDate.of(2026, 10, 20);
        MenuComedor menu = menu(TipoComida.ALMUERZO, fecha);
        MenuComedorEntity entity = MenuComedorEntity.builder().id("M-1").fecha(fecha).tipo(TipoComida.ALMUERZO).build();
        MenuComedor publicado = menu(TipoComida.ALMUERZO, fecha);
        when(menuComedorRepository.existsByFechaAndTipo(fecha, TipoComida.ALMUERZO)).thenReturn(false);
        when(menuComedorEntityMapper.toEntity(any(MenuComedor.class))).thenReturn(entity);
        when(menuComedorRepository.save(entity)).thenReturn(entity);
        when(menuComedorEntityMapper.toDomain(entity)).thenReturn(publicado);

        MenuComedor resultado = service.publicar(menu);

        assertSame(publicado, resultado);
        assertNotNull(menu.getId());
        assertNotNull(menu.getPlatos().get(0).getId());
        verify(menuComedorValidator).validar(menu);
        verify(menuComedorRepository).save(entity);
    }

    @Test
    void debeRechazarMenuDuplicadoParaLaMismaFechaYTipo() {
        LocalDate fecha = LocalDate.of(2026, 10, 20);
        MenuComedor menu = menu(TipoComida.ALMUERZO, fecha);
        when(menuComedorRepository.existsByFechaAndTipo(fecha, TipoComida.ALMUERZO)).thenReturn(true);

        assertThrows(ConflictoException.class, () -> service.publicar(menu));

        verify(menuComedorRepository, never()).save(any());
    }

    @Test
    void debePropagarErrorDelValidador() {
        MenuComedor menu = menu(TipoComida.ALMUERZO, null);
        doThrow(new ReglaDeNegocioException("La fecha del menú es obligatoria."))
                .when(menuComedorValidator).validar(menu);

        assertThrows(ReglaDeNegocioException.class, () -> service.publicar(menu));

        verifyNoInteractions(menuComedorRepository);
    }

    @Test
    void debeConsultarMenusDeUnaFechaOrdenadosPorTipoDeComida() {
        LocalDate fecha = LocalDate.of(2026, 10, 20);
        MenuComedorEntity cenaEntity = MenuComedorEntity.builder().id("M-2").build();
        MenuComedorEntity desayunoEntity = MenuComedorEntity.builder().id("M-1").build();
        MenuComedor cena = menu(TipoComida.CENA, fecha);
        MenuComedor desayuno = menu(TipoComida.DESAYUNO, fecha);
        when(menuComedorRepository.findByFecha(fecha)).thenReturn(List.of(cenaEntity, desayunoEntity));
        when(menuComedorEntityMapper.toDomain(cenaEntity)).thenReturn(cena);
        when(menuComedorEntityMapper.toDomain(desayunoEntity)).thenReturn(desayuno);

        List<MenuComedor> resultado = service.obtenerPorFecha(fecha);

        assertEquals(List.of(desayuno, cena), resultado);
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayMenu() {
        LocalDate fecha = LocalDate.of(2026, 10, 21);
        when(menuComedorRepository.findByFecha(fecha)).thenReturn(List.of());

        List<MenuComedor> resultado = service.obtenerPorFecha(fecha);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void debeRechazarConsultaSinFecha() {
        assertThrows(ReglaDeNegocioException.class, () -> service.obtenerPorFecha(null));
    }

    @Test
    void debeConsultarElMenuDeHoy() {
        when(menuComedorRepository.findByFecha(LocalDate.now())).thenReturn(List.of());

        List<MenuComedor> resultado = service.obtenerHoy();

        assertTrue(resultado.isEmpty());
        verify(menuComedorRepository).findByFecha(LocalDate.now());
    }
}
