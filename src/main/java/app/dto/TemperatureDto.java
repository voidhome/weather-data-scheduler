package app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TemperatureDto(@JsonProperty("feels_like") double feelsLike, @JsonProperty("temp_min") double tempMin,
                             @JsonProperty("temp_max") double tempMax, double temp, int pressure, int humidity) {
}
