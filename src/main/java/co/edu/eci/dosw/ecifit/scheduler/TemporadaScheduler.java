package co.edu.eci.dosw.ecifit.scheduler;

import co.edu.eci.dosw.ecifit.service.ITemporadaService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TemporadaScheduler {

    private final ITemporadaService temporadaService;

    @Scheduled(cron = "0 0 0 * * MON")
    public void ejecutarCorteSemanal() {
        temporadaService.recalcularLigasSemanal();
    }
}
