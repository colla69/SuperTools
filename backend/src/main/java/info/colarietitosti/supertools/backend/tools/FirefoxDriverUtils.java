package info.colarietitosti.supertools.backend.tools;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class FirefoxDriverUtils {

    public static org.openqa.selenium.firefox.FirefoxDriver getFirefoxDriverHeadless() throws Exception {
        FirefoxBinary firefoxBinary = new FirefoxBinary();
        firefoxBinary.addCommandLineOptions("--headless");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
        firefoxOptions.setBinary(firefoxBinary);
        return new org.openqa.selenium.firefox.FirefoxDriver(firefoxOptions);
    }

    public static void killDriver(FirefoxDriver driver) {
        try {
            driver.close();
            driver.quit();
        } catch (Exception ex) {
        }
    }

    public static boolean tryWaitingForPageToLoad(FirefoxDriver driver, long timeOutInSeconds, By expectedConditions) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
            wait.until(ExpectedConditions.elementToBeClickable(expectedConditions));
        } catch (TimeoutException e) {
            killDriver(driver);
            return true;
        }
        return false;
    }

    public static void cleanup(){
        ShellExecuter.execShellCmd("killall geckodriver");
    }
}
