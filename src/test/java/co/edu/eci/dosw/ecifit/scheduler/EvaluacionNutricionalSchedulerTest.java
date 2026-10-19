package co.edu.eci.dosw.ecifit.scheduler;

import co.edu.eci.dosw.ecifit.service.IComidaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EvaluacionNutricionalSchedulerTest {

    @Mock
    private IComidaService comidaService;
    @InjectMocks
    private EvaluacionNutricionalScheduler scheduler;

    @Test
    void debeReintentarLasEvaluacionesPendientes() {
        scheduler.reintentarEvaluacionesPendientes();

        verify(comidaService).reintentarPendientes();
    }
}
