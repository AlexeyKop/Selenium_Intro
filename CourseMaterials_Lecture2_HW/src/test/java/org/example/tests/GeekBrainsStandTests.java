package org.example.tests;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import org.example.pom.LoginPage;
import org.example.pom.MainPage;
import org.example.pom.ProfilePage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Пример использования самых базовых методов библиотеки Selenium.
 */
public class GeekBrainsStandTests {

    private WebDriver driver;
    private WebDriverWait wait;
    private LoginPage loginPage;
    private MainPage mainPage;
    private ProfilePage profilePage;

    private static final String USERNAME = "Student-13";
    private static final String PASSWORD = "0d53afe343";
    private static final String FULL_NAME = "13 Student";

    @BeforeAll
    public static void setupClass() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.browser = "chrome";
        Configuration.browserVersion = "128";

        // Помещаем в переменные окружения путь до драйвера
        //System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");

        // mvn clean test -Dgeekbrains_username=USER -Dgeekbrains_password=PASS
        //USERNAME = System.getProperty("geekbrains_username", System.getenv("geekbrains_username"));
        //PASSWORD = System.getProperty("geekbrains_password", System.getenv("geekbrains_password"));
    }

    @BeforeEach
    public void setupTest() throws MalformedURLException {

        // URL Selenoid сервера
        URL selenoidURL = new URL("http://localhost:4444/wd/hub");

        // Настройки браузера Chrome
        ChromeOptions options = new ChromeOptions();
        options.setCapability("browserName", "chrome");
        options.setCapability("browserVersion", "128.0"); // версия браузера
        options.setCapability("enableVNC", true); // Включить просмотр через VNC
        options.setCapability("enableVideo", true); // Включить запись видео
        options.setCapability("enableLog", true); // Включить логи браузера

        // Создание драйвера
        driver = new RemoteWebDriver(selenoidURL, options);


        // Создаём экземпляр драйвера
        //driver = new ChromeDriver();

        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        // Растягиваем окно браузера на весь экран
        driver.manage().window().maximize();
        // Навигация на https://test-stand.gb.ru/login
        driver.get("https://test-stand.gb.ru/login");
        // Объект созданного Page Object
        loginPage = new LoginPage(driver, wait);
        profilePage = new ProfilePage(driver);
    }

    @Test
    public void testLoginWithEmptyFields() throws InterruptedException {
        // Клик на кнопку LOGIN без ввода данных в поля
        loginPage.clickLoginButton();
        // Проверка, что появился блок с ожидаемой ошибкой
        assertEquals("401 Invalid credentials.", loginPage.getErrorBlockText());
        //Thread.sleep(5000);
    }

    @Test
    public void testAddingGroupOnMainPage() {
        // Логин в систему с помощью метода из класса Page Object
        loginPage.login(USERNAME, PASSWORD);
        // Инициализация объекта класса MainPage
        mainPage = new MainPage(driver, wait);
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
        // Создание группы. Даём ей уникальное имя, чтобы в каждом запуске была проверка нового имени
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
        // Проверка, что группа создана и находится в таблице
        assertTrue(mainPage.waitAndGetGroupTitleByText(groupTestName).isDisplayed());
    }

    @Test
    void testArchiveGroupOnMainPage() {
        // Обычный логин + создание группы
        loginPage.login(USERNAME, PASSWORD);
        mainPage = new MainPage(driver, wait);
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
        // Требуется закрыть модальное окно
        mainPage.closeCreateGroupModalWindow();
        // Изменение созданной группы с проверками
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("inactive", mainPage.getStatusOfGroupWithTitle(groupTestName));
        mainPage.clickRestoreFromTrashIconOnGroupWithTitle(groupTestName);
        assertEquals("active", mainPage.getStatusOfGroupWithTitle(groupTestName));
    }

    @Test
    void testBlockingStudentInTableOnMainPage() throws InterruptedException {
        // Обычный логин + создание группы
        loginPage.login(USERNAME, PASSWORD);
        mainPage = new MainPage(driver, wait);
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);
        // Требуется закрыть модальное окно
        mainPage.closeCreateGroupModalWindow();
        // Добавление студентов
        mainPage.clickAddStudentsIconOnGroupWithTitle(groupTestName);
        mainPage.typeAmountOfStudentsInCreateStudentsForm(3);
        mainPage.clickSaveButtonOnCreateStudentsForm();
        mainPage.closeCreateStudentsModalWindow();
        mainPage.clickZoomInIconOnGroupWithTitle(groupTestName);
        // Проверка переходов статуса первого студента из таблицы
        String firstGeneratedStudentName = mainPage.getFirstGeneratedStudentName();
        assertEquals("active", mainPage.getStatusOfStudentWithName(firstGeneratedStudentName));
        mainPage.clickTrashIconOnStudentWithName(firstGeneratedStudentName);
        assertEquals("block", mainPage.getStatusOfStudentWithName(firstGeneratedStudentName));
        mainPage.clickRestoreFromTrashIconOnStudentWithName(firstGeneratedStudentName);
        assertEquals("active", mainPage.getStatusOfStudentWithName(firstGeneratedStudentName));
    }

    @Test
    void testEditBirthdateOnProfilePage() throws InterruptedException {
        // Логин в систему с помощью метода из класса Page Object
        loginPage.login(USERNAME, PASSWORD);
        // Удаление кэшированных данных
        driver.manage().deleteAllCookies();
        // Инициализация объекта класса MainPage
        mainPage = new MainPage(driver, wait);
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
        // Навигация на Profile page
        mainPage.clickUsernameLabel();
        mainPage.clickProfileLink();

        // Открытие модального окна редактирования
        profilePage.openEditProfileModal();

        // Изменение значения даты рождения
        String newBirthdate = "2013-03-03";
        profilePage.setBirthdate(newBirthdate);
        //Thread.sleep(5000);

        // Сохранение изменений и закрытие модального окна
        profilePage.saveProfileChanges();

        Thread.sleep(10000);
        //driver.navigate().refresh();

        profilePage.closeEditProfileModal();


        // Проверка, что изменения применились
        //Thread.sleep(5000);
        String actualBirthdate = profilePage.getBirthdateFromAdditionalInfo();
        assertEquals(newBirthdate, actualBirthdate, "Дата рождения не была обновлена");
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            try {
                File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
                Path destination = Path.of("screenshots", "test_failure_" + System.currentTimeMillis() + ".png");
                Files.createDirectories(destination.getParent());
                Files.copy(screenshot.toPath(), destination);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                driver.quit();
            }
        }
    }


}

