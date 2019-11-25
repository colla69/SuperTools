package info.colarietitosti.supertools.backend.tools;

import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.springframework.stereotype.Component;

@Component
public class FirefoxDriverFactory {

    public org.openqa.selenium.firefox.FirefoxDriver getFirefoxDriverHeadless() throws Exception {
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        //System.setProperty("webdriver.gecko.driver", "/home/ndipiazza/Desktop/geckodriver");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
        //System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/tmp/seleLog.txt");
        firefoxOptions.setBinary(firefoxBinary);
        return new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions);
    }
}
