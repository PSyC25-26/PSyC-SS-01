package deusto.sd.ubesto.integration;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.net.URI;
import java.net.http.HttpClient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import deusto.sd.ubesto.entity.Passenger;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class IntegrationTest {
    @Test
    void testCreatePassenger(){
        String nombrePass = "David";
        String emailPass = "david@gmail.com";
        String passPass = "1234";

        String url = "http://localhost:8080/passengers/registerPassenger"; //
        String jsonBody = String.format(
                        "{\"nombre\":\"%s\", \"email\":\"%s\", \"password\":\"%s\", \"metodoPago\":\"efectivo\"}", 
                        nombrePass, emailPass, passPass);

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
            .build();
        try {
            HttpResponse<String> driverResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            assertNotNull(driverResponse);
            assertEquals(HttpStatus.CREATED, driverResponse.statusCode());

        } catch (Exception e) {
            // TODO: handle exception
            fail();
        }
            

    }
}
