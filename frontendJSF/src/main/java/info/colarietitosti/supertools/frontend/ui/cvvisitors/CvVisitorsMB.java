package info.colarietitosti.supertools.frontend.ui.cvvisitors;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;
import org.bouncycastle.asn1.misc.MiscObjectIdentifiers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Log
@Getter
@Setter
@ManagedBean("cvVisitorsMB")
@Scope("prototype")
public class CvVisitorsMB {

    @Autowired
    CvVisitorsClient visitorsClient;

    private List<CvVisitor> visitors;

    @PostConstruct
    public void init(){
        visitors = visitorsClient.getAll();
        visitors = visitors.stream().sorted(new Comparator<CvVisitor>() {
            @Override
            public int compare(CvVisitor o1, CvVisitor o2) {
                return o1.getTimestamp().compareTo(o2.getTimestamp())*-1;
            }
        }).collect(Collectors.toList());
    }

}
