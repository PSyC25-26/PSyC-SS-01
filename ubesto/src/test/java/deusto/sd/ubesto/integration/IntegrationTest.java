package deusto.sd.ubesto.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import deusto.sd.ubesto.dao.PassengerRepository;
import tools.jackson.databind.ObjectMapper;
import deusto.sd.ubesto.dao.DriverRepository;

/**
 * TESTS DE INTEGRACIÓN
 * Levanta Spring Boot completo con H2 en memoria.
 * Usa MockMvc en lugar de HttpClient — no requiere servidor externo.
 * Cada test es @Transactional para que no ensucie la BD entre tests.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@Transactional
public class IntegrationTest {

    @Autowired private ObjectMapper objectMapper;
    @Autowired private PassengerRepository passengerRepository;
    @Autowired private DriverRepository driverRepository;
    private MockMvc mockMvc; // 1. Quitamos el @Autowired de aquí porque lo crearemos a mano

    @Autowired
    private WebApplicationContext webContext; // 2. Necesitamos el contexto para fabricar MockMvc

    @BeforeEach
    void setup() {
        // Con esto creamos el Mock
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
    }

    // =========================================================
    // Passengers
    // =========================================================

    @Test
    @DisplayName("[I-01] POST /passengers/registerPassenger → 201 y pasajero guardado en BD")
    void i01_testCreatePassenger() throws Exception {
        String jsonBody = """
            {
              "nombre": "David3",
              "email": "david3@gmail.com",
              "password": "1234",
              "metodoPago": "efectivo",
              "posicionActual": {"latitud": 0.0, "longitud": 0.0}
            }
            """;

        MvcResult result = mockMvc.perform(post("/passengers/registerPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("david3@gmail.com"))
            .andReturn();

        // Verifica que realmente se guardó en H2
        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        assertTrue(passengerRepository.findById(id).isPresent());
    }

    @Test
    @DisplayName("[I-02] POST /passengers/registerPassenger → 406 si el email ya existe")
    void i02_registroPasajero_emailDuplicado_devuelve406() throws Exception {
        String jsonBody = """
            {
              "nombre": "Duplicado",
              "email": "duplicado@gmail.com",
              "password": "1234",
              "metodoPago": "efectivo",
              "posicionActual": {"latitud": 0.0, "longitud": 0.0}
            }""";

        // Primer registro: OK
        mockMvc.perform(post("/passengers/registerPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isCreated());

        // Segundo registro con mismo email: debe fallar
        mockMvc.perform(post("/passengers/registerPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isNotAcceptable());
    }

    @Test
    @DisplayName("[I-03] POST /passengers/loginPassenger → 200 con credenciales correctas")
    void i03_loginPasajero_credencialesCorrectas_devuelve200() throws Exception {
        // Primero registramos
        mockMvc.perform(post("/passengers/registerPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre": "Login Test",
                      "email": "login@test.com",
                      "password": "mipass",
                      "metodoPago": "efectivo",
                      "posicionActual": {"latitud": 0.0, "longitud": 0.0}
                    }
                    """))
            .andExpect(status().isCreated());

        // Luego hacemos login
        mockMvc.perform(post("/passengers/loginPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"login@test.com\", \"password\":\"mipass\"}"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("[I-04] POST /passengers/loginPassenger → 401 con contraseña incorrecta")
    void i04_loginPasajero_passIncorrecta_devuelve401() throws Exception {
        mockMvc.perform(post("/passengers/registerPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre": "Bad Login",
                      "email": "badlogin@test.com",
                      "password": "correcta",
                      "metodoPago": "efectivo",
                      "posicionActual": {"latitud": 0.0, "longitud": 0.0}
                    }
                    """))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/passengers/loginPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\":\"badlogin@test.com\", \"password\":\"incorrecta\"}"))
            .andExpect(status().isUnauthorized());
    }

    // =========================================================
    // Drivers
    // =========================================================

    @Test
    @DisplayName("[I-05] POST /drivers/registerDriver → 201 y conductor guardado en BD")
    void i05_registroConductor_correcto_devuelve201() throws Exception {
        String jsonBody = """
            {
              "nombre": "Carlos",
              "email": "carlos@test.com",
              "password": "1234",
              "licenciaConducir": "LIC-001",
              "calificacionMedia": 5.0,
              "posicionActual": {"latitud": 0.0, "longitud": 0.0}
            }
            """;

        MvcResult result = mockMvc.perform(post("/drivers/registerDriver")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.licenciaConducir").value("LIC-001"))
            .andReturn();

        Long id = objectMapper.readTree(result.getResponse().getContentAsString()).get("id").asLong();
        assertTrue(driverRepository.findById(id).isPresent());
    }

    // @Test
    // @DisplayName("[I-06] POST /drivers/loginDriver → 200 con credenciales correctas")
    // void i06_loginConductor_correcto_devuelve200() throws Exception {
    //     mockMvc.perform(post("/drivers/registerDriver")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content("""
    //                 {
    //                   "nombre": "Login Driver",
    //                   "email": "logindriver@test.com",
    //                   "password": "driverpass",
    //                   "licenciaConducir": "LIC-999",
    //                   "calificacionMedia": 5.0,
    //                   "posicionActual": {"latitud": 0.0, "longitud": 0.0}
    //                 }
    //                 """))
    //         .andExpect(status().isCreated());

    //     mockMvc.perform(post("/drivers/loginDriver")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content("{\"email\":\"logindriver@test.com\", \"password\":\"driverpass\"}"))
    //         .andExpect(status().isOk())
    //         .andExpect(content().string(org.hamcrest.Matchers.matchesPattern("\\d+")));
    // }

    // =========================================================
    // Trips
    // =========================================================

    @Test
    @DisplayName("[I-07] POST /trips/request → 201 cuando el pasajero existe")
    void i07_solicitarViaje_pasajeroExiste_devuelve201() throws Exception {
        // Creamos pasajero primero
        MvcResult reg = mockMvc.perform(post("/passengers/registerPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nombre": "Viajero",
                      "email": "viajero@test.com",
                      "password": "1234",
                      "metodoPago": "efectivo",
                      "posicionActual": {"latitud": 0.0, "longitud": 0.0}
                    }
                    """))
            .andExpect(status().isCreated())
            .andReturn();

        Long idPasajero = objectMapper.readTree(reg.getResponse().getContentAsString()).get("id").asLong();

        String tripBody = String.format("""
            {
              "passengerId": %d,
              "origen": {"latitud": 43.26, "longitud": -2.93},
              "destino": {"latitud": 43.32, "longitud": -1.98},
              "categoria": "UBERX"
            }
            """, idPasajero);

        mockMvc.perform(post("/trips/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tripBody))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.estado").value("SOLICITADO"))
            .andExpect(jsonPath("$.precio").isNumber());
    }

    @Test
    @DisplayName("[I-08] POST /trips/request → 404 si el pasajero no existe")
    void i08_solicitarViaje_pasajeroInexistente_devuelve404() throws Exception {
        String tripBody = """
            {
              "passengerId": 9999,
              "origen": {"latitud": 43.26, "longitud": -2.93},
              "destino": {"latitud": 43.32, "longitud": -1.98},
              "categoria": "UBERX"
            }
            """;

        mockMvc.perform(post("/trips/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tripBody))
            .andExpect(status().isNotFound());
    }
}