package deusto.sd.ubesto.service;

/**
 * Esta clase simula el progreso de un viaje en un hilo separado.
 */
public class TripSimulator implements Runnable {

    private final Long tripId;
    private final TripService tripService; // Referencia al service para actualizar el estado

    public TripSimulator(Long tripId, TripService tripService) {
        this.tripId = tripId;
        this.tripService = tripService;
    }

    @Override
    public void run() {
        try {
            System.out.println("THREAD: Iniciando simulación para el viaje ID: " + tripId);

            // 1. Simula el tiempo que tarda el conductor en llegar al pasajero
            Thread.sleep(5000); // Espera 5 segundos

            // 2. Actualiza el estado a "EN_CURSO"
            tripService.startTrip(tripId);
            System.out.println("THREAD: Viaje ID: " + tripId + " ahora está EN_CURSO.");

            // 3. Simula la duración del viaje
            Thread.sleep(10000); // Espera 10 segundos

            // 4. Actualiza el estado a "FINALIZADO"
            tripService.finishTrip(tripId);
            System.out.println("THREAD: Viaje ID: " + tripId + " ha FINALIZADO.");

        } catch (InterruptedException e) {
            System.err.println("THREAD: El hilo del viaje ID: " + tripId + " fue interrumpido.");
            Thread.currentThread().interrupt();
        }
    }
}