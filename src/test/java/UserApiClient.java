import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserApiClient {
    private final String baseUrl;

    public UserApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response createUser(Map<String, Object> payload) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post("/api/v1/users");
    }

    public Response getUserById(String userId) {
        return given()
                .baseUri(baseUrl)
                .pathParam("id", userId)
                .when()
                .get("/api/v1/users/{id}");
    }

    public Response deleteUser(String userId) {
        return given()
                .baseUri(baseUrl)
                .pathParam("id", userId)
                .when()
                .delete("/api/v1/users/{id}");
    }
}