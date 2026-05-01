package deusto.sd.ubesto.aceptacion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TESTS DE ACEPTACIÓN
 *
 * Validan flujos de negocio completos tal como los experimentaría un usuario real.
 * Cada escenario cubre un caso de uso de extremo a extremo (end-to-end).
 *
 * Escenarios cubiertos:
 *   A-01: Registro completo de un pasajero nuevo
 *   A-02: Registro duplicado es rechazado
 *   A-03: Ciclo completo de login de pasajero
 *   A-04: Ciclo completo de login de conductor
 *   A-05: Registro de conductor con vehículo
 *   A-06: Solicitud de viaje por pasajero registrado
 *   A-07: Flujo completo: conductor acepta viaje solicitado
 *   A-08: Conductor no puede aceptar viaje inexistente (404)
 *   A-09: Viaje no puede ser aceptado dos veces (409 Conflict)
 *   A-10: Actualización de datos de pasajero
 *   A-11: Actualización de datos de conductor
 *   A-12: Actualización de conductor inexistente devuelve 404
 *   A-13: Solicitud de viaje con categoría BLACK calcula precio mayor
 *   A-14: Solicitud de viaje con pasajero inexistente devuelve 404
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AceptacionTest {

    @Autowired
    private WebApplicationContext webContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    // IDs compartidos entre tests del mismo escenario
    private static Long pasajeroId;
    private static Long conductorId;
    private static Long vehiculoId;
    private static Long viajeId;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
    }

    // =========================================================
    // ESCENARIO 1: Gestión de pasajeros
    // =========================================================

    @Test
    @Order(1)
    @DisplayName("[A-01] Un nuevo pasajero puede registrarse correctamente")
    void a01_registroPasajero_nuevo_devuelve201YPersiste() throws Exception {
        String body = """
                {
                  "nombre": "María García",
                  "email": "maria.garcia@ubesto.com",
                  "password": "securePass1",
                  "metodoPago": "tarjeta",
                  "posicionActual": {"latitud": 43.2630, "longitud": -2.9350}
                }
                """;

        MvcResult result = mockMvc.perform(post("/passengers/registerPassenger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("maria.garcia@ubesto.com"))
                .andExpect(jsonPath("$.nombre").value("María García"))
                .andExpect(jsonPath("$.id").isNumber())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        pasajeroId = json.get("id").asLong();
        assertNotNull(pasajeroId, "El ID asignado al pasajero no debe ser null");
    }

    @Test
    @Order(2)
    @DisplayName("[A-02] Registrar un pasajero con email ya existente devuelve 406")
    void a02_registroPasajero_emailDuplicado_devuelve406() throws Exception {
        String body = """
                {
                  "nombre": "Otro Usuario",
                  "email": "maria.garcia@ubesto.com",
                  "password": "otraPass",
                  "metodoPago": "efectivo",
                  "posicionActual": {"latitud": 0.0, "longitud": 0.0}
                }
                """;

        // El email ya existe (registrado en A-01)
        mockMvc.perform(post("/passengers/registerPassenger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    @Order(3)
    @DisplayName("[A-03] Un pasajero registrado puede hacer login y recibe su ID")
    void a03_loginPasajero_credencialesCorrectas_devuelveId() throws Exception {
        // Registrar primero por si el test se ejecuta de forma aislada
        mockMvc.perform(post("/passengers/registerPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nombre": "LoginUser",
                          "email": "loginuser@ubesto.com",
                          "password": "mypass123",
                          "metodoPago": "efectivo",
                          "posicionActual": {"latitud": 0.0, "longitud": 0.0}
                        }
                        """));

        MvcResult result = mockMvc.perform(post("/passengers/loginPassenger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "loginuser@ubesto.com", "password": "mypass123"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        long idFromLogin = Long.parseLong(result.getResponse().getContentAsString().trim());
        assertTrue(idFromLogin > 0, "El ID devuelto en login debe ser positivo");
    }

    @Test
    @Order(4)
    @DisplayName("[A-10] Un pasajero puede actualizar su nombre y método de pago")
    void a10_actualizarPasajero_datosValidos_devuelve200() throws Exception {
        assertNotNull(pasajeroId, "Necesita que A-01 haya creado el pasajero");

        String updateBody = String.format("""
                {
                  "nombre": "María G. Actualizada",
                  "password": "newPass456",
                  "metodoPago": "bizum"
                }
                """);

        mockMvc.perform(put("/passengers/update/" + pasajeroId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody))
                .andExpect(status().isOk());
    }

    // =========================================================
    // ESCENARIO 2: Gestión de conductores y vehículos
    // =========================================================

    @Test
    @Order(10)
    @DisplayName("[A-04] Un nuevo conductor puede registrarse correctamente")
    void a04_registroConductor_nuevo_devuelve201() throws Exception {
        String body = """
                {
                  "nombre": "Pedro Rodríguez",
                  "email": "pedro.rod@ubesto.com",
                  "password": "driverPass99",
                  "licenciaConducir": "B-12345678",
                  "calificacionMedia": 4.8,
                  "posicionActual": {"latitud": 43.2630, "longitud": -2.9350}
                }
                """;

        MvcResult result = mockMvc.perform(post("/drivers/registerDriver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licenciaConducir").value("B-12345678"))
                .andExpect(jsonPath("$.nombre").value("Pedro Rodríguez"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        conductorId = json.get("id").asLong();
        assertNotNull(conductorId);
    }

    @Test
    @Order(11)
    @DisplayName("[A-04b] Un conductor registrado puede hacer login")
    void a04b_loginConductor_credencialesCorrectas_devuelveId() throws Exception {
        assertNotNull(conductorId, "Necesita que A-04 haya creado el conductor");

        MvcResult result = mockMvc.perform(post("/drivers/loginDriver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "pedro.rod@ubesto.com", "password": "driverPass99"}
                                """))
                .andExpect(status().isOk())
                .andReturn();

        long idFromLogin = Long.parseLong(result.getResponse().getContentAsString().trim());
        assertEquals(conductorId, idFromLogin, "El ID devuelto en login debe coincidir con el del registro");
    }

    @Test
    @Order(12)
    @DisplayName("[A-05] Un conductor puede añadir un vehículo a su perfil")
    void a05_registroVehiculo_conductorExiste_devuelve201() throws Exception {
        assertNotNull(conductorId, "Necesita que A-04 haya creado el conductor");

        String vehicleBody = """
                {
                  "matricula": "1234-BCN",
                  "marca": "Toyota",
                  "modelo": "Corolla",
                  "color": "Blanco",
                  "categoria": "UBERX"
                }
                """;

        MvcResult result = mockMvc.perform(post("/vehicles/create/" + conductorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(vehicleBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.matricula").value("1234-BCN"))
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        vehiculoId = json.get("id").asLong();
        assertNotNull(vehiculoId);
    }

    @Test
    @Order(13)
    @DisplayName("[A-11] Un conductor puede actualizar su nombre y licencia")
    void a11_actualizarConductor_datosValidos_devuelve200() throws Exception {
        assertNotNull(conductorId, "Necesita que A-04 haya creado el conductor");

        mockMvc.perform(put("/drivers/update/" + conductorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Pedro R. Pro",
                                  "licenciaConducir": "B-99999999"
                                }
                                """))
                .andExpect(status().isOk());
    }

    @Test
    @Order(14)
    @DisplayName("[A-12] Actualizar un conductor inexistente devuelve 404")
    void a12_actualizarConductor_noExiste_devuelve404() throws Exception {
        mockMvc.perform(put("/drivers/update/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Nadie"}
                                """))
                .andExpect(status().isNotFound());
    }

    // =========================================================
    // ESCENARIO 3: Flujo completo de viaje
    // =========================================================

    @Test
    @Order(20)
    @DisplayName("[A-06] Un pasajero registrado puede solicitar un viaje (UBERX)")
    void a06_solicitarViaje_pasajeroExiste_devuelve201ConPrecio() throws Exception {
        assertNotNull(pasajeroId, "Necesita que A-01 haya creado el pasajero");

        String tripBody = String.format("""
                {
                  "passengerId": %d,
                  "origen":  {"latitud": 43.2630, "longitud": -2.9350},
                  "destino": {"latitud": 43.3200, "longitud": -1.9800},
                  "categoria": "UBERX"
                }
                """, pasajeroId);

        MvcResult result = mockMvc.perform(post("/trips/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tripBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("SOLICITADO"))
                .andExpect(jsonPath("$.precio").isNumber())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        viajeId = json.get("id").asLong();
        double precio = json.get("precio").asDouble();

        assertNotNull(viajeId);
        assertTrue(precio > 0, "El precio debe ser mayor que 0");
    }

    @Test
    @Order(21)
    @DisplayName("[A-13] Viaje con categoría BLACK tiene precio mayor que con UBERX")
    void a13_solicitarViaje_categoriaBLACK_precioMayor() throws Exception {
        assertNotNull(pasajeroId, "Necesita que A-01 haya creado el pasajero");

        String bodyUberX = String.format("""
                {
                  "passengerId": %d,
                  "origen":  {"latitud": 43.2630, "longitud": -2.9350},
                  "destino": {"latitud": 43.3200, "longitud": -1.9800},
                  "categoria": "UBERX"
                }
                """, pasajeroId);

        String bodyBlack = String.format("""
                {
                  "passengerId": %d,
                  "origen":  {"latitud": 43.2630, "longitud": -2.9350},
                  "destino": {"latitud": 43.3200, "longitud": -1.9800},
                  "categoria": "BLACK"
                }
                """, pasajeroId);

        MvcResult rUberX = mockMvc.perform(post("/trips/request")
                        .contentType(MediaType.APPLICATION_JSON).content(bodyUberX))
                .andExpect(status().isCreated()).andReturn();

        MvcResult rBlack = mockMvc.perform(post("/trips/request")
                        .contentType(MediaType.APPLICATION_JSON).content(bodyBlack))
                .andExpect(status().isCreated()).andReturn();

        double precioUberX = objectMapper.readTree(rUberX.getResponse().getContentAsString()).get("precio").asDouble();
        double precioBlack = objectMapper.readTree(rBlack.getResponse().getContentAsString()).get("precio").asDouble();

        assertTrue(precioBlack > precioUberX,
                "Precio BLACK (" + precioBlack + ") debe ser mayor que UBERX (" + precioUberX + ")");
    }

    @Test
    @Order(22)
    @DisplayName("[A-07] Un conductor con vehículo puede aceptar un viaje SOLICITADO")
    void a07_aceptarViaje_conductorConVehiculo_devuelve200ACEPTADO() throws Exception {
        assertNotNull(viajeId, "Necesita que A-06 haya creado el viaje");
        assertNotNull(conductorId, "Necesita que A-04 haya creado el conductor");
        assertNotNull(vehiculoId, "Necesita que A-05 haya creado el vehículo");

        MvcResult result = mockMvc.perform(post("/trips/" + viajeId + "/accept/" + conductorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACEPTADO"))
                .andExpect(jsonPath("$.conductor").isNotEmpty())
                .andReturn();

        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertEquals("ACEPTADO", json.get("estado"));
    }

    @Test
    @Order(23)
    @DisplayName("[A-09] Aceptar un viaje ya ACEPTADO devuelve 409 Conflict")
    void a09_aceptarViajeDosVeces_devuelve409() throws Exception {
        assertNotNull(viajeId, "Necesita que A-07 haya aceptado el viaje");

        // Registrar un segundo conductor para que no falle por "conductor ocupado"
        MvcResult regResult = mockMvc.perform(post("/drivers/registerDriver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Segundo Conductor",
                                  "email": "segundo@ubesto.com",
                                  "password": "pass",
                                  "licenciaConducir": "C-00000001",
                                  "calificacionMedia": 4.0,
                                  "posicionActual": {"latitud": 0.0, "longitud": 0.0}
                                }
                                """))
                .andExpect(status().isCreated())
                .andReturn();

        Long segundoConductorId = objectMapper.readTree(regResult.getResponse().getContentAsString()).get("id").asLong();

        // Añadir vehículo al segundo conductor
        mockMvc.perform(post("/vehicles/create/" + segundoConductorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"matricula":"9999-ZZZ","marca":"Seat","modelo":"Ibiza","color":"Rojo","categoria":"UBERX"}
                                """))
                .andExpect(status().isCreated());

        // El viaje ya está en ACEPTADO → debe devolver 409
        mockMvc.perform(post("/trips/" + viajeId + "/accept/" + segundoConductorId))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(24)
    @DisplayName("[A-08] Aceptar un viaje inexistente devuelve 404")
    void a08_aceptarViaje_noExiste_devuelve404() throws Exception {
        assertNotNull(conductorId, "Necesita que A-04 haya creado el conductor");

        mockMvc.perform(post("/trips/999999/accept/" + conductorId))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(25)
    @DisplayName("[A-14] Solicitar viaje con pasajero inexistente devuelve 404")
    void a14_solicitarViaje_pasajeroNoExiste_devuelve404() throws Exception {
        String tripBody = """
                {
                  "passengerId": 999999,
                  "origen":  {"latitud": 43.2630, "longitud": -2.9350},
                  "destino": {"latitud": 43.3200, "longitud": -1.9800},
                  "categoria": "UBERX"
                }
                """;

        mockMvc.perform(post("/trips/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tripBody))
                .andExpect(status().isNotFound());
    }
}
