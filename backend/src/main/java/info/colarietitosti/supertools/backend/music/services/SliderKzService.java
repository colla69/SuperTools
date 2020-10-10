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

    public static final String HREF = "href";
    public static final String A = "a";
    public static final String LIVEAUDIO = "liveaudio";
    public static final String STRIPE_ODD = "stripe-odd";
    private final String baseSearch = "http://slider.kz/#";

    public String searchSong(String sText, FirefoxDriver driver) {
        log.info("Search started: "+sText);
        String dwnLink = "";
        String searchLink = getSearchLink(sText);
        try{
            driver.get(searchLink);
            FirefoxDriverUtils.tryWaitingForPageToLoad(driver, 10, By.className(STRIPE_ODD));
            WebElement el = driver.findElement(By.id(LIVEAUDIO));
            WebElement dwn = el.findElement(By.tagName(A));
            dwnLink = dwn.getAttribute(HREF);

            log.info("found :"+dwnLink+"\n"+"in "+searchLink);
        } catch (Exception e){
            log.error("error finding Download Link in {}", searchLink);
        }
        politeWaitTime();
        return dwnLink;
    }

    private String getSearchLink(String sText) {
        sText = sText.replace(" ","%20");
        return baseSearch.concat(sText);
    }

    private void politeWaitTime() {
        try {
            sleep(500);
        } catch (InterruptedException ex) {
            log.error(ex.getMessage());
        }
    }
}
