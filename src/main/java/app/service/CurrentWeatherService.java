package app.service;

import app.dto.WeatherDto;
import app.table.CurrentWeather;
import reactor.core.publisher.Mono;

public interface CurrentWeatherService {

    Mono<CurrentWeather> createCurrentWeather(WeatherDto weatherData);
}
