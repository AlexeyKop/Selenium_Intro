package org.example;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;

/**
 * Пример использования самых базовых методов библиотеки Selenium.
 */
public class GeekBrainsStandTests {

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
    public void testGeekBrainsStandLogin() {
        // Навигация на https://test-stand.gb.ru/login
        driver.get("https://test-stand.gb.ru/login");

        // Поиск полей для ввода username и password + Два явных ожидания
        WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form#login input[type='text']")));
        WebElement passwordField = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form#login input[type='password']")));
        usernameField.sendKeys(USERNAME);
        passwordField.sendKeys(PASSWORD);

        // Нажатие LOGIN кнопки
        WebElement loginButton = driver.findElement(By.cssSelector("form#login button"));
        loginButton.click();
        // Ожидание после совершения действия: кнопка LOGIN должна исчезнуть
        wait.until(ExpectedConditions.invisibilityOf(loginButton));

        // Проверка, что логин прошёл успешно
        // Одно явное ожидание элемента для завершения теста
        WebElement usernameLink = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(USERNAME)));
        String actualUsername = usernameLink.getText().replace("\n", " ").trim();
        Assertions.assertEquals(String.format("Hello, %s", USERNAME), actualUsername);
    }

    @Test
    public void testAddingGroupOnMainPage() throws IOException {
        // Навигация на https://test-stand.gb.ru/login
        driver.get("https://test-stand.gb.ru/login");

        // Поиск полей для ввода username и password
        // Два явных ожидания
        wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("form#login input[type='text']"))).sendKeys(USERNAME);
        driver.findElement(By.cssSelector("form#login input[type='password']")).sendKeys(PASSWORD);
        driver.findElement(By.cssSelector("form#login button")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.partialLinkText(USERNAME)));

        // Создание группы. Даём ей уникальное имя, чтобы в каждом запуске была проверка нового имени
        String groupTestName = "New Test Group " + System.currentTimeMillis();
        driver.findElement(By.id("create-btn")).click();
        By groupNameField = By.xpath("//form//span[contains(text(), 'Group name')]/following-sibling::input");
        wait.until(ExpectedConditions.visibilityOfElementLocated(groupNameField)).sendKeys(groupTestName);
        driver.findElements(By.cssSelector("form div.submit button"))
                .stream().filter(WebElement::isDisplayed).findFirst().orElseThrow().click();

        // Проверка, что группа создана и находится в таблице
        // Одно явное ожидание элемента для завершения теста
        String tableTitlesXpath = "//table[@aria-label='Tutors list']//tbody/tr/td[text()='%s']";
        WebElement expectedTitle = wait.until(ExpectedConditions
                .visibilityOfElementLocated(By.xpath(String.format(tableTitlesXpath, groupTestName))));
        Assertions.assertTrue(expectedTitle.isDisplayed());

        // Как сделать скриншот окна браузера
        byte[] screenshotBytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Files.write(Path.of(
                "src/test/resources/screenshot_" + System.currentTimeMillis() + ".png"), screenshotBytes);
    }

    /*
    Тест закомментирован, чтобы оставить проект только с тестами на стенд GeekBrains.
    Но код можно пока оставить, в качестве примера.

    @Test
    public void testDragAndDropActionWithScreenshot() throws InterruptedException {
        // Навигация на https://www.globalsqa.com/demo-site/draganddrop/
        driver.get("https://www.globalsqa.com/demo-site/draganddrop/");

        // Переключение на фрейм, так как элементы для работы находятся внутри тега iframe
        driver.switchTo().frame(driver.findElement(By.cssSelector("iframe.demo-frame.lazyloaded")));

        // Поиск двух элементов для операции drag&drop
        WebElement pictureElement = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector("ul#gallery li.ui-draggable")));
        WebElement trashSection = driver.findElement(By.id("trash"));

        // Класс Actions в деле
        Actions action = new Actions(driver);
        action.dragAndDrop(pictureElement, trashSection)
                .build().perform();
        // Использование такого ожидания в реальном проекте - плохая практика
        // Но в учебных целях иногда полезно, чтобы понаблюдать как работает тест
        Thread.sleep(1000);
    }
    */

    @AfterEach
    public void teardown() {
        // Закрываем все окна брайзера и процесс драйвера
        driver.quit();
    }

}

