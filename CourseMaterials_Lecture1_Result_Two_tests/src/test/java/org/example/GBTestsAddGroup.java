package org.example;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Пример использования самых базовых методов библиотеки Selenium.
 */
public class GBTestsAddGroup {

    private WebDriver driver;
    private WebDriverWait wait;
    private static String USERNAME;
    private static String PASSWORD;

    @BeforeAll
    public static void setupClass() {
        // Помещаем в переменные окружения путь до драйвера
        System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");
        // mvn clean test -Dgeekbrains_username=USER -Dgeekbrains_password=PASS
        USERNAME = System.getProperty("geekbrains_username", System.getenv("geekbrains_username"));
        PASSWORD = System.getProperty("geekbrains_password", System.getenv("geekbrains_password"));
    }

    @BeforeEach
    public void setupTest() {
        // Создаём экземпляр драйвера
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        // Растягиваем окно браузера на весь экран
        driver.manage().window().maximize();
    }

    @Test
    public void testAddGroup() throws IOException {
        // Навигация на страницу логина
        driver.get("https://test-stand.gb.ru/login");

        // Логин
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form#login input[type='text']")));
        WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form#login input[type='password']")));
        usernameField.sendKeys(USERNAME);
        passwordField.sendKeys(PASSWORD);

        WebElement loginButton = driver.findElement(By.cssSelector("form#login button"));
        loginButton.click();

        // Убедиться, что логин прошел
        wait.until(ExpectedConditions.invisibilityOf(loginButton));

        // Нажимаем на кнопку добавления группы
        WebElement addGroupButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.id("create-btn")));
        addGroupButton.click();

        // Ждем появления модального окна
        WebElement modalWindow = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".mdc-dialog__container")));

        // Вводим уникальное имя группы
        String uniqueGroupName = "TestGroup" + System.currentTimeMillis();
        WebElement groupNameField = modalWindow.findElement(By.cssSelector("input.mdc-text-field__input"));
        groupNameField.sendKeys(uniqueGroupName);

        // Нажимаем кнопку Save
        WebElement saveButton = modalWindow.findElement(By.cssSelector("button[type='submit']"));
        saveButton.click();

        // Ждем появления новой группы в таблице
        WebElement newGroupTitle = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//table//td[text()='" + uniqueGroupName + "']")));
        Assertions.assertNotNull(newGroupTitle, "Группа не появилась в таблице");


        // Сохраняем скриншот экрана
        Path screenshotPath = Path.of("src", "test", "resources", "screenshot.png");
        TakesScreenshot screenshot = (TakesScreenshot) driver;
        Files.write(screenshotPath, screenshot.getScreenshotAs(OutputType.BYTES));

        System.out.println("Скриншот сохранен: " + screenshotPath.toAbsolutePath());
    }


    @AfterEach
    public void teardown() {
        // Закрываем все окна брайзера и процесс драйвера
        driver.quit();
    }

}

