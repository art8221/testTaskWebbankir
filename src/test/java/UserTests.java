import io.restassured.response.Response;
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

    @ParameterizedTest
    @MethodSource("userTestDataProvider")
    void createUserAndRemoved(String testName, String userName, String userEmail,
                              String role, int expectedStatusCode, boolean shouldExistInUI) {

        createdUserName = userName + " " + UUID.randomUUID().toString().substring(0, 8);
        createdUserEmail = userEmail + UUID.randomUUID().toString().substring(0, 8) + "@example.com";

        Map<String, Object> userPayload = new HashMap<>();
        userPayload.put("name", createdUserName);
        userPayload.put("email", createdUserEmail);
        userPayload.put("role", role);

        Response createResponse = apiClient.createUser(userPayload);

        assertEquals(expectedStatusCode, createResponse.getStatusCode(),
                String.format("[%s] Ожидался статус %d, получен: %d",
                        testName, expectedStatusCode, createResponse.getStatusCode()));

        if (expectedStatusCode == 201) {
            createdUserId = createResponse.jsonPath().getString("id");

            assertNotNull(createdUserId,
                    String.format("[%s] ID пользователя не должен быть null", testName));

            adminUsersPage = new UsersTablePage(driver);
            adminUsersPage.pageToLoad();

            boolean userFound = adminUsersPage.isUserPresent(createdUserName, createdUserEmail);

            if (shouldExistInUI) {
                assertTrue(userFound,
                        String.format("[%s] Пользователь с email '%s' или именем '%s' не найден на странице",
                                testName, createdUserEmail, createdUserName));
            } else {
                assertFalse(userFound,
                        String.format("[%s] Пользователь с email '%s' не должен отображаться на странице",
                                testName, createdUserEmail));
            }

            Response deleteResponse = apiClient.deleteUser(createdUserId);
            int statusCode = deleteResponse.getStatusCode();

            assertEquals(200, statusCode,
                    String.format("[%s] Не удалось удалить пользователя с ID '%s'",
                            testName, createdUserId));

            adminUsersPage.refresh();

            boolean userStillExists = adminUsersPage.isUserPresent(createdUserName, createdUserEmail);

            assertFalse(userStillExists,
                    String.format("[%s] Пользователь с email '%s' всё ещё отображается после удаления",
                            testName, createdUserEmail));
        }
    }

    static Stream<Arguments> userTestDataProvider() {
        return Stream.of(
                Arguments.of(
                        "Валидный пользователь",
                        "Test User",
                        "test.",
                        "user",
                        201,
                        true
                ),
                Arguments.of(
                        "Невалидный email",
                        "Invalid Email User",
                        "invalid-email",
                        "user",
                        400,
                        false
                ),
                Arguments.of(
                        "Дублирующийся email",
                        "Duplicate User",
                        "duplicate.",
                        "user",
                        409,
                        false
                )
        );
    }
}