package app.dto.response;

import app.table.WeatherForecast;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
public class WeatherForecastResponse implements Serializable {

    private String message;
    private List<WeatherForecast> forecasts;
}

