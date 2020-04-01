package info.colarietitosti.supertools.backend.music.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class Album {

    List<Track> tracks = new ArrayList<>();
    String name = "";
    String link = "";
    String genre = "";
    String year = "";

    public Album(String name, String link, String genre, String year) {
        this.name = name;
        this.link = link;
        this.genre = genre;
        this.year = year;
    }
}
