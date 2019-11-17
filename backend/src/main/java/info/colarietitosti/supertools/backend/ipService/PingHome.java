package info.colarietitosti.supertools.backend.ipService;

import lombok.extern.java.Log;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UnsupportedEncodingException;


@Slf4j
@Component
public class PingHome {

    @Scheduled(fixedDelay = 600000)
    public void pingServer(){
        log.info("pinging server.. ");
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost("https://admin.colarietitosti.info/ips/myNewIP");
        try {
            StringEntity httpbody = new StringEntity("penis=lang");
            httppost.setEntity(httpbody);
            HttpResponse response = httpclient.execute(httppost);
            log.info("success! ".concat(response.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
