package pages;

import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import me.s3for.common.WebDriverCommon;

public class HomePage {

	private final WebDriver driver;

	By textareaStatusLocator = By.id("u_0_14");
	By buttonSubmitStatus = By
			.xpath("//div[@id='pagelet_composer']//button[@type='submit']");
	By panelStatus = By.id("home_stream");

	public HomePage(WebDriver driver) {
		this.driver = driver;

		WebElement textareaStatus = driver.findElement(textareaStatusLocator);
		if (!textareaStatus.isDisplayed()) {
			throw new IllegalStateException("This is not the home page");
		}
	}

	public HomePage typeStatus(String textStatus) {
		driver.findElement(textareaStatusLocator).sendKeys(textStatus);
		return this;
	}

	public HomePage submitStatus() {
		driver.findElement(buttonSubmitStatus).submit();
		return new HomePage(driver);
	}

	public HomePage postStatus(String textStatus) {
		typeStatus(textStatus);
		return submitStatus();
	}

	public boolean checkLastStatusText(String text) {
		String xPath_elementLastStatus = "//ul[@id='home_stream']/li[not(@data-ft)]//div[@class='storyInnerContent']//span";

		WebDriverCommon.waitForPageLoaded(driver);
		WebElement elementLastStatus = driver.findElement(By
				.xpath(xPath_elementLastStatus));

		int i = 0;

		System.out.println("no while i: " + i + " displayed "
				+ elementLastStatus.isDisplayed() + " text "
				+ elementLastStatus.getText());

		while ((!elementLastStatus.isDisplayed() || !elementLastStatus
				.getText().equals(text)) && i < 30) {

			System.out.println("i: " + i + " displayed "
					+ elementLastStatus.isDisplayed() + " text "
					+ elementLastStatus.getText());

			elementLastStatus = driver.findElement(By
					.xpath(xPath_elementLastStatus));
			i++;
			driver.manage().timeouts().implicitlyWait(1, TimeUnit.SECONDS);
		}

		if (elementLastStatus.isDisplayed()
				&& elementLastStatus.getText().equals(text)) {
			return true;
		}

		return false;
	}
}
