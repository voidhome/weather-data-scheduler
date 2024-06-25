package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ForecastDetailsDto(Long dt, TemperatureDto main, List<WeatherDetailsDto> weather, WindDto wind) {
}
