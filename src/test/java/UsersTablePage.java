import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class UsersTablePage {
    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(id = "users-table")
    private WebElement usersTable;

    @FindBy(css = "#users-table tbody tr")
    private List<WebElement> userRows;

    public UsersTablePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public void pageToLoad() {
        driver.get("http://localhost:8080/admin/users");
        waitForPageToLoad();
    }

    public void waitForPageToLoad() {
        wait.until(ExpectedConditions.visibilityOf(usersTable));
    }

    public void refresh() {
        driver.navigate().refresh();
        waitForPageToLoad();
    }

    public boolean isUserPresent(String userName, String userEmail) {
        return getAllUsers().stream()
                .anyMatch(user -> user.containsText(userName) || user.containsText(userEmail));
    }

    public List<UserRow> getAllUsers() {
        return userRows.stream()
                .map(UserRow::new)
                .collect(Collectors.toList());
    }

    public static class UserRow {
        private final WebElement row;

        public UserRow(WebElement row) {
            this.row = row;
        }

        public String getText() {
            return row.getText();
        }

        public boolean containsText(String text) {
            return getText().contains(text);
        }
    }
}