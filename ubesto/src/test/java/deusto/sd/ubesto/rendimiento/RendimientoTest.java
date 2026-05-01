package deusto.sd.ubesto.rendimiento;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * TESTS DE RENDIMIENTO
 *
 * Validan que los endpoints cumplen umbrales de tiempo aceptables
 * y que el sistema responde correctamente bajo carga concurrente.
 *
 * Umbrales definidos (conservadores para entorno CI/CD con H2 en memoria):
 *   - Registro individual:   < 500 ms
 *   - Login individual:      < 300 ms
 *   - Solicitar viaje:       < 400 ms
 *   - 50 registros en serie: < 5 000 ms total
 *   - 10 peticiones paralelas: sin errores de servidor (5xx)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class RendimientoTest {

    @Autowired
    private WebApplicationContext webContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    // Umbrales en milisegundos
    private static final long MAX_MS_REGISTRO       = 500L;
    private static final long MAX_MS_LOGIN          = 300L;
    private static final long MAX_MS_SOLICITAR      = 400L;
    private static final long MAX_MS_SERIE_50       = 5_000L;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webContext).build();
    }

    // =========================================================
    // R-01: Tiempo de registro de pasajero
    // =========================================================

    @Test
    @DisplayName("[R-01] POST /passengers/registerPassenger responde en menos de " + MAX_MS_REGISTRO + " ms")
    void r01_registroPasajero_tiempoRespuesta() throws Exception {
        String body = passengerBody("r01user@perf.com");

        long inicio = System.currentTimeMillis();
        mockMvc.perform(post("/passengers/registerPassenger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        long elapsed = System.currentTimeMillis() - inicio;

        System.out.printf("[R-01] Registro pasajero: %d ms%n", elapsed);
        assertTrue(elapsed < MAX_MS_REGISTRO,
                "Registro tardó " + elapsed + " ms, límite: " + MAX_MS_REGISTRO + " ms");
    }

    // =========================================================
    // R-02: Tiempo de login de pasajero
    // =========================================================

    @Test
    @DisplayName("[R-02] POST /passengers/loginPassenger responde en menos de " + MAX_MS_LOGIN + " ms")
    void r02_loginPasajero_tiempoRespuesta() throws Exception {
        // Pre-condición: registrar usuario
        String email = "r02login@perf.com";
        mockMvc.perform(post("/passengers/registerPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(passengerBody(email)));

        String loginBody = "{\"email\":\"" + email + "\",\"password\":\"perfpass\"}";

        // Calentar JVM con una petición previa
        mockMvc.perform(post("/passengers/loginPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginBody));

        long inicio = System.currentTimeMillis();
        mockMvc.perform(post("/passengers/loginPassenger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk());
        long elapsed = System.currentTimeMillis() - inicio;

        System.out.printf("[R-02] Login pasajero: %d ms%n", elapsed);
        assertTrue(elapsed < MAX_MS_LOGIN,
                "Login tardó " + elapsed + " ms, límite: " + MAX_MS_LOGIN + " ms");
    }

    // =========================================================
    // R-03: Tiempo de registro de conductor
    // =========================================================

    @Test
    @DisplayName("[R-03] POST /drivers/registerDriver responde en menos de " + MAX_MS_REGISTRO + " ms")
    void r03_registroConductor_tiempoRespuesta() throws Exception {
        String body = driverBody("r03driver@perf.com", "LIC-R03");

        long inicio = System.currentTimeMillis();
        mockMvc.perform(post("/drivers/registerDriver")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
        long elapsed = System.currentTimeMillis() - inicio;

        System.out.printf("[R-03] Registro conductor: %d ms%n", elapsed);
        assertTrue(elapsed < MAX_MS_REGISTRO,
                "Registro conductor tardó " + elapsed + " ms, límite: " + MAX_MS_REGISTRO + " ms");
    }

    // =========================================================
    // R-04: Tiempo de solicitud de viaje
    // =========================================================

    @Test
    @DisplayName("[R-04] POST /trips/request responde en menos de " + MAX_MS_SOLICITAR + " ms")
    void r04_solicitarViaje_tiempoRespuesta() throws Exception {
        // Pre-condición: crear pasajero
        String email = "r04trip@perf.com";
        var regResult = mockMvc.perform(post("/passengers/registerPassenger")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(passengerBody(email)))
                .andExpect(status().isCreated())
                .andReturn();
        Long pid = objectMapper.readTree(regResult.getResponse().getContentAsString()).get("id").asLong();

        String tripBody = tripBody(pid, "UBERX");

        long inicio = System.currentTimeMillis();
        mockMvc.perform(post("/trips/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(tripBody))
                .andExpect(status().isCreated());
        long elapsed = System.currentTimeMillis() - inicio;

        System.out.printf("[R-04] Solicitar viaje: %d ms%n", elapsed);
        assertTrue(elapsed < MAX_MS_SOLICITAR,
                "Solicitar viaje tardó " + elapsed + " ms, límite: " + MAX_MS_SOLICITAR + " ms");
    }

    // =========================================================
    // R-05: 50 registros de pasajeros en serie
    // =========================================================

    @Test
    @DisplayName("[R-05] 50 registros de pasajeros en serie en menos de " + MAX_MS_SERIE_50 + " ms total")
    void r05_registros_serie_rendimiento() throws Exception {
        int N = 50;

        long inicio = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            mockMvc.perform(post("/passengers/registerPassenger")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(passengerBody("r05serie" + i + "@perf.com")))
                    .andExpect(status().isCreated());
        }
        long total = System.currentTimeMillis() - inicio;

        System.out.printf("[R-05] %d registros en serie: %d ms (media: %.1f ms/req)%n",
                N, total, (double) total / N);
        assertTrue(total < MAX_MS_SERIE_50,
                N + " registros tardaron " + total + " ms, límite: " + MAX_MS_SERIE_50 + " ms");
    }

    // =========================================================
    // R-06: Media de latencia de 20 logins consecutivos
    // =========================================================

    @Test
    @DisplayName("[R-06] Media de 20 logins consecutivos inferior a " + MAX_MS_LOGIN + " ms/petición")
    void r06_logins_consecutivos_media() throws Exception {
        String email = "r06batch@perf.com";
        mockMvc.perform(post("/passengers/registerPassenger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(passengerBody(email)));

        String loginBody = "{\"email\":\"" + email + "\",\"password\":\"perfpass\"}";
        int N = 20;
        List<Long> tiempos = new ArrayList<>();

        for (int i = 0; i < N; i++) {
            long t = System.currentTimeMillis();
            mockMvc.perform(post("/passengers/loginPassenger")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginBody))
                    .andExpect(status().isOk());
            tiempos.add(System.currentTimeMillis() - t);
        }

        double media = tiempos.stream().mapToLong(Long::longValue).average().orElse(0);
        System.out.printf("[R-06] Media de %d logins: %.1f ms%n", N, media);
        assertTrue(media < MAX_MS_LOGIN,
                "Media de login fue " + media + " ms, límite: " + MAX_MS_LOGIN + " ms");
    }

    // =========================================================
    // R-07: Concurrencia — 10 registros simultáneos
    // =========================================================

    @Test
    @DisplayName("[R-07] 10 registros simultáneos no generan errores 5xx")
    void r07_registros_concurrentes_sinErrores() throws Exception {
        int N = 10;
        ExecutorService executor = Executors.newFixedThreadPool(N);
        CountDownLatch latch = new CountDownLatch(N);
        AtomicInteger errores5xx = new AtomicInteger(0);
        AtomicInteger exitos = new AtomicInteger(0);

        long inicio = System.currentTimeMillis();
        for (int i = 0; i < N; i++) {
            final int idx = i;
            executor.submit(() -> {
                try {
                    var result = mockMvc.perform(post("/passengers/registerPassenger")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(passengerBody("r07concurrent" + idx + "@perf.com")))
                            .andReturn();
                    int status = result.getResponse().getStatus();
                    if (status >= 500) errores5xx.incrementAndGet();
                    else if (status == 201) exitos.incrementAndGet();
                } catch (Exception e) {
                    errores5xx.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();
        long elapsed = System.currentTimeMillis() - inicio;

        System.out.printf("[R-07] %d registros concurrentes: %d ms | OK=%d | Errores5xx=%d%n",
                N, elapsed, exitos.get(), errores5xx.get());

        assertEquals(0, errores5xx.get(),
                "No debe haber errores 5xx en registros concurrentes, pero hubo: " + errores5xx.get());
        assertEquals(N, exitos.get(),
                "Todos los registros deben completarse con 201, pero solo " + exitos.get() + " lo hicieron");
    }

    // =========================================================
    // R-08: Concurrencia — varios pasajeros solicitan viaje a la vez
    // =========================================================

    @Test
    @DisplayName("[R-08] 5 solicitudes de viaje concurrentes no generan errores 5xx")
    void r08_solicitudesViaje_concurrentes_sinErrores() throws Exception {
        int N = 5;
        Long[] pasajeroIds = new Long[N];

        // Crear N pasajeros previamente
        for (int i = 0; i < N; i++) {
            var r = mockMvc.perform(post("/passengers/registerPassenger")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(passengerBody("r08viaje" + i + "@perf.com")))
                    .andReturn();
            pasajeroIds[i] = objectMapper.readTree(r.getResponse().getContentAsString()).get("id").asLong();
        }

        ExecutorService executor = Executors.newFixedThreadPool(N);
        CountDownLatch latch = new CountDownLatch(N);
        AtomicInteger errores5xx = new AtomicInteger(0);
        AtomicInteger creados = new AtomicInteger(0);

        for (int i = 0; i < N; i++) {
            final Long pid = pasajeroIds[i];
            executor.submit(() -> {
                try {
                    var result = mockMvc.perform(post("/trips/request")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(tripBody(pid, "UBERX")))
                            .andReturn();
                    int status = result.getResponse().getStatus();
                    if (status >= 500) errores5xx.incrementAndGet();
                    else if (status == 201) creados.incrementAndGet();
                } catch (Exception e) {
                    errores5xx.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        System.out.printf("[R-08] %d viajes concurrentes | Creados=%d | Errores5xx=%d%n",
                N, creados.get(), errores5xx.get());

        assertEquals(0, errores5xx.get(), "No debe haber errores 5xx en viajes concurrentes");
        assertEquals(N, creados.get(), "Todos los viajes deben crearse con 201");
    }

    // =========================================================
    // Helpers
    // =========================================================

    private String passengerBody(String email) {
        return String.format("""
                {
                  "nombre": "Perf User",
                  "email": "%s",
                  "password": "perfpass",
                  "metodoPago": "efectivo",
                  "posicionActual": {"latitud": 43.26, "longitud": -2.93}
                }
                """, email);
    }

    private String driverBody(String email, String licencia) {
        return String.format("""
                {
                  "nombre": "Perf Driver",
                  "email": "%s",
                  "password": "perfpass",
                  "licenciaConducir": "%s",
                  "calificacionMedia": 5.0,
                  "posicionActual": {"latitud": 43.26, "longitud": -2.93}
                }
                """, email, licencia);
    }

    private String tripBody(Long passengerId, String categoria) {
        return String.format("""
                {
                  "passengerId": %d,
                  "origen":  {"latitud": 43.2630, "longitud": -2.9350},
                  "destino": {"latitud": 43.3200, "longitud": -1.9800},
                  "categoria": "%s"
                }
                """, passengerId, categoria);
    }
}
