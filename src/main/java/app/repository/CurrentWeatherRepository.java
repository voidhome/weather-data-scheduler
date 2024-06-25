package app.repository;

import app.table.CurrentWeather;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CurrentWeatherRepository extends CrudRepository<CurrentWeather, UUID> {
}
