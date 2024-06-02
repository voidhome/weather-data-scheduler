package app.restclient;

import app.dto.ForecastDto;
import app.dto.WeatherDto;

import java.util.Optional;

public interface OpenWeatherRestClient {

    Optional<WeatherDto> getWeatherDto(String city);

    Optional<ForecastDto> getForecastDto(String city);
}
