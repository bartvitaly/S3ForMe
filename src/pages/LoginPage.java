package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class LoginPage {

	private final WebDriver driver;

	By loginPageLocator = By
			.xpath("//body[contains(@class, 'UIPage_LoggedOut')]");

	By usernameLocator = By.id("email");
	By passwordLocator = By.id("pass");
	By loginButtonLocator = By.xpath("//label[@id='loginbutton']/input");

	public LoginPage(WebDriver driver) {
		this.driver = driver;

		WebElement loginPage = driver.findElement(loginPageLocator);

		if (!loginPage.isDisplayed()) {
			throw new IllegalStateException("This is not the login page");
		}
	}

	public LoginPage typeUsername(String username) {
		driver.findElement(usernameLocator).sendKeys(username);
		return this;
	}

	public LoginPage typePassword(String password) {
		driver.findElement(passwordLocator).sendKeys(password);
		return this;
	}

	public HomePage submitLogin() {
		driver.findElement(loginButtonLocator).submit();
		return new HomePage(driver);
	}

	public LoginPage submitLoginExpectingFailure() {
		driver.findElement(loginButtonLocator).submit();
		return new LoginPage(driver);
	}

	public HomePage loginAs(String username, String password) {
		typeUsername(username);
		typePassword(password);
		return submitLogin();
		
	}

}
