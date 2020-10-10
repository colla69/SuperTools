package info.colarietitosti.supertools.backend.music.services;

import info.colarietitosti.supertools.backend.music.Entity.Album;
import info.colarietitosti.supertools.backend.music.Entity.Artist;
import info.colarietitosti.supertools.backend.music.Entity.Track;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class DiscogsService {

    public static final String YEAR = "year";
    public static final String RELEASED = "released";
    public static final String GENRE = "genre";
    public static final String DIV = "div";
    public static final String PROFILE = "profile";
    public static final String TRACKLIST_TRACK_TITLE = "tracklist_track_title";
    public static final String SPAN = "span";
    public static final String ARTIST = "artist";
    public static final String TITLE = "title";
    public static final String A = "a";
    public static final String HREF = "href";
    public static final String TR = "tr";
    private final String baseLink = "https://www.discogs.com";

    public Artist searchArtist(String artist, String artistLink){

        Artist result = new Artist(artist, artistLink);
        Document doc = null;
        try {
            doc = Jsoup.connect(baseLink+"/artist/"+artistLink).get();
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
        Element table = doc.getElementById(ARTIST);
        Elements rows = table.select(TR);
        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            Elements cols = row.getElementsByClass(TITLE);
            for (Element c : cols){
                Element a = c.select(A).first();
                String albLink = a.attr(HREF);
                String albName = a.text();
                result.getAlbums().add(new Album(albName, baseLink+albLink, "", ""));
            }
        }
        for (Album album : result.getAlbums()){
            searchAlbum(album, album.getLink());
        }
        return result;
    }

    public Album searchAlbum(Album album, String albumLink) {
        Document doc;
        try {
            doc = Jsoup.connect(albumLink).get();
            Elements info = doc.getElementsByClass(PROFILE);
            Elements divs = info.select(DIV);
            String genre = "";
            String year = "";
            for (int i = 0; i < divs.size(); i++){
                Element div = divs.get(i);
                if (div.text().toLowerCase().contains(GENRE)){
                    i++;
                    genre = divs.get(i).text().split(",")[0];
                } else if (
                        div.text().toLowerCase().contains(YEAR) ||
                                div.text().toLowerCase().contains(RELEASED)
                ){
                    i++;
                    year = divs.get(i).text();
                }
            }
            album.setYear(year);
            album.setGenre(genre);
            Elements songtable = doc.getElementsByClass(TRACKLIST_TRACK_TITLE);
            songtable = songtable.select(SPAN);
            for (int i = 0; i<songtable.size(); i++){
                String title = songtable.get(i).text();
                album.getTracks().add(new Track(i+1, title));
            }
            return album;
        } catch (IOException e) {
            log.error(e.getMessage());
            return null;
        }
    }
}
