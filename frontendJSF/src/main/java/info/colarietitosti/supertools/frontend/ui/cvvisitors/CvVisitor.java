package info.colarietitosti.supertools.frontend.ui.cvvisitors;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.Date;
@Getter @Setter
public class CvVisitor {

    private int id;
    private String ip_addr;
    private String locale;
    private String location;
    private String referer;
    private Date timestamp;
}

