package app.service;

import app.dto.ForecastDto;
import app.table.WeatherForecast;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherForecastService {

    List<WeatherForecast> getWeatherForecast(String city, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Iterable<WeatherForecast> createWeatherForecast(ForecastDto forecastDto);

    void deleteAllWeatherForecast();

    String analyzeWeatherForecast(List<WeatherForecast> weatherForecasts);
}
