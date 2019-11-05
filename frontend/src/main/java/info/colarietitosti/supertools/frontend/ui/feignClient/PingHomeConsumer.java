package info.colarietitosti.supertools.frontend.ui.feignClient;

import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Log @Getter
@RestController
public class PingHomeConsumer {

    @Autowired
    IpTrust ipTrust;

    private String lastIP = "";

    @RequestMapping(value = "/myNewIP", method = RequestMethod.POST)
    public String addTrustedIp(@RequestBody String newIP){

        String realIP = "";
        String loc = "";
        String referer = "";


        log.info("PingHome");
        List<String> mess = Arrays.asList(newIP.split("="));
        if (!mess.isEmpty()){
            String pass = mess.get(0);
            log.info(pass);
            if (pass.equals("penis")){
                String ip = mess.get(1);
                if (!ipTrust.getTrustedIPs().contains(pass)){
                    ipTrust.getTrustedIPs().add(ip);
                    lastIP = ip;
                    log.info("PingHome: adding ".concat(ip));
                    return String.valueOf(200);
                }
            }
        }
        return String.valueOf(200);
    }
}
