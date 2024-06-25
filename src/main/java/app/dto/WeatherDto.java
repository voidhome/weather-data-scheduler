package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WeatherDto(String name, TemperatureDto main, List<WeatherDetailsDto> weather) {
}
