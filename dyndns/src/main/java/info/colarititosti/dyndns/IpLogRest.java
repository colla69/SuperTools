package info.colarititosti.dyndns;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    public String addTrustedIp(@RequestBody String newIP, HttpServletRequest request) {
        String realIP = "";
        String loc = "";
        String referer = "";
        System.out.println("PingHome");
        List<String> mess = Arrays.asList(newIP.split("="));
        if (!mess.isEmpty()){
            String pass = mess.get(0);
            String ip = request.getHeader("X-Real-IP");
            List<String> knownIps = ipLogRepository.findAll()
                    .stream().map(IpLog::getIp).collect(Collectors.toList());

            if (!knownIps.contains(ip)) {
                IpLog newip = new IpLog();
                newip.setIp(ip);
                newip.setTime(new Date(System.currentTimeMillis()));
                ipLogRepository.save(newip);
                System.out.println("saving new ip: "+ip);
                ProcessBuilder processBuilder = new ProcessBuilder();

                String newNginxConf = String.format(nginxConf,ip,ip);
                System.out.println( newNginxConf );
                System.out.println("reconfiguring nginx... ");
                //this.execShellCmd("rm /etc/nginx/conf.d/dash.colarietitosti.info.conf");
                String reconfCmd = String.format(
                        "echo \"%s\" > /etc/nginx/conf.d/dash.colarietitosti.info.conf", newNginxConf);
                System.out.println( reconfCmd );
                this.execShellCmd( reconfCmd );
                this.execShellCmd("nginx -s reload");
                System.out.println("done! ");
            }
        }
        return String.valueOf(200);
    }

    private void execShellCmd(String cmd){
        ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command("bash", "-c", cmd);
        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private  String nginxConf =
    "server {\n" +
            "        listen 443;\n" +
            "        server_name dash.colarietitosti.info;\n" +
            "\n" +
            "        ssl_session_timeout 5m;\n" +
            "        ssl_ciphers ECDHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES128-GCM-SHA256:DHE-RSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-SHA384:ECDHE-RSA-AES128-SHA256:ECDHE-RSA-AES256-SHA:ECDHE-RSA-AES128-SHA:DHE-RSA-AES256-SHA:DHE-RSA-AES128-SHA;\n" +
            "        ssl_session_cache shared:SSL:50m;\n" +
            "        ssl_dhparam /etc/letsencrypt/certs/dhparam.pem;\n" +
            "        ssl_prefer_server_ciphers on;\n" +
            "\n" +
            "        ssl_certificate /etc/letsencrypt/live/dash.colarietitosti.info/fullchain.pem; # managed by Certbot\n" +
            "        ssl_certificate_key /etc/letsencrypt/live/dash.colarietitosti.info/privkey.pem; # managed by Certbot\n" +
            "\n" +
            "        location /backend {\n" +
            "                proxy_set_header X-Real-IP  \\$remote_addr;\n" +
            "                proxy_set_header X-Forwarded-For \\$remote_addr;\n" +
            "                proxy_set_header Host \\$host;\n" +
            "                proxy_pass  https://%s:8443;\n" +
            "                proxy_redirect off;\n" +
            "        }\n" +
            "\n" +
            "        location /jupyter {\n" +
            "                proxy_set_header X-Real-IP  \\$remote_addr;\n" +
            "                proxy_set_header X-Forwarded-For \\$remote_addr;\n" +
            "                proxy_set_header Host \\$host;\n" +
            "                proxy_pass  https://%s:89;\n" +
            "                proxy_redirect off;\n" +
            "        }\n" +
            "\n" +
            "}\n" +
            "\n" +
            "server {\n" +
            "    if (\\$host = dash.colarietitosti.info) {\n" +
            "        return 301 https://\\$host\\$request_uri;\n" +
            "    } # managed by Certbot\n" +
            "\n" +
            "\n" +
            "  listen 80;\n" +
            "#  listen 443;\n" +
            "  server_name dash.colarietitosti.info;\n" +
            "\n" +
            "\n" +
            "  return 301 https://dash.colarietitosti.info\\$request_uri;\n" +
            "\n" +
            "\n" +
            "\n" +
            "}\n";
}
