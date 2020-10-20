package info.colarietitosti.supertools.backend.music;

import com.mpatric.mp3agic.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class Tagger {

    public void tagTracksRecursiveByPath(String rootPath) {
        File root = new File(rootPath);
        if (root.exists()){
            try {
                Stream<Path> paths = Files.walk(Paths.get(rootPath));
                paths.filter(Files::isRegularFile)
                        .filter(f -> f.toString().endsWith(".mp3"))
                        .forEach(this::tagByPath);

                restoreNameAfterTagging(rootPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            List<File> files = Arrays.asList(root.listFiles());
            System.out.println(files);
        } else {
            log.error("can't tag empty path!! ");
        }
    }

    private void tagByPath(Path path) {

        if (path != null) {
            List<String> splittedInfo = splittedInfosFromPath(path);

            String artist = splittedInfo.get(0);
            String albumInfo = splittedInfo.get(1);
            String[] splittedAlbumInfo = albumInfo.split("_");
            String album = splittedAlbumInfo[0];
            String year = "";
            try {
                year = splittedAlbumInfo[1];
            } catch (Exception e) {
                log.error("year not found");
            }
            // String genre = albInfo[2];
            String fileName = splittedInfo.get(2);

            String title = "";
            String no = "";
            if (fileName.contains("_")){
                no = fileName.substring(0, 2);
                title = fileName.substring(2, fileName.length() - 4);
            } else {
                title = fileName.substring(0, fileName.length() - 4);
            }

            saveNewFileWithTags(path, artist, album, year, title, no);
        }
    }

    private List<String> splittedInfosFromPath(Path path) {
        final int len = path.getNameCount();
        return Arrays.asList(path.subpath(len - 3, path.getNameCount()).toString().split("/"));
    }

    private void saveNewFileWithTags(Path path, String artist, String album, String year, String title, String no){
        Mp3File mp3file;
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
            mp3file.save(path.toString() + ".new");

        } catch (IOException | IllegalArgumentException e) {
            log.error(e.getMessage());
        } catch (UnsupportedTagException | NotSupportedException e) {
            log.error(e.getMessage());
        } catch (InvalidDataException e) {
            log.error(e.getMessage());
        }
    }

    private void restoreNameAfterTagging(String rootPath) throws IOException {
        Stream<Path> paths;
        paths = Files.walk(Paths.get(rootPath));
        paths.filter(file -> file.toString().endsWith(".new"))
            .forEach(file -> {
                String oldpath = file.toString();
                String newPath = oldpath.substring(0, oldpath.length() - 4);
                File newMp3 = new File(newPath);
                File oldMp3 = new File(oldpath);
                oldMp3.renameTo(newMp3);
            });
    }
}
