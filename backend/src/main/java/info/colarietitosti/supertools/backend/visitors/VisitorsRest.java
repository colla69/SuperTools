package info.colarietitosti.supertools.backend.visitors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.colarietitosti.supertools.backend.config.profiling.Profiled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Profiled
@Controller
public class VisitorsRest {

    public static final String MY_IPS_API = "https://admin.colarietitosti.info/ips/IpLog";
    public static final String VISITORS_API = "https://cv.colarietitosti.info/visitors";

    @GetMapping("/visitors")
    @ResponseBody
    public List<VisitorIps> getAllApps() {
        try {
            Set<VisitorIps> visitorIps = getVisitorIps();
            Set<MyIps> myIps = getMyIps();
            List<String> myKnownIps = myIps.parallelStream().map(MyIps::getIp).collect(Collectors.toList());

            return filterOutKnownIps(visitorIps, myKnownIps);
        } catch (IOException e) {
            log.error("could not get visitors list",e);
        }
        return null;
    }

    private List<VisitorIps> filterOutKnownIps(Set<VisitorIps> visitorIps, List<String> myKnownIps) {
        return visitorIps.parallelStream()
                .filter(v -> !v.getIp_addr().startsWith("66.249"))
                .filter(v -> !v.getIp_addr().isEmpty())
                .filter(v -> !myKnownIps.contains(v.getIp_addr()))
                .sorted((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()))
                .collect(Collectors.toList());
    }

    private Set<VisitorIps> getVisitorIps() throws IOException {
        URL url = new URL(VISITORS_API);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(url, new TypeReference<Set<VisitorIps>>() {});
    }

    private Set<MyIps> getMyIps() throws IOException {
        URL url = new URL(MY_IPS_API);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.readValue(url, new TypeReference<Set<MyIps>>() {});
    }
}
