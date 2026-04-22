import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class UserTests extends BaseTest {
    private String createdUserId;
    private String createdUserEmail;
    private String createdUserName;
    private UsersTablePage adminUsersPage;

    @Test
    void createUserAndRemoved() {
        createdUserName = "userName" + " " + UUID.randomUUID().toString().substring(0, 8);
        createdUserEmail = "userEmail" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("name", createdUserName);
        userPayload.put("email", createdUserEmail);
        userPayload.put("role", "user");

        Response createResponse = apiClient.createUser(userPayload);

        assertEquals(201, createResponse.getStatusCode(), "Статус код не соответствует ожидаемому");


        createdUserId = createResponse.jsonPath().getString("id");

        assertNotNull(createdUserId, "ID пользователя не должен быть null");

        adminUsersPage = new UsersTablePage(driver);
        adminUsersPage.pageToLoad();

        assertTrue(adminUsersPage.isUserPresent(createdUserName, createdUserEmail),
                "Пользователь с именем '" + createdUserName + "' не появился на странице /admin/users");


        Response deleteResponse = apiClient.deleteUser(createdUserId);
        int statusCode = deleteResponse.getStatusCode();

        assertEquals(200, statusCode,
                String.format("Не удалось удалить пользователя с ID '%s'", createdUserId));

        adminUsersPage.refresh();

        assertTrue(adminUsersPage.isUserAbsent(createdUserName, createdUserEmail),
                "Пользователь с именем '" + createdUserName + "' всё ещё отображается после удаления");
    }

    @ParameterizedTest
    @MethodSource("userTestDataProvider")
    void createNegative(String testName, String userName, String userEmail, String hostEmail,
                String role, int expectedStatusCode) {

        createdUserName = userName + " " + UUID.randomUUID().toString().substring(0, 8);
        createdUserEmail = userEmail + UUID.randomUUID().toString().substring(0, 8) + hostEmail;

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("name", createdUserName);
        userPayload.put("email", createdUserEmail);
        userPayload.put("role", role);

        Response createResponse = apiClient.createUser(userPayload);

        assertEquals(expectedStatusCode, createResponse.getStatusCode(),
                String.format("[%s] Ожидался статус %d, получен: %d",
                        testName, expectedStatusCode, createResponse.getStatusCode()));

        adminUsersPage = new UsersTablePage(driver);
        adminUsersPage.pageToLoad();

        assertFalse(adminUsersPage.isUserPresent(createdUserName, createdUserEmail),
                "Пользователь с именем '" + createdUserName + "' появился на странице /admin/users");


    }

    static Stream<Arguments> userTestDataProvider() {
        return Stream.of(
                Arguments.of(
                        "Невалидный user name",
                        "TestUser;%?*?:%;%:?*?%:??:?:?",
                        "test",
                        "@example.com",
                        "user",
                        400
                ),
                Arguments.of(
                        "Невалидный email",
                        "Invalid Email User",
                        "invalid-email",
                        "example.com",
                        "user",
                        400
                )
        );
    }
}