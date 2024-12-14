package org.example.pom.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;
import java.util.function.Function;

public class StudentRow {
    private final WebDriver driver;
    private final WebElement rowElement; // Элемент строки таблицы

    // Локаторы для элементов внутри строки
    private final By statusCell = By.xpath("./td[4]"); // Четвертая колонка для статуса
    private final By deleteButton = By.xpath("./td[5]//button[contains(@class, 'material-icons') and text()='delete']");
    private final By restoreButton = By.xpath("./td[5]//button[contains(@class, 'material-icons') and text()='restore_from_trash']");

    // Конструктор
    public StudentRow(WebDriver driver, WebElement rowElement) {
        this.driver = driver;
        this.rowElement = rowElement;
    }

    // Получение текущего статуса с ожиданием
    public String getStatus() {
        return waitUntilStatusChanged(rowElement -> rowElement.findElement(statusCell).getText());
    }

    // Клик на кнопку "удалить" (корзина)
    public void clickDelete() {
        WebElement deleteBtn = rowElement.findElement(deleteButton);
        deleteBtn.click();
        // После клика на корзину ждём, пока статус изменится на "block"
        waitUntilStatusChanged(rowElement -> rowElement.findElement(statusCell).getText());
    }

    // Клик на кнопку "восстановить"
    public void clickRestore() {
        WebElement restoreBtn = rowElement.findElement(restoreButton);
        restoreBtn.click();
        // После клика на восстановление ждём, пока статус изменится обратно на "active"
        waitUntilStatusChanged(rowElement -> rowElement.findElement(statusCell).getText());
    }

    // Метод для ожидания изменения статуса (с использованием FluentWait)
    private String waitUntilStatusChanged(Function<WebElement, String> condition) {
        FluentWait<WebElement> wait = new FluentWait<>(rowElement)
                .withTimeout(Duration.ofSeconds(20)) // Время ожидания
                .pollingEvery(Duration.ofSeconds(1)) // Интервал между проверками
                .ignoring(NoSuchElementException.class); // Игнорировать исключения

        return wait.until(condition); // Дожидаемся, пока условие выполнится
    }
}
