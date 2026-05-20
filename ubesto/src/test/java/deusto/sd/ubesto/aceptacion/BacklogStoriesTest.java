package deusto.sd.ubesto.aceptacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import deusto.sd.ubesto.service.TripService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class BacklogStoriesTest {

    @Autowired private WebApplicationContext webContext;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private TripService tripService;

    private MockMvc mockMvc;
    private static final String RUN_ID = String.valueOf(System.currentTimeMillis());

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
    }

    @Test
    @DisplayName("[US07] Pasajero puede ver historial de viajes")
    void us07_historialPasajero() throws Exception {
        Long passengerId = createPassenger("historial-" + RUN_ID + "@ubesto.com");
        Long tripId = requestTrip(passengerId, "UBERX");

        mockMvc.perform(get("/passengers/" + passengerId + "/trips"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(tripId))
            .andExpect(jsonPath("$[0].estado").value("SOLICITADO"));
    }

    @Test
    @DisplayName("[US08] Pasajero puede cancelar viaje solicitado")
    void us08_cancelarViaje() throws Exception {
        Long passengerId = createPassenger("cancelar-" + RUN_ID + "@ubesto.com");
        Long tripId = requestTrip(passengerId, "UBERX");

        mockMvc.perform(post("/trips/" + tripId + "/cancel/passenger/" + passengerId)
                .param("reason", "Cambio de planes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("CANCELADO"))
            .andExpect(jsonPath("$.cancelReason").value("Cambio de planes"));

        mockMvc.perform(post("/trips/" + tripId + "/cancel/passenger/" + passengerId))
            .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("[US08] Pasajero puede eliminar cuenta y no volver a hacer login")
    void us08_eliminarCuentaPasajero() throws Exception {
        String email = "delete-passenger-" + RUN_ID + "@ubesto.com";
        Long passengerId = createPassenger(email);

        mockMvc.perform(delete("/passengers/" + passengerId))
            .andExpect(status().isOk());

        mockMvc.perform(post("/passengers/loginPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"email\":\"%s\",\"password\":\"pass123\"}", email)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("[US11 + US19] Valoración actualiza media y wallet muestra ganancias")
    void us11_us19_valoracionYWalletConductor() throws Exception {
        Long passengerId = createPassenger("rating-passenger-" + RUN_ID + "@ubesto.com");
        Long driverId = createDriver("rating-driver-" + RUN_ID + "@ubesto.com");
        createVehicle(driverId, "RAT-" + RUN_ID.substring(Math.max(0, RUN_ID.length() - 6)));
        Long tripId = requestTrip(passengerId, "BLACK");

        mockMvc.perform(post("/trips/" + tripId + "/accept/" + driverId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("ACEPTADO"));

        tripService.finishTrip(tripId);

        mockMvc.perform(post("/trips/" + tripId + "/rate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("{\"passengerId\":%d,\"estrellas\":5}", passengerId)))
            .andExpect(status().isOk());

        MvcResult wallet = mockMvc.perform(get("/drivers/" + driverId + "/wallet"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.viajesFinalizados").value(1))
            .andExpect(jsonPath("$.valoracionesRecibidas").value(1))
            .andExpect(jsonPath("$.calificacionMedia").value(5.0))
            .andReturn();

        JsonNode json = objectMapper.readTree(wallet.getResponse().getContentAsString());
        assertTrue(json.get("gananciasTotales").asDouble() > 0.0);
        assertEquals(json.get("gananciasTotales").asDouble(), json.get("saldo").asDouble(), 0.01);
    }

    @Test
    @DisplayName("[US10] Endpoints principales responden por debajo de umbral simple CI")
    void us10_rendimientoPeticionesBase() throws Exception {
        long start = System.currentTimeMillis();
        Long passengerId = createPassenger("perf-backlog-" + RUN_ID + "@ubesto.com");
        long registerElapsed = System.currentTimeMillis() - start;

        start = System.currentTimeMillis();
        requestTrip(passengerId, "UBERX");
        long requestElapsed = System.currentTimeMillis() - start;

        assertTrue(registerElapsed < 1500, "Registro tardó " + registerElapsed + "ms");
        assertTrue(requestElapsed < 1500, "Solicitud de viaje tardó " + requestElapsed + "ms");
    }

    private Long createPassenger(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/passengers/registerPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {
                      "nombre": "Test Passenger",
                      "email": "%s",
                      "password": "pass123",
                      "metodoPago": "tarjeta",
                      "posicionActual": {"latitud": 43.2630, "longitud": -2.9350}
                    }
                    """, email)))
            .andExpect(status().isCreated())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long createDriver(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/drivers/registerDriver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {
                      "nombre": "Test Driver",
                      "email": "%s",
                      "password": "pass123",
                      "licenciaConducir": "LIC-%s",
                      "calificacionMedia": 5.0,
                      "posicionActual": {"latitud": 43.2630, "longitud": -2.9350}
                    }
                    """, email, RUN_ID)))
            .andExpect(status().isCreated())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long createVehicle(Long driverId, String matricula) throws Exception {
        MvcResult result = mockMvc.perform(post("/vehicles/create/" + driverId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {
                      "matricula": "%s",
                      "marca": "Toyota",
                      "modelo": "Corolla",
                      "color": "Blanco",
                      "categoria": "UBERX"
                    }
                    """, matricula)))
            .andExpect(status().isCreated())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }

    private Long requestTrip(Long passengerId, String categoria) throws Exception {
        MvcResult result = mockMvc.perform(post("/trips/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format("""
                    {
                      "passengerId": %d,
                      "origen":  {"latitud": 43.2630, "longitud": -2.9350},
                      "destino": {"latitud": 43.3200, "longitud": -1.9800},
                      "categoria": "%s"
                    }
                    """, passengerId, categoria)))
            .andExpect(status().isCreated())
            .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
    }
}
