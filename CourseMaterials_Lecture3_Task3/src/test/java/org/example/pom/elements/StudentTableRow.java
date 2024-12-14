package org.example.pom.elements;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.function.Function;

public class StudentTableRow {

    private final WebElement root;

    public StudentTableRow(WebElement root) {
        this.root = root;
    }

    public String getName() {
        return root.findElement(By.xpath("./td[2]")).getText();
    }

    public String getStatus() {
        return root.findElement(By.xpath("./td[4]")).getText();
    }

    public void clickTrashIcon() {
        root.findElement(By.xpath("./td/button[text()='delete']")).click();
        waitUntil(root -> root.findElement(By.xpath("./td/button[text()='restore_from_trash']")));
    }

    public void clickRestoreFromTrashIcon() {
        root.findElement(By.xpath("./td/button[text()='restore_from_trash']")).click();
        waitUntil(root -> root.findElement(By.xpath("./td/button[text()='delete']")));
    }

    private void waitUntil(Function<WebElement, WebElement> until) {
        new FluentWait<>(root)
                .withTimeout(Duration.ofSeconds(30))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoring(NoSuchElementException.class)
                .until(until);
    }

}
