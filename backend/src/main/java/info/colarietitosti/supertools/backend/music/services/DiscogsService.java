package info.colarietitosti.supertools.backend.music.services;

import info.colarietitosti.supertools.backend.music.Entity.Album;
import info.colarietitosti.supertools.backend.music.Entity.Artist;
import info.colarietitosti.supertools.backend.music.Entity.Track;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class DiscogsService {

    private final String baseLink = "https://www.discogs.com";

    public Artist searchArtist(String artist, String artistLink){

        Artist result = new Artist(artist, artistLink);
        Document doc = null;
        try {
            doc = Jsoup.connect(baseLink+"/artist/"+artistLink).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element table = doc.getElementById("artist");
        Elements rows = table.select("tr");
        for (int i = 0; i < rows.size(); i++) {
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
                    if (div.text().toLowerCase().contains("genre")){
                        i++;
                        genre = divs.get(i).text().split(",")[0];
                        //log.info("genre:"+genre);
                    } else if (
                            div.text().toLowerCase().contains("year") ||
                                    div.text().toLowerCase().contains("released")
                    ){
                        i++;
                        year = divs.get(i).text();
                        //log.info("year:"+year);
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
}
