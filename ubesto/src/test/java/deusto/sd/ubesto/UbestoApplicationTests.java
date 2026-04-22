package deusto.sd.ubesto;

import org.junit.jupiter.api.DisplayName;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

import deusto.sd.ubesto.integration.IntegrationTest;
import deusto.sd.ubesto.unitarios.UnitariosTest;

/**
 * Suite principal de tests de Ubesto.
 *
 * Ejecuta todos los tests en orden:
 *   1. UnitariosTest  → sin Spring, rápidos, con Mockito
 *   2. IntegrationTest → con Spring + H2 en memoria, MockMvc
 *
 * Para ejecutar solo esta suite: mvn test -Dtest=UbestoApplicationTests
 * Para ejecutar todos los tests:  mvn test
 */
@Suite
@SuiteDisplayName("Ubesto - Suite Completa de Tests")
@SelectClasses({
    UnitariosTest.class,
    IntegrationTest.class
})
@DisplayName("Suite Completa: Unitarios + Integración")
public class UbestoApplicationTests {
    // JUnit Platform descubre y ejecuta las clases declaradas arriba.
    // No es necesario ningún método aquí.
}