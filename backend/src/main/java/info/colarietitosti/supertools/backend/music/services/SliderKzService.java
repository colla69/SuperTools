package info.colarietitosti.supertools.backend.music.services;

import info.colarietitosti.supertools.backend.tools.FirefoxDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.stereotype.Service;

import static java.lang.Thread.sleep;

@Slf4j
@Service
public class SliderKzService {

    private final String baseSearch = "http://slider.kz/#";

    public String searchSong(String sText, FirefoxDriver driver) {
        log.info("Search started: "+sText);
        sText = sText.replace(" ","%20");
        String sLink = baseSearch.concat(sText);
        try{
            driver.get(sLink);
            FirefoxDriverUtils.tryWaitingForPageToLoad(driver, 10, By.className("stripe-odd"));
            WebElement el = driver.findElement(By.id("liveaudio"));
            WebElement dwn = el.findElement(By.tagName("a"));
            String dwnLink = dwn.getAttribute("href");
            log.info("found :"+dwnLink+"\n"+"in "+sLink);
            sleep(500);
            return dwnLink;
        } catch (Exception e){
            log.error("error finding Download Link in {}", sLink);
            //e.printStackTrace();
            try {
                sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return "";
        }
    }
}
