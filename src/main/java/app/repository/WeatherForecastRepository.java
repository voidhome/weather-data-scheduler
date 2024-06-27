package app.repository;

import app.table.WeatherForecast;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface WeatherForecastRepository extends ReactiveCrudRepository<WeatherForecast, UUID> {

    Flux<WeatherForecast> findByCityAndForecastDateTimeBetween(String city, LocalDateTime startDateTime,
                                                               LocalDateTime endDateTime);
}
