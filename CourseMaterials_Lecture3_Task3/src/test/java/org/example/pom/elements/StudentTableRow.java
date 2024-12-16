package org.example.pom.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

public class StudentTableRow {

    private final SelenideElement root;

    public StudentTableRow(SelenideElement root) {
        this.root = root;
    }

    public String getName() {
        return root.$x("./td[2]").getText();
    }

    public String getStatus() {
        return root.$x("./td[4]").getText();
    }

    public void clickTrashIcon() {
        // Ожидаем, что кнопка "delete" будет видимой
        root.$x("./td/button[text()='delete']").shouldBe(Condition.visible, Duration.ofSeconds(20));
        root.$x("./td/button[text()='delete']").click();

        root.$x("./td/button[text()='restore_from_trash']").shouldBe(Condition.visible, Duration.ofSeconds(60));
        root.$x("./td[@class='mdc-data-table__cell' and text()='block']").shouldBe(Condition.visible, Duration.ofSeconds(60));
    }

    public void clickRestoreFromTrashIcon() {
        root.$x("./td/button[text()='restore_from_trash']").click();
        root.$x("./td/button[text()='delete']").shouldBe(Condition.visible);
    }
}


/*package org.example.pom.elements;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import java.time.Duration;
import java.util.function.Function;

public class StudentTableRow {

*//*    private final WebElement root;

    public StudentTableRow(WebElement root) {
        this.root = root;
    }*//*

    private final SelenideElement root;

    public StudentTableRow(SelenideElement root) {
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
        //waitUntil(root -> root.findElement(By.xpath("./td/button[text()='restore_from_trash']")));
        //waitUntil(root -> root.findElement(By.xpath("./td[@class='mdc-data-table__cell' and text()='block']")));
        // Ожидание появления кнопки "block"
        SelenideElement blockCell = root.$x("./td[@class='mdc-data-table__cell' and text()='block']");
        blockCell.shouldBe(Condition.visible);
    }

    public void clickRestoreFromTrashIcon() {
        root.findElement(By.xpath("./td/button[text()='restore_from_trash']")).click();
        waitUntil(root -> root.findElement(By.xpath("./td/button[text()='delete']")));
    }

    private void waitUntil(Function<WebElement, WebElement> until) {
        new FluentWait<>(root)
                .withTimeout(Duration.ofSeconds(90))
                .pollingEvery(Duration.ofSeconds(3))
                .ignoring(NoSuchElementException.class)
                .until(until);
    }

}*/
