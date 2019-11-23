package info.colarietitosti.supertools.backend.tools;

import com.mpatric.mp3agic.*;
import lombok.extern.java.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Log
public class Tagger {

    public void tagTracksRecursiveByPath(String rootPath){
        File root = new File(rootPath);
        try{
            Stream<Path> paths = Files.walk(Paths.get(rootPath));
            paths.filter(Files::isRegularFile)
                    .filter(f -> f.toString().endsWith(".mp3"))
                    .forEach(this::tagByPath);

            // creative renaming
            paths = Files.walk(Paths.get(rootPath));
            paths.filter(f -> f.toString().endsWith(".new"))
                    .forEach(f -> {
                        String oldpath = f.toString();
                        String newPath = oldpath.substring(0, oldpath.length()-4);
                        File newMp3 = new File(newPath);
                        File oldMp3 = new File(oldpath);
                        oldMp3.renameTo(newMp3);
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        List<File> files = Arrays.asList(root.listFiles());
        System.out.println(files);
    }

    private void tagByPath(Path path){
        final int len = path.getNameCount();
        Path infos = path.subpath(len-3, path.getNameCount());
        log.info(path.toString());
        log.info(infos.toString());

        List<String> s = Arrays.asList(infos.toString().split("/"));
        String artist = s.get(0);
        String albumString = s.get(1);

        String[] albInfo = albumString.split("_");
        log.info(albInfo.toString());

        String album = albInfo[0];
        String year = albInfo[1];
        String genre = albInfo[2];
        String fileName = s.get(2);
        String title = fileName.substring(2, fileName.length()-4);
        String no = fileName.substring(0,2);
        Mp3File mp3file = null;
        try {
            mp3file = new Mp3File(path.toString());
            if (mp3file.hasId3v1Tag()) {
                mp3file.removeId3v1Tag();
            }
            if (mp3file.hasCustomTag()) {
                mp3file.removeCustomTag();
            }
            ID3Wrapper newId3Wrapper = new ID3Wrapper(new ID3v1Tag(), new ID3v23Tag());
            newId3Wrapper.setArtist(artist);
            //newId3Wrapper.setGenre(genre);
            newId3Wrapper.setYear(year);
            newId3Wrapper.setAlbum(album);
            newId3Wrapper.setTitle(title);
            newId3Wrapper.setTrack(no);
            mp3file.setId3v1Tag(newId3Wrapper.getId3v1Tag());
            mp3file.setId3v2Tag(newId3Wrapper.getId3v2Tag());
            mp3file.save(path.toString()+".new");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedTagException | NotSupportedException e) {
            e.printStackTrace();
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }

    }
}
