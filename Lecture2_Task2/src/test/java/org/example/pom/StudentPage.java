package org.example.pom;

import org.example.pom.elements.StudentRow;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class StudentPage {
    private final WebDriver driver; // Веб-драйвер для взаимодействия с браузером
    private final WebDriverWait wait; // Объект ожидания для явных ожиданий

    // Локаторы
    private final By addButton = By.xpath("//table/tbody/tr[1]/td[4]/button[1]");
    private final By modalTitle = By.id("generateStudentsForm-title");
    private final By studentCountInput = By.cssSelector("input.mdc-text-field__input[type='number']");
    private final By saveButton = By.cssSelector("button[type='submit'][form='generate-logins']");
    private final By closeModalButton = By.cssSelector("div.mdc-dialog.mdc-dialog--open button[data-mdc-dialog-action='close']");

    private final By studentCountDisplay = By.cssSelector(".mdc-button__label .text-span.svelte-b5t5jw");
    private final By zoomButton = By.xpath("//button[contains(@class, 'material-icons') and text()='zoom_in']");

    private final By tableRows = By.cssSelector("div.mdc-dialog__container table tbody tr");

    private final By deleteButton = By.cssSelector("button.material-icons[aria-label='delete']");
    private final By restoreButton = By.cssSelector("button.material-icons[aria-label='restore']");

    // Конструктор
    public StudentPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver; // Инициализация драйвера
        this.wait = wait; // Инициализация ожиданий
        PageFactory.initElements(driver, this);
    }

    public void clickAddButton() {
        WebElement addButtonElement = wait.until(ExpectedConditions.elementToBeClickable(addButton));
        addButtonElement.click();
    }

    // Получение заголовка модального окна
    public String getModalTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(modalTitle)).getText();
    }

    // Ввод количества студентов
    public void enterStudentCount(int count) {
        WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(studentCountInput));
        input.clear(); // Очищаем поле
        input.sendKeys(String.valueOf(count)); // Вводим значение
    }

    public void clickSaveButton() {
        wait.until(ExpectedConditions.elementToBeClickable(saveButton)).click();
    }

    // Метод для закрытия модального окна
    public void closeModal() {
        WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(closeModalButton));
        closeButton.click();
    }


    public int getDisplayedStudentCount() {
        // Ожидаем, что текст в элементе изменится с "0" на любое другое значение
        wait.until(ExpectedConditions.not(ExpectedConditions.textToBePresentInElementLocated(studentCountDisplay, "0")));
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(studentCountDisplay)).getText();
        return Integer.parseInt(text);
    }

    // Нажатие на кнопку "Увеличить"
    public void clickZoomButton() {
        wait.until(ExpectedConditions.elementToBeClickable(zoomButton)).click();
    }

    // Получение количества строк в таблице
    public int getTableRowCount() {
        return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(tableRows)).size();
    }

    // Получение всех строк таблицы как объектов StudentRow
    public List<StudentRow> getStudentRows() {
        // Ждем, пока все строки таблицы станут видимыми
        List<WebElement> rows = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(tableRows));

        // Преобразуем WebElement в StudentRow
        return rows.stream()
                .map(row -> new StudentRow(driver, row))
                .collect(Collectors.toList());
    }


    // Получение конкретной строки студента по индексу
    public StudentRow getStudentRow(int index) {
        List<StudentRow> rows = getStudentRows();
        return rows.get(index);
    }
}
