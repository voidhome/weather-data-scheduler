package app.service;

import app.dto.ForecastDto;
import app.dto.response.WeatherForecastResponse;
import app.table.WeatherForecast;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface WeatherForecastService {

    Mono<WeatherForecastResponse> getWeatherForecast(String city, LocalDateTime startDateTime, LocalDateTime endDateTime);

    Mono<ForecastDto> getForecastDtoFromExternalApi(String city);

    Flux<WeatherForecast> createWeatherForecast(String city);

    Mono<Void> deleteAllWeatherForecast();

    Mono<String> analyzeWeatherForecast(Mono<WeatherForecastResponse> response);
}
