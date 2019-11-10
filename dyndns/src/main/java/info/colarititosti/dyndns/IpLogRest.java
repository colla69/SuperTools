package info.colarititosti.dyndns;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class IpLogRest {

    @Autowired
    IpLogRepository ipLogRepository;

    @RequestMapping(method = RequestMethod.GET, value="/IpLog")
    @ResponseBody
    public List<IpLog> getAllApps(){
        return ipLogRepository.findAll();
    }

    @RequestMapping(value = "/myNewIP", method = RequestMethod.POST)
    public String addTrustedIp(@RequestBody String newIP, HttpServletRequest request){
        String realIP = "";
        String loc = "";
        String referer = "";

        System.out.println("PingHome");
        List<String> mess = Arrays.asList(newIP.split("="));
        if (!mess.isEmpty()){
            String pass = mess.get(0);
            String ip = request.getRemoteAddr();
            List<String> knownIps = ipLogRepository.findAll()
                    .stream().map(IpLog::getIp).collect(Collectors.toList());
            if (!knownIps.contains(ip)) {
                IpLog newip = new IpLog();
                newip.setIp(ip);
                newip.setTime(new Date(System.currentTimeMillis()));
                ipLogRepository.save(newip);
                System.out.println("saving new ip: "+request.getRemoteAddr());
            }
        }
        return String.valueOf(200);
    }

}
