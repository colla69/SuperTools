package info.colarietitosti.supertools.frontend.ui;

import feign.Feign;
import info.colarietitosti.supertools.frontend.ui.feignClient.FeignClientConfiguration;
import info.colarietitosti.supertools.frontend.ui.feignClient.PingHomeConsumer;
import lombok.Getter;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@Log
@Getter
public class SuperGenericBean{

    @Autowired
    PingHomeConsumer pingHomeConsumer;

    @Value("${backend.ip}")
    String backIP;

    public GenericClient makeDataSource(Class<? extends GenericClient> type){
        String lastIP = pingHomeConsumer.getLastIP();
        String addr = "https://".concat("192.168.1.247").concat(":8443");
        if (!lastIP.equals("")){
            addr =  "https://".concat(lastIP).concat(":8443");
        }
        return Feign.builder()
                .client(FeignClientConfiguration.feignClient())
                .encoder(FeignClientConfiguration.feignEncoder())
                .decoder(FeignClientConfiguration.feignDecoder())
                .contract(FeignClientConfiguration.feignContract())
                .target(type, addr);
    }
}
