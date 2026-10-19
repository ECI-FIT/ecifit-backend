package co.edu.eci.dosw.ecifit.scheduler;

import co.edu.eci.dosw.ecifit.service.IComidaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EvaluacionNutricionalScheduler {

    private final IComidaService comidaService;

    @Scheduled(fixedDelayString = "${ia.gemini.reintento-ms:900000}",
            initialDelayString = "${ia.gemini.reintento-ms:900000}")
    public void reintentarEvaluacionesPendientes() {
        comidaService.reintentarPendientes();
    }
}
