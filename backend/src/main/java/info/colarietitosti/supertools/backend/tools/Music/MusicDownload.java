package info.colarietitosti.supertools.backend.tools.Music;

import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.tools.Config;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.FirefoxDriverFactory;
import info.colarietitosti.supertools.backend.tools.Music.Entity.Album;
import info.colarietitosti.supertools.backend.tools.Music.Entity.Artist;
import info.colarietitosti.supertools.backend.tools.Tagger;
import lombok.extern.java.Log;
import org.openqa.selenium.SessionNotCreatedException;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import static java.lang.Thread.sleep;

@Log
@Component
public class MusicDownload {

    @Autowired
    DownloadQueue downloadQueue;

    @Autowired
    Config config;

    @Autowired
    FirefoxDriverFactory firefoxDriverFactory;

    public void downloadAndTag(String artist,  String linkPart){
        log.info("starting music download");
        downloadArtistDiscography(artist, linkPart);
        log.info("starting tag process");
        Tagger tagger = new Tagger();
        tagger.tagTracksRecursiveByPath(config.getMusicOutPath().concat("downloads/").concat(artist).concat("/"));
        log.info("music download terminated");
    }

    public void downloadArtistDiscography(String artistName, String linkPart){
        log.info("searching arstist ".concat(artistName));
        MusicSearch musicSearch = new MusicSearch();
        Artist artist = musicSearch.searchArtist(artistName, linkPart);
        log.info("done!\n");
        for (Album album : artist.getAlbums()){
            album.getTracks().stream().forEach( track -> {
                FirefoxDriver driver = null;
                try {
                    driver = firefoxDriverFactory.getFirefoxDriverHeadless();
                } catch (Exception e) {
                    return;
                }
                String link = musicSearch.searchSong(artist.getName()+" "+track.getName(), driver);
                try { driver.close(); driver.quit(); } catch (Exception ex) {}
                if (link.equals("")){
                    return;
                }
                String separator = "_";
                String pathSeparator = "/";
                String downPath = config.getMusicOutPath().concat("downloads/").concat(artist.getName()).concat(pathSeparator).concat(album.getName())
                        .concat(separator).concat(album.getYear()).concat(pathSeparator);
                        //.concat(separator).concat(album.getYear()).concat(separator).concat(album.getGenre()).concat(pathSeparator);
                downloadQueue.put(new FileDownloader(link, downPath, track.getNo()+" "+track.getName().concat(".mp3"),""));
            });
            while (downloadQueue.getRunning()){
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
