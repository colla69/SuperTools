package info.colarietitosti.supertools.frontend.ui.feignClient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter @Setter
@Service
public class IpTrust {

    private Set<String> trustedIPs = new HashSet<>();

}
