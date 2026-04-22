package deusto.sd.ubesto.unitarios;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import deusto.sd.ubesto.dao.DriverRepository;
import deusto.sd.ubesto.dao.LoggedUserRepository;
import deusto.sd.ubesto.dao.PassengerRepository;
import deusto.sd.ubesto.dao.TripRepository;
import deusto.sd.ubesto.dao.VehicleRepository;
import deusto.sd.ubesto.dto.LoginDTO;
import deusto.sd.ubesto.dto.TripRequestDTO;
import deusto.sd.ubesto.entity.*;
import deusto.sd.ubesto.entity.Trip.EstadoViaje;
import deusto.sd.ubesto.entity.Vehicle.CategoriaVehiculo;
import deusto.sd.ubesto.service.DriverService;
import deusto.sd.ubesto.service.PassengerService;
import deusto.sd.ubesto.service.TripService;
import jakarta.persistence.EntityNotFoundException;

/**
 * TESTS UNITARIOS
 * No levanta Spring ni BD. Mockea todos los repositorios con Mockito.
 */
@ExtendWith(MockitoExtension.class)
public class UnitariosTest {

    // --- Mocks de repositorios ---
    @Mock private PassengerRepository passengerRepository;
    @Mock private DriverRepository driverRepository;
    @Mock private TripRepository tripRepository;
    @Mock private VehicleRepository vehicleRepository;
    @Mock private LoggedUserRepository loggedUserRepository;

    // --- Services bajo test ---
    @InjectMocks private PassengerService passengerService;
    @InjectMocks private DriverService driverService;
    @InjectMocks private TripService tripService;

    // --- Datos reutilizables ---
    private Passenger pasajero;
    private Driver conductor;
    private Vehicle vehiculo;
    private Trip tripSolicitado;

    @BeforeEach
    void setUp() {
        Posicion origen  = new Posicion(43.26, -2.93);
        Posicion destino = new Posicion(43.32, -1.98);

        pasajero = new Passenger(1L, "Ana", "ana@test.com", "pass123", origen, "efectivo");
        pasajero.setMonedero(100.0);

        conductor = new Driver(2L, "Carlos", "carlos@test.com", "pass123", "LIC-001", 5.0, 10L, origen);
        conductor.setMonedero(0.0);

        vehiculo = new Vehicle();
        vehiculo.setId(10L);
        vehiculo.setMatricula("1234-ABC");
        vehiculo.setCategoria(CategoriaVehiculo.UBERX);

        tripSolicitado = new Trip(1L, pasajero, null, null, origen, destino, 12.50, EstadoViaje.SOLICITADO);
    }

    // =========================================================
    // PassengerService - verificarPassword
    // =========================================================

    @Test
    @DisplayName("[U-01] Login pasajero: contraseña correcta devuelve true")
    void u01_verificarPassword_pasajero_correcto() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("ana@test.com");
        dto.setPassword("pass123");

        when(passengerRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(pasajero));

        assertTrue(passengerService.verificarPassword(dto));
    }

    @Test
    @DisplayName("[U-02] Login pasajero: contraseña incorrecta devuelve false")
    void u02_verificarPassword_pasajero_incorrecto() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("ana@test.com");
        dto.setPassword("wrongpass");

        when(passengerRepository.findByEmail("ana@test.com")).thenReturn(Optional.of(pasajero));

        assertFalse(passengerService.verificarPassword(dto));
    }

    // =========================================================
    // DriverService - verificarPassword
    // =========================================================

    @Test
    @DisplayName("[U-03] Login conductor: contraseña correcta devuelve true")
    void u03_verificarPassword_conductor_correcto() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("carlos@test.com");
        dto.setPassword("pass123");

        when(driverRepository.findByEmail("carlos@test.com")).thenReturn(Optional.of(conductor));

        assertTrue(driverService.verificarPassword(dto));
    }

    @Test
    @DisplayName("[U-04] Login conductor: contraseña incorrecta devuelve false")
    void u04_verificarPassword_conductor_incorrecto() {
        LoginDTO dto = new LoginDTO();
        dto.setEmail("carlos@test.com");
        dto.setPassword("wrongpass");

        when(driverRepository.findByEmail("carlos@test.com")).thenReturn(Optional.of(conductor));

        assertFalse(driverService.verificarPassword(dto));
    }

    // =========================================================
    // TripService - requestTrip
    // =========================================================

    @Test
    @DisplayName("[U-05] requestTrip: crea viaje correctamente con pasajero existente")
    void u05_requestTrip_pasajeroExiste_creaViaje() {
        TripRequestDTO dto = buildTripDTO();

        when(passengerRepository.findById(1L)).thenReturn(Optional.of(pasajero));
        when(tripRepository.save(any(Trip.class))).thenAnswer(i -> i.getArgument(0));

        Trip result = tripService.requestTrip(dto);

        assertNotNull(result);
        assertEquals(EstadoViaje.SOLICITADO, result.getEstado());
        assertEquals(pasajero, result.getCliente());
        verify(tripRepository, times(1)).save(any(Trip.class));
    }

    @Test
    @DisplayName("[U-06] requestTrip: lanza EntityNotFoundException si el pasajero no existe")
    void u06_requestTrip_pasajeroNoExiste_lanzaExcepcion() {
        TripRequestDTO dto = buildTripDTO();
        dto.setPassengerId(99L);

        when(passengerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> tripService.requestTrip(dto));
        verify(tripRepository, never()).save(any());
    }

    // =========================================================
    // TripService - acceptTrip
    // =========================================================

    @Test
    @DisplayName("[U-07] acceptTrip: acepta viaje correctamente y cambia estado a ACEPTADO")
    void u07_acceptTrip_correcto() {
        when(tripRepository.findAll()).thenReturn(Collections.emptyList());
        when(tripRepository.findById(1L)).thenReturn(Optional.of(tripSolicitado));
        when(driverRepository.findById(2L)).thenReturn(Optional.of(conductor));
        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(vehiculo));
        when(tripRepository.save(any(Trip.class))).thenAnswer(i -> i.getArgument(0));

        Trip result = tripService.acceptTrip(1L, 2L);

        assertEquals(EstadoViaje.ACEPTADO, result.getEstado());
        assertEquals(conductor, result.getConductor());
        assertEquals(vehiculo, result.getVehiculo());
    }

    @Test
    @DisplayName("[U-08] acceptTrip: lanza excepción si el viaje no está en estado SOLICITADO")
    void u08_acceptTrip_estadoIncorrecto_lanzaExcepcion() {
        tripSolicitado.setEstado(EstadoViaje.ACEPTADO);
        when(tripRepository.findAll()).thenReturn(Collections.emptyList());
        when(tripRepository.findById(1L)).thenReturn(Optional.of(tripSolicitado));

        assertThrows(IllegalStateException.class, () -> tripService.acceptTrip(1L, 2L));
    }

    @Test
    @DisplayName("[U-09] acceptTrip: lanza excepción si el conductor ya tiene un viaje activo")
    void u09_acceptTrip_conductorOcupado_lanzaExcepcion() {
        Trip tripActivo = new Trip(2L, pasajero, conductor, vehiculo,
            new Posicion(0, 0), new Posicion(1, 1), 10.0, EstadoViaje.EN_CURSO);

        when(tripRepository.findAll()).thenReturn(Collections.singletonList(tripActivo));

        assertThrows(IllegalStateException.class, () -> tripService.acceptTrip(1L, 2L));
    }

    @Test
    @DisplayName("[U-10] acceptTrip: lanza excepción si el conductor no tiene vehículo asignado")
    void u10_acceptTrip_sinVehiculo_lanzaExcepcion() {
        conductor.setVehicleActiveId(null);
        when(tripRepository.findAll()).thenReturn(Collections.emptyList());
        when(tripRepository.findById(1L)).thenReturn(Optional.of(tripSolicitado));
        when(driverRepository.findById(2L)).thenReturn(Optional.of(conductor));

        assertThrows(EntityNotFoundException.class, () -> tripService.acceptTrip(1L, 2L));
    }

    // =========================================================
    // TripService - finishTrip
    // =========================================================

    @Test
    @DisplayName("[U-11] finishTrip: transfiere dinero y actualiza posición al finalizar")
    void u11_finishTrip_transfiereMonederoYPosicion() {
        tripSolicitado.setConductor(conductor);
        tripSolicitado.setPrecio(12.50);

        when(tripRepository.findById(1L)).thenReturn(Optional.of(tripSolicitado));
        when(passengerRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(driverRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(tripRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        tripService.finishTrip(1L);

        assertEquals(EstadoViaje.FINALIZADO, tripSolicitado.getEstado());
        assertEquals(87.50, pasajero.getMonedero(), 0.001);  // 100 - 12.50
        assertEquals(12.50, conductor.getMonedero(), 0.001); // 0   + 12.50
        assertEquals(tripSolicitado.getPosicionDestino(), pasajero.getPosicionActual());
        assertEquals(tripSolicitado.getPosicionDestino(), conductor.getPosicionActual());
    }

    // =========================================================
    // Helpers
    // =========================================================

    private TripRequestDTO buildTripDTO() {
        TripRequestDTO dto = new TripRequestDTO();
        dto.setPassengerId(1L);
        dto.setOrigen(new Posicion(43.26, -2.93));
        dto.setDestino(new Posicion(43.32, -1.98));
        dto.setCategoria(CategoriaVehiculo.UBERX);
        return dto;
    }
}