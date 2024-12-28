package org.example.pom;

import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProfilePage {

    private final WebDriverWait wait;
    private final WebDriver driver;



    @FindBy(xpath = "//button[contains(@class, 'material-icons') and contains(@class, 'mdc-icon-button') and @title='More options' and text()='edit']") // Кнопка для открытия модального окна
    private WebElement editProfileButton;

    @FindBy(css = "input[type='date']") // Поле даты рождения
    private WebElement birthdateField;

    @FindBy(css = "button.mdc-button[type='submit']") // Кнопка сохранения изменений
    private WebElement  saveProfileButton;

    @FindBy(css = "button[data-mdc-dialog-action='close']") // Кнопка закрытия модального окна
    private WebElement closeEditModalButton;

    //@FindBy(xpath = "//div[@class='row'][.//div[@class='label' and text()='Date of birth']]//div[@class='content']") // Элемент с датой рождения
    @FindBy(xpath = "//div[@class='row svelte-vyyzan'][.//div[@class='label svelte-vyyzan' and text()='Date of birth']]//div[@class='content svelte-vyyzan']") // Элемент с датой рождения
    private WebElement dateOfBirthLabel;

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    public WebElement getBirthdateLabelElement() {
        return dateOfBirthLabel;
    }


    // Открытие модального окна редактирования
    public void openEditProfileModal() {
        wait.until(ExpectedConditions.visibilityOf(editProfileButton)).click();
    }

    // Установить новую дату рождения
    public void setBirthdate(String newBirthdate) throws InterruptedException {

        JavascriptExecutor js = (JavascriptExecutor) driver;
        // Очистить поле перед вводом нового значения
        js.executeScript("arguments[0].value = ''; ", birthdateField);
        js.executeScript("arguments[0].value = arguments[1];", birthdateField, newBirthdate);
        // Потеря фокуса после ввода даты
        birthdateField.sendKeys(Keys.TAB);
        // Убедимся, что значение установлено
        assertEquals(newBirthdate, birthdateField.getAttribute("value"), "Значение не установилось корректно");
        // Убедиться, что новое значение отображается в Additional info
        //wait.until(ExpectedConditions.textToBePresentInElement(dateOfBirthLabel, newBirthdate));
    }


    // Сохранить изменения в профиле
    public void saveProfileChanges() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", saveProfileButton);
        //wait.until(ExpectedConditions.elementToBeClickable(saveProfileButton)).click();
    }

    // Закрыть модальное окно редактирования
    public void closeEditProfileModal() {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", closeEditModalButton); // Клик по кнопке закрытия
        //wait.until(ExpectedConditions.elementToBeClickable(closeEditModalButton)).click();
        // Подождать закрытия модального окна
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".mdc-dialog__container")));
    }
/*    public void closeEditProfileModal() {
        wait.until(ExpectedConditions.invisibilityOf(closeEditModalButton)); // Подтверждаем, что окно закрыто
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].click();", closeEditModalButton); // Клик по кнопке закрытия
    }*/


    // Получить обновленную дату рождения из секции Additional Info
    public String getBirthdateFromAdditionalInfo() {
        return wait.until(ExpectedConditions.visibilityOf(dateOfBirthLabel)).getText();
    }
}
