package app.service;

import app.dto.ForecastDto;
import app.dto.response.WeatherForecastResponse;
import app.table.WeatherForecast;

import java.time.LocalDateTime;
import java.util.Optional;

public interface WeatherForecastService {

    WeatherForecastResponse getWeatherForecast(String city, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Optional<ForecastDto> getForecastDtoFromExternalApi(String city);

    Iterable<WeatherForecast> createWeatherForecast(String city);

    void deleteAllWeatherForecast();

    String analyzeWeatherForecast(WeatherForecastResponse response);
}
