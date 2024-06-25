package app.table;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Table("current_weather")
public record CurrentWeather(@Id UUID uuid, String city, String description, double temp, LocalDateTime placedAt) {
}


