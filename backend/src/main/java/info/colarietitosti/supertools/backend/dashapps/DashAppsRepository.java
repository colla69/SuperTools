package info.colarietitosti.supertools.backend.dashapps;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DashAppsRepository extends JpaRepository<DashApp, Long> {

}