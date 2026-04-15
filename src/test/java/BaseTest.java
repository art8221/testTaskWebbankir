import io.github.bonigarcia.seljup.SeleniumJupiter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.time.Duration;

@ExtendWith(SeleniumJupiter.class)
public class BaseTest {

    protected WebDriver driver;
    protected UserApiClient apiClient;

    @BeforeEach
    void setUp(ChromeDriver chromeDriver) {
        this.driver = chromeDriver;
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        apiClient =  new UserApiClient("http://localhost:8080");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}