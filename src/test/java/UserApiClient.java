import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class UserApiClient {
    private final String baseUrl;
    private static UserApiClient instance;

    public UserApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response createUser(Map<String, Object> payload) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()           // ← добавить .all()
                .when()
                .post("/api/v1/users")
                .then()
                .log().all()           // ← добавить .all()
                .extract().response(); // ← добавить .response()
    }

    public Response getUserById(String userId) {
        return given()
                .baseUri(baseUrl)
                .pathParam("id", userId)
                .log().all()
                .when()
                .get("/api/v1/users/{id}")
                .then()
                .log().all()
                .extract().response();
    }

    public Response deleteUser(String userId) {
        return given()
                .baseUri(baseUrl)
                .pathParam("id", userId)
                .log().all()
                .when()
                .delete("/api/v1/users/{id}")
                .then()
                .log().all()
                .extract().response();
    }
}