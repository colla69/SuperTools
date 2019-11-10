package info.colarititosti.dyndns;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IpLogRepository extends JpaRepository<IpLog, Long> {



}
