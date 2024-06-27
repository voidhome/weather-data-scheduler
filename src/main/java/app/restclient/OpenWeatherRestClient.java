package app.restclient;

import app.dto.ForecastDto;
import app.dto.WeatherDto;
import reactor.core.publisher.Mono;

public interface OpenWeatherRestClient {

    Mono<WeatherDto> getWeatherDto(String city);

    Mono<ForecastDto> getForecastDto(String city);
}
