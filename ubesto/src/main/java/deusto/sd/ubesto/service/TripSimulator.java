package deusto.sd.ubesto.service;

/**
 * Simula el progreso de un viaje en un hilo separado.
 * Si el viaje se cancela mientras el hilo duerme, TripService no lo mueve a EN_CURSO/FINALIZADO.
 */
public class TripSimulator implements Runnable {

    private final Long tripId;
    private final TripService tripService;

    public TripSimulator(Long tripId, TripService tripService) {
        this.tripId = tripId;
        this.tripService = tripService;
    }

    @Override
    public void run() {
        try {
            System.out.println("THREAD: Iniciando simulación para el viaje ID: " + tripId);

            Thread.sleep(5000);
            tripService.startTrip(tripId);
            System.out.println("THREAD: Viaje ID: " + tripId + " intentó pasar a EN_CURSO.");

            Thread.sleep(10000);
            tripService.finishTrip(tripId);
            System.out.println("THREAD: Viaje ID: " + tripId + " intentó finalizar.");

        } catch (InterruptedException e) {
            System.err.println("THREAD: El hilo del viaje ID: " + tripId + " fue interrumpido.");
            Thread.currentThread().interrupt();
        }
    }
}
