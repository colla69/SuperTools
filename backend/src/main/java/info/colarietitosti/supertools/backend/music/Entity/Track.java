package info.colarietitosti.supertools.backend.music.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Track {

    Integer no;
    String name;

    public Track(Integer no, String name) {
        this.no = no;
        this.name = name;
    }
}
