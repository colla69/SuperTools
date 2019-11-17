package info.colarietitosti.supertools.backend.visitors;

/*
{"id":156,
"ip_addr":"127.0.0.1",
"locale":"en_US",
"location":"Welcome",
"referer":null,
"timestamp":"2019-06-03T16:35:08.000+0000"}
 */

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter @Setter
public class VisitorIps {

    private Integer id;
    private String ip_addr;
    private String locale;
    private String location;
    private String referer;
    private Date timestamp;
}
