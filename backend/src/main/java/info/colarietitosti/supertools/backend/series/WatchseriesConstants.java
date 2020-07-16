package info.colarietitosti.supertools.backend.series;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class WatchseriesConstants {

    public static final String VIDUP = "vidup";
    public static final String VIDTODO = "vidtodo";
    public static final String VSHARE = "vshare";
    public static final String VIDOZA = "vidoza";
    public static final String VIDLOX = "vidlox";
    public static final String VIDEOBIN = "videobin";

    public static final Set<String> allowedStreamServices = new HashSet<>(Arrays.asList(
            //VIDOZA,
            VIDTODO,
            //VIDUP,
            //VSHARE,
            VIDLOX,
            VIDEOBIN
    ));

    public static boolean isAllowedStream(String text) {
        return allowedStreamServices.stream().anyMatch(text::contains);
    }
}
