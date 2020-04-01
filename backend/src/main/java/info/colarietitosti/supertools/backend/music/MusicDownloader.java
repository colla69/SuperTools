package info.colarietitosti.supertools.backend.music;

import info.colarietitosti.supertools.backend.config.BackendConfigutation;
import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.music.Entity.Album;
import info.colarietitosti.supertools.backend.music.Entity.Artist;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.FirefoxDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Slf4j
@Component
public class MusicDownloader {

    @Autowired
    DownloadQueue downloadQueue;

    @Autowired
    BackendConfigutation config;

    @Autowired
    MusicSearchService musicSearchService;

    public void downloadAndTag(String artist,  String linkPart){
        download(artist, linkPart);
        tag(artist);
    }

    public void download(String artist,  String linkPart){
        log.info("starting music download");
        downloadArtistDiscography(artist, linkPart);
        log.info("music download terminated");
    }

    public void tag(String artist){
        log.info("starting tag process");
        Tagger tagger = new Tagger();
        tagger.tagTracksRecursiveByPath(config.getMusicOutPath().concat("downloads/").concat(artist).concat("/"));
        log.info("tag process terminated");
    }

    public void downloadArtistDiscography(String artistName, String linkPart){
        log.info("searching arstist ".concat(artistName));

        Artist artist = loadArtistInfo(artistName, linkPart);

        log.info("done!\n");
        for (Album album : artist.getAlbums()){
            album.getTracks().stream().forEach( track -> {
                FirefoxDriver driver = null;
                try {
                    driver = FirefoxDriverUtils.getFirefoxDriverHeadless();
                } catch (Exception e) {
                    return;
                }
                String link = musicSearchService.searchSong(artist.getName()+" "+track.getName(), driver);
                FirefoxDriverUtils.killDriver(driver);
                if (link.equals("")){
                    return;
                }
                String downPath = getDownloadPath(artist, album);

                downloadQueue.put(new FileDownloader(link, downPath, track.getNo()+" "+track.getName().concat(".mp3"),""));
            });
            waitForDownloadsToFinish();
        }
    }

    private String getDownloadPath(Artist artist, Album album) {
        String separator = "_";
        String pathSeparator = "/";
        return config.getMusicOutPath().concat("downloads/").concat(artist.getName()).concat(pathSeparator).concat(album.getName())
                .concat(separator).concat(album.getYear()).concat(pathSeparator);
    }

    private Artist loadArtistInfo(String artistName, String linkPart) {
        return musicSearchService.searchArtist(artistName, linkPart);
    }

    private void waitForDownloadsToFinish() {
        while (downloadQueue.getRunning()){
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
