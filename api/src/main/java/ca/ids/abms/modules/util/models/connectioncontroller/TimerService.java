package ca.ids.abms.modules.util.models.connectioncontroller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TimerService {
    ExecutorService service = Executors.newSingleThreadExecutor();

    public TimerService(StateMachine p_stateController, int p_warning_interval) {
        try {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(p_warning_interval);
                        if (p_stateController.getState().equals(ServiceState.WARNING)) {
                            p_stateController.do_action(ServiceOperations.CONNECTION_FAIL);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };

            service.submit(r);
        }
        finally {
            service.shutdown();
        }
    }
}
