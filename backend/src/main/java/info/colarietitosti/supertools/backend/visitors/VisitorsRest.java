package info.colarietitosti.supertools.backend.visitors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

@Log
@Controller
public class VisitorsRest {

    @GetMapping("/visitors")
    @ResponseBody
    public List<VisitorIps> getAllApps() {
        List<VisitorIps> result = null;
        URL url = null;
        try {
            Set<VisitorIps> visitorIps = getVisitorIps();
            Set<MyIps> myIps = getMyIps();
            List<String> myKnownIps = myIps.parallelStream().map(MyIps::getIp).collect(Collectors.toList());
            result = visitorIps.parallelStream()
                    .filter(v -> !v.getIp_addr().startsWith("66.249"))
                    .filter(v -> !v.getIp_addr().isEmpty())
                    .filter(v -> !myKnownIps.contains(v.getIp_addr()))
                    .sorted(new Comparator<VisitorIps>() {
                        @Override
                        public int compare(VisitorIps o1, VisitorIps o2) {
                            //time asc
                            return o2.getTimestamp().compareTo(o1.getTimestamp());
                        }
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private Set<VisitorIps> getVisitorIps() throws IOException {
        URL url;
        url = new URL("https://cv.colarietitosti.info/visitors");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        String content = con.getContent().toString();
        con.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(url, new TypeReference<Set<VisitorIps>>() {});
    }

    private Set<MyIps> getMyIps() throws IOException {
        URL url;
        url = new URL("https://admin.colarietitosti.info/ips/IpLog");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.connect();
        String content = con.getContent().toString();
        con.disconnect();

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(url, new TypeReference<Set<MyIps>>() {});
    }
}
