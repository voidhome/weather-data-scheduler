package app.service;

import app.dto.WeatherDto;
import app.table.CurrentWeather;

import java.util.Optional;

public interface CurrentWeatherService {

    Optional<CurrentWeather> createCurrentWeather(WeatherDto weatherData);
}
