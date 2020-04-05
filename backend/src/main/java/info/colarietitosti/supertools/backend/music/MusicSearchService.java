package info.colarietitosti.supertools.backend.music;

import info.colarietitosti.supertools.backend.music.Entity.Artist;
import info.colarietitosti.supertools.backend.music.services.DiscogsService;
import info.colarietitosti.supertools.backend.music.services.SliderKzService;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.stereotype.Service;

@Service
public class MusicSearchService {

    DiscogsService discogsService = new DiscogsService();
    SliderKzService sliderKzService = new SliderKzService();

    public Artist searchArtist(String artist, String artistLink){
        return discogsService.searchArtist(artist, artistLink);
    }

   public String searchSong(String sText, FirefoxDriver driver) {
        return sliderKzService.searchSong(sText, driver);
    }
}
