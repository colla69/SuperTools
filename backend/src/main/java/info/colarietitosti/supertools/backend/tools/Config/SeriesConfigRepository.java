package info.colarietitosti.supertools.backend.tools.Config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesConfigRepository extends JpaRepository<SeriesConfigE,Long> {
}
