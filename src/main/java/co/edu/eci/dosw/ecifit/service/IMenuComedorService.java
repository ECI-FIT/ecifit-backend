package co.edu.eci.dosw.ecifit.service;

import co.edu.eci.dosw.ecifit.model.MenuComedor;

import java.time.LocalDate;
import java.util.List;

public interface IMenuComedorService {

    MenuComedor publicar(MenuComedor menu);

    List<MenuComedor> obtenerPorFecha(LocalDate fecha);

    List<MenuComedor> obtenerHoy();
}
