package app.repository;

import app.table.WeatherForecast;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface WeatherForecastRepository extends CrudRepository<WeatherForecast, UUID> {

    @Query("SELECT * FROM weather_forecast WHERE city = :city AND forecast_date_time BETWEEN :startDateTime AND :endDateTime")
    List<WeatherForecast> findWeatherForecastByCityAndDateTime(String city, LocalDateTime startDateTime,
                                                               LocalDateTime endDateTime);
}
