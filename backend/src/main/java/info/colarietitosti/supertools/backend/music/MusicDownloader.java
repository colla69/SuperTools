package info.colarietitosti.supertools.backend.music;

import info.colarietitosti.supertools.backend.config.BackendConfiguration;
import info.colarietitosti.supertools.backend.config.profiling.Profiled;
import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.music.Entity.Album;
import info.colarietitosti.supertools.backend.music.Entity.Artist;
import info.colarietitosti.supertools.backend.music.Entity.Track;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.FirefoxDriverUtils;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Slf4j
@Profiled
@Component
public class MusicDownloader {

    public static final String MP3_EXTENTION = ".mp3";
    public static final String SEPARATOR = "_";
    public static final String PATH_SEPARATOR = "/";
    public static final String UNKNOWN = "Unknown";
    public static final String DOWNLOADS_PATH = "downloads" + PATH_SEPARATOR;

    @Autowired
    DownloadQueue downloadQueue;

    @Autowired
    BackendConfiguration config;

    @Autowired
    MusicSearchService musicSearchService;

    public void downloadArtistAndTag(String artist, String linkPart) {
        log.info("starting music download");
        downloadArtistDiscography(artist, linkPart);
        log.info("music download terminated");

        tag(artist);
    }

    public void downloadAlbumAndTag(String artist, String album, String linkpart) {
        log.info("starting music download");
        downloadAlbumFromLink(artist, album, linkpart);
        log.info("music download terminated");

        tag(artist);
    }

    public void downloadSingleTrackAndTag(String artist, String album, String title) {
        log.info("starting music download");
        downloadSingleTrack(artist, album, title);
        log.info("music download terminated");

        tag(artist);
    }

    public void tag(String artist) {
        log.info("starting tag process");
        Tagger tagger = new Tagger();
        tagger.tagTracksRecursiveByPath(config.getMusicOutPath()
                .concat(DOWNLOADS_PATH).concat(artist).concat(PATH_SEPARATOR));
        log.info("tag process terminated");
    }

    private void downloadArtistDiscography(String artistName, String linkPart) {
        log.info("searching artist {}", artistName);
        Artist artist = musicSearchService.searchArtist(artistName, linkPart);
        log.info("done!\n");

        for (Album album : artist.getAlbums()) {
            downloadAlbum(artist, album);
            waitForDownloadsToFinish();
        }
    }

    private void downloadAlbumFromLink(String artistName, String albumName, String linkPart) {
        log.info("searching album {}", albumName);
        Album album = new Album(albumName, linkPart, "", "");
        musicSearchService.searchAlbum(album, linkPart);
        log.info("done!\n");

        Artist artist = new Artist(artistName, "");
        downloadAlbum(artist, album);
        waitForDownloadsToFinish();
    }

    private void downloadAlbum(Artist artist, Album album) {
        album.getTracks().forEach(track -> {
            downloadTrack(artist, album, track);
        });
    }

    private void downloadTrack(Artist artist, Album album, Track track) {
        FirefoxDriver driver = null;
        try {
            driver = FirefoxDriverUtils.getFirefoxDriverHeadless();
        } catch (Exception e) {
            return;
        }
        String link = musicSearchService.searchSong(artist.getName() + " " + track.getName(), driver);
        FirefoxDriverUtils.killDriver(driver);
        if (link.equals("")) {
            return;
        }
        String downPath = getDownloadPath(artist, album);
        downloadQueue.put(new FileDownloader(link, downPath, getMp3Name(track), ""));
    }

    private String getMp3Name(Track track) {
        if (track.getNo() != null) {
            return track.getNo() + " - " + track.getName().concat(MP3_EXTENTION);
        }
        return track.getName().concat(MP3_EXTENTION);
    }

    private String getDownloadPath(Artist artist, Album album) {
        return config.getMusicOutPath().concat(DOWNLOADS_PATH) +
                artist.getName() + PATH_SEPARATOR + album.getName() + SEPARATOR + album.getYear() + PATH_SEPARATOR;
    }

    private void waitForDownloadsToFinish() {
        while (downloadQueue.getRunning()) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void downloadSingleTrack(String artist, String album, String title) {
        if (album == null) {
            album = UNKNOWN;
        }
        downloadTrack(
                new Artist(artist, ""),
                new Album(album, "", "", ""),
                new Track(null, title)
        );
        waitForDownloadsToFinish();
    }
}
