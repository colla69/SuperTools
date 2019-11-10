package info.colarietitosti.supertools.backend.tools.Music;

import info.colarietitosti.supertools.backend.downloaderQueue.DownloadQueue;
import info.colarietitosti.supertools.backend.tools.FileDownloader;
import info.colarietitosti.supertools.backend.tools.Music.Entity.Album;
import info.colarietitosti.supertools.backend.tools.Music.Entity.Artist;
import info.colarietitosti.supertools.backend.tools.Tagger;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.Thread.sleep;

@Log
@Component
public class MusicDownload {

    @Autowired
    DownloadQueue downloadQueue;

    public void searchAndDownload(){
        downloadArtistDiscography("Romare","2662253-Romare");
        Tagger tagger = new Tagger();
        tagger.tagTracksRecursiveByPath("downloads/");
    }

    public void downloadArtistDiscography(String artistName, String linkPart) {
        MusicSearch musicSearch = new MusicSearch();
        log.info("downloading data");
        Artist artist = musicSearch.searchArtist(artistName, linkPart);
        log.info("done!\n");
        for (Album album : artist.getAlbums()){
            album.getTracks().parallelStream().forEach( track -> {
                try {
                    String link = musicSearch.searchSong(artist.getName()+" "+track.getName());
                    if (link.equals("")){
                        return;
                    }
                    sleep(1000);
                    String separator = "_";
                    String pathSeparator = "/";
                    String downPath = "downloads/".concat(artist.getName()).concat(pathSeparator).concat(album.getName())
                            .concat(separator).concat(album.getYear()).concat(separator).concat(album.getGenre()).concat(pathSeparator);
                    downloadQueue.put(new FileDownloader(link, downPath, track.getNo()+" "+track.getName().concat(".mp3"),""));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}
