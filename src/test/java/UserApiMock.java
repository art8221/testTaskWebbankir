import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.*;

public class UserApiMock {
    private WireMockServer wireMockServer;

    @BeforeEach
    void startMock() {
        wireMockServer = new WireMockServer(wireMockConfig().port(8080));
        wireMockServer.start();
        configureFor("localhost", 8080);
    }

    @AfterEach
    void stopMock() {
        wireMockServer.stop();
    }


    @Test
    void testWithMock() {
        String createdUserName = "userName" + " " + UUID.randomUUID().toString().substring(0, 8);
        String  createdUserEmail = "userEmail" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        String userId = "12345";
        String scenarioName = "user_deletion_scenario";

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("name", createdUserName);
        userPayload.put("email", createdUserEmail);
        userPayload.put("role", "user");

        // Настраиваем мок: при POST /api/v1/users возвращаем 201
        stubFor(post(urlEqualTo("/api/v1/users"))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"12345\", \"name\": \"Test User\"}")));

        // Настраиваем мок Сценарий: пользователь существует
        stubFor(get(urlEqualTo("/api/v1/users/" + userId))
                .inScenario(scenarioName)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"id\": \"" + userId + "\"}")));

        // Настраиваем мок: DELETE меняет состояние
        stubFor(delete(urlEqualTo("/api/v1/users/" + userId))
                .inScenario(scenarioName)
                .whenScenarioStateIs(Scenario.STARTED)
                .willReturn(aResponse().withStatus(200))
                .willSetStateTo("DELETED"));

        // Настраиваем мок: После удаления GET возвращает 404
        stubFor(get(urlEqualTo("/api/v1/users/" + userId))
                .inScenario(scenarioName)
                .whenScenarioStateIs("DELETED")
                .willReturn(aResponse().withStatus(404)));

        // Тест
        UserApiClient client = new UserApiClient("http://localhost:8080");

        Response createResponse = client.createUser(userPayload);
        assertEquals(201, createResponse.getStatusCode());

        String id = createResponse.jsonPath().getString("id");
        assertNotNull(id, "ID пользователя не должен быть null");
        assertFalse(id.isEmpty(), "ID пользователя не должен быть пустым");


        Response getBefore = client.getUserById(userId);
        assertEquals(200, getBefore.getStatusCode());

        Response deleteResponse = client.deleteUser(userId);
        assertEquals(200, deleteResponse.getStatusCode());

        Response getAfter = client.getUserById(userId);
        assertEquals(404, getAfter.getStatusCode());
    }
}