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
        //System.setProperty("webdriver.gecko.driver", "/home/ndipiazza/Desktop/geckodriver");
        FirefoxOptions firefoxOptions = new FirefoxOptions();
        System.setProperty(FirefoxDriver.SystemProperty.DRIVER_USE_MARIONETTE,"true");
        //System.setProperty(FirefoxDriver.SystemProperty.BROWSER_LOGFILE,"/tmp/seleLog.txt");
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

    public static boolean tryWaitingForPageToLoad(FirefoxDriver driver, long timeOutInSeconds) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeOutInSeconds);
            wait.until(ExpectedConditions.elementToBeClickable(By.className("push_button")));
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
