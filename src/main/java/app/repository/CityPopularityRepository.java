package app.repository;

import app.table.CityPopularity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CityPopularityRepository extends ReactiveCrudRepository<CityPopularity, Integer> {

    Mono<CityPopularity> findByCity(String city);
}
