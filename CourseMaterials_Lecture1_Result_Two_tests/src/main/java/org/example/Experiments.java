package org.example;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class Experiments {
    public static void main(String[] args) {
        String pathToChromeDriver = "D:\\Learning_5.0_D disk\\GeekBrains\\Selenium_Intro\\CourseMaterials_Lecture1_Result_Two_tests\\src\\test\\resources\\chromedriver.exe";
        System.setProperty("webdriver.chrome.driver", pathToChromeDriver);

        ChromeOptions options = new ChromeOptions();
        // options.addArguments("--headless"); // в явном виде не будет запускать браузер
        // options.addArguments("--start-maximized");

        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();

        driver.get("https://www.google.com");
        System.out.println("Page title: " + driver.getTitle());
        driver.quit();
    }
}
