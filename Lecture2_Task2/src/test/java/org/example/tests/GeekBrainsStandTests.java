package org.example.tests;

import org.example.pom.LoginPage;
import org.example.pom.MainPage;
import org.example.pom.StudentPage;
import org.example.pom.elements.StudentRow;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
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
    private StudentPage studentPage;

    private static final String USERNAME = "Student-13";
    private static final String PASSWORD = "0d53afe343";

    @BeforeAll
    public static void setupClass() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver");
    }

    @BeforeEach
    public void setupTest() {
        // Создаём экземпляр драйвера
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        // Растягиваем окно браузера на весь экран
        driver.manage().window().maximize();
        // Навигация на https://test-stand.gb.ru/login
        driver.get("https://test-stand.gb.ru/login");
        // Объект созданного Page Object
        loginPage = new LoginPage(driver, wait);
        studentPage = new StudentPage(driver, wait); // Добавлено
    }

    @Test
    public void testLoginWithoutCredentials() {
        // Попытка логина без указания логина и пароля
        loginPage.clickLoginButton();

        // Проверяем, что код ошибки — "401"
        String expectedErrorCode = "401";
        assertEquals(expectedErrorCode, loginPage.getErrorCode());

        // Проверяем, что текст ошибки соответствует "Invalid credentials."
        String expectedErrorMessage = "Invalid credentials.";
        assertEquals(expectedErrorMessage, loginPage.getErrorMessage());
    }


    @Test
    public void testAddingStudents() {
        // Проверяем успешный вход в систему
        checkLogin();

        // Создание группы. Даём ей уникальное имя, чтобы в каждом запуске была проверка нового имени
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        mainPage.createGroup(groupTestName);

        mainPage.closeCreateGroupModalWindow();

        studentPage.clickAddButton();

        // Проверяем заголовок модального окна
        assertEquals("Creating new logins", studentPage.getModalTitle());

        // Ввод количества студентов и сохранение
        studentPage.enterStudentCount(3);
        studentPage.clickSaveButton();

        studentPage.closeModal();

        // Проверяем, что отображаемое количество студентов равно 3
        assertEquals(3, studentPage.getDisplayedStudentCount());


        // Проверка таблицы после "Увеличить"
        studentPage.clickZoomButton();

        // Ожидаем появления заголовка модального окна
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[text()='Students identities']")));

        // Получаем количество строк в таблице
        int rowCount = studentPage.getTableRowCount();

        // Проверяем, что в таблице три строки
        assertEquals(3, rowCount);
    }

    @Test
    public void testStudentStatusChange() {
        checkLogin();

        studentPage.clickZoomButton();

        //Получаем первую строку таблицы
        StudentRow firstRow = studentPage.getStudentRow(0);

        // Проверяем, что изначальный статус - "active"
        assertEquals("active", firstRow.getStatus());

        //Кликаем на кнопку "удалить" и проверяем, что статус изменился на "block"
        firstRow.clickDelete();
        // Добавляем ожидание, чтобы убедиться, что статус обновился
        wait.until(driver -> firstRow.getStatus().equals("block"));
        assertEquals("block", firstRow.getStatus());

        //Кликаем на кнопку "восстановить" и проверяем, что статус изменился обратно на "active"
        firstRow.clickRestore();

        //Добавляем ожидание, чтобы убедиться, что статус обновился
        wait.until(driver -> firstRow.getStatus().equals("active"));
        assertEquals("active", firstRow.getStatus());
    }



    private void checkLogin() {
        // Логин в систему с помощью метода из класса Page Object
        loginPage.login(USERNAME, PASSWORD);
        // Инициализация объекта класса MainPage
        mainPage = new MainPage(driver, wait);
        // Проверка, что логин прошёл успешно
        assertTrue(mainPage.getUsernameLabelText().contains(USERNAME));
    }

    @AfterEach
    public void teardown() {
        // Закрываем все окна брайзера и процесс драйвера
        driver.quit();
    }

}

