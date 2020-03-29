package info.colarietitosti.supertools.backend.config.series;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeriesConfigRepository extends JpaRepository<SeriesConfig,Long> {
}
