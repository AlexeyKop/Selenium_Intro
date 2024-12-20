package org.example.pom;

import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.support.FindBy;

import java.io.File;
import java.util.Objects;

import static com.codeborne.selenide.Condition.visible;

public class ProfilePage {

    @FindBy(xpath = "//h3/following-sibling::div" +
            "//div[contains(text(), 'Full name')]/following-sibling::div")
    private SelenideElement fullNameInAdditionalInfo;
    @FindBy(css = "div.mdc-card h2")
    private SelenideElement fullNameInAvatarSection;
    @FindBy(css = "div.mdc-card div.mdc-card__action--icon button[title='More options']")
    private SelenideElement editIconInAvatarSection;
    @FindBy(xpath = "//form[@id='update-item']//span[contains(text(), 'New Avatar')]/following-sibling::input")
    private SelenideElement newAvatarFieldOnEditingPopup;

    public void clickEditIconInAvatarSection(){
        editIconInAvatarSection.shouldBe(visible).click();
    }

    public void uploadPictureFileToAvatarField(String filePath){
        newAvatarFieldOnEditingPopup.shouldBe(visible).uploadFile(new File(filePath));
    }

    public String getAvatarInputValueOnSettingPopup(){
        String inputValue = newAvatarFieldOnEditingPopup.getValue();
        return Objects.requireNonNull(inputValue)
                .substring(inputValue.lastIndexOf("\\") + 1);
    }

    public String getFullNameFromAdditionalInfo() {
        return fullNameInAdditionalInfo.shouldBe(visible).text();
    }

    public String getFullNameFromAvatarSection() {
        return fullNameInAvatarSection.shouldBe(visible).text();
    }

}
