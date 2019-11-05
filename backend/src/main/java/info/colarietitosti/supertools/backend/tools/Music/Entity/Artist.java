package info.colarietitosti.supertools.backend.tools.Music.Entity;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class Artist {

    List<Album> albums = new ArrayList<>();
    String name = "";
    String link = "";

    public Artist(String name, String link) {
        this.name = name;
        this.link = link;
    }
}
