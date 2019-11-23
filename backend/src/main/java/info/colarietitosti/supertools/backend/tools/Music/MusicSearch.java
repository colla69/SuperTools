package info.colarietitosti.supertools.backend.tools.Music;

import info.colarietitosti.supertools.backend.tools.FirefoxDriverFactory;
import info.colarietitosti.supertools.backend.tools.Music.Entity.Album;
import info.colarietitosti.supertools.backend.tools.Music.Entity.Artist;
import info.colarietitosti.supertools.backend.tools.Music.Entity.Track;
import lombok.extern.java.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;


import static java.lang.Thread.sleep;

@Log
public class MusicSearch {

    private final String baseSearch = "http://slider.kz/#";


    public Artist searchArtist(String artist, String artistLink){
        String baseLink = "https://www.discogs.com";
        Artist result = new Artist(artist, artistLink);
        Document doc = null;
        try {
            doc = Jsoup.connect(baseLink+"/artist/"+artistLink).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element table = doc.getElementById("artist");
        Elements rows = table.select("tr");
        for (int i = 1; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.getElementsByClass("title");
            for (Element c : cols){
                Element a = c.select("a").first();
                String albLink = a.attr("href");
                String albName = a.text();
                result.getAlbums().add(new Album(albName, baseLink+albLink, "", ""));
            }
        }
        for (Album album : result.getAlbums()){
            try {
                doc = Jsoup.connect(album.getLink()).get();
                Elements info = doc.getElementsByClass("profile");
                Elements divs = info.select("div");
                String genre = "";
                String year = "";
                for (int i = 0; i < divs.size(); i++){
                    Element div = divs.get(i);
                    if (div.text().equals("Genre:")){
                        i++;
                        genre = divs.get(i).text().split(",")[0];
                    } else if (div.text().equals("Year:")){
                        i++;
                        year = divs.get(i).text();
                    }
                }
                album.setYear(year);
                album.setGenre(genre);
                Elements songtable = doc.getElementsByClass("tracklist_track_title");
                songtable = songtable.select("span");
                for (int i = 0; i<songtable.size(); i++){
                    String title = songtable.get(i).text();
                    album.getTracks().add(new Track(i+1, title));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            //break;
        }
        return result;
    }

   public String searchSong(String sText, FirefoxDriver driver) {
        log.info("Search started: "+sText);
        sText = sText.replace(" ","%20");
        String sLink = baseSearch.concat(sText);
        try{
            driver.get(sLink);
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.elementToBeClickable(By.className("stripe-odd")));
            WebElement el = driver.findElement(By.id("liveaudio"));
            WebElement dwn = el.findElement(By.tagName("a"));
            String dwnLink = dwn.getAttribute("href");
            log.info("found :"+dwnLink+"\n"+"in "+sLink);
            sleep(1000);
            return dwnLink;
        } catch (Exception e){
            log.severe("error finding Download Link in "+sLink);
            //e.printStackTrace();
            try {
                sleep(1000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return "";
        }
    }
}
