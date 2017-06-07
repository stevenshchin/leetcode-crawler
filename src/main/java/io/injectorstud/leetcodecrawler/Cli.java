package io.injectorstud.leetcodecrawler;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

public class Cli {
  public static void main(String[] args) {
    System.setProperty("webdriver.chrome.driver", "bin/chromedriver");

    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    ChromeOptions options = new ChromeOptions();

    options.addArguments("user-data-dir=run/chrome/profile");
    capabilities.setCapability(ChromeOptions.CAPABILITY, options);

    WebDriver driver = new ChromeDriver(capabilities);
    Crawler crawler =
        new Crawler(driver, new ProblemSetParser());

    crawler.run();
  }
}
