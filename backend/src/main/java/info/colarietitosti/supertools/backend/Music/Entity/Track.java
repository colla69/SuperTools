package info.colarietitosti.supertools.backend.Music.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Track {

    int no;
    String name;

    public Track(int no, String name) {
        this.no = no;
        this.name = name;
    }
}
