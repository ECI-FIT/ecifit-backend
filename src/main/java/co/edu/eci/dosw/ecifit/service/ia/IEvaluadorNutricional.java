package co.edu.eci.dosw.ecifit.service.ia;

import co.edu.eci.dosw.ecifit.exception.IaNoDisponibleException;
import co.edu.eci.dosw.ecifit.model.EvaluacionNutricional;
import co.edu.eci.dosw.ecifit.model.TipoComida;

/**
 * Puerto hacia el proveedor de IA que evalúa comidas. Permite cambiar de proveedor
 * (Gemini, DeepSeek, etc.) sin tocar la lógica de negocio.
 */
public interface IEvaluadorNutricional {

    /**
     * Evalúa si una comida es balanceada y genera un consejo.
     *
     * @throws IaNoDisponibleException si el proveedor no entrega una evaluación válida
     */
    EvaluacionNutricional evaluar(TipoComida tipo, String descripcion);
}
