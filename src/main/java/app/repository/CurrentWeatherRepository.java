package app.repository;

import app.table.CurrentWeather;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CurrentWeatherRepository extends ReactiveCrudRepository<CurrentWeather, UUID> {
}
