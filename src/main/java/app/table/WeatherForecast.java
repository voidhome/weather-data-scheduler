package app.table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Table("weather_forecast")
public class WeatherForecast implements Serializable {

    @Id
    private UUID uuid;

    private int cityId;
    private String city;
    private LocalDateTime forecastDateTime;
    private double temp;
    private Long windSpeed;
    private String description;
    private WeatherType weatherType;
}
