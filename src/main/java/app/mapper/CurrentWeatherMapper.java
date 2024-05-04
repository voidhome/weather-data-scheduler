package app.mapper;

import app.dto.WeatherDto;
import app.table.CurrentWeather;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CurrentWeatherMapper {

    CurrentWeatherMapper INSTANCE = Mappers.getMapper(CurrentWeatherMapper.class);

    @Mappings({
            @Mapping(target = "description", source = "dto"),
            @Mapping(target = "temp", source = "main.temp"),
            @Mapping(target = "city", source = "dto.name"),
            @Mapping(target = "placedAt", expression = "java(java.time.LocalDateTime.now())")
    })
    CurrentWeather map(WeatherDto dto);

    default String extractWeatherDescription(WeatherDto dto) {
        if (dto != null && !dto.weather().isEmpty()) {
            return dto.weather().get(0).description();
        }
        return null;
    }
}
