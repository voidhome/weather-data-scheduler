package app.mapper;

import app.dto.CityDto;
import app.dto.ForecastDetailsDto;
import app.dto.ForecastDto;
import app.dto.response.WeatherForecastResponse;
import app.table.WeatherForecast;
import app.table.WeatherType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Mapper(componentModel = "spring")
public interface WeatherForecastMapper {

    WeatherForecastMapper INSTANCE = Mappers.getMapper(WeatherForecastMapper.class);

    @Mappings({
            @Mapping(target = "city", source = "cityDto.name"),
            @Mapping(target = "cityId", source = "cityDto.id"),
            @Mapping(target = "forecastDateTime", source = "forecastDetailsDto.dt"),
            @Mapping(target = "temp", source = "forecastDetailsDto.main.temp"),
            @Mapping(target = "description", source = "forecastDetailsDto"),
            @Mapping(target = "weatherType", source = "forecastDetailsDto", qualifiedByName = "initializeWeatherType"),
            @Mapping(target = "windSpeed", source = "forecastDetailsDto.wind.speed")
    })
    WeatherForecast mapForecastDetailsDtoToWeatherForecast(ForecastDetailsDto forecastDetailsDto, CityDto cityDto);

    default List<WeatherForecast> map(ForecastDto forecastDto) {
        CityDto cityDto = forecastDto.city();
        return Optional.ofNullable(forecastDto)
                .map(dto -> dto.list().stream()
                        .map(detailsDto -> mapForecastDetailsDtoToWeatherForecast(detailsDto, cityDto))
                        .collect(toList()))
                .orElseGet(Collections::emptyList);
    }

    default String extractWeatherDescription(ForecastDetailsDto dto) {
        if (dto != null && !dto.weather().isEmpty()) {
            return dto.weather().get(0).description();
        }
        return null;
    }

    default LocalDateTime mapLongToLocalDateTime(Long epochSecond) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), ZoneId.systemDefault());
    }

    @Named("initializeWeatherType")
    default WeatherType initializeWeatherType(ForecastDetailsDto dto) {
        String description = extractWeatherDescription(dto);
        double temperature = dto.main().temp();

        if (description.contains("thunderstorm")) return WeatherType.THUNDERSTORM;
        else if (description.contains("snow")) return WeatherType.SNOW;
        else if (description.contains("rain")) return WeatherType.RAIN;
        else if (temperature < 10) return WeatherType.COLD;
        else if (temperature < 20) return WeatherType.MILD;
        else return WeatherType.WARM;
    }

    default WeatherForecastResponse toResponse(Iterable<WeatherForecast> weatherForecasts) {
        List<WeatherForecast> forecasts = new ArrayList<>();
        weatherForecasts.forEach(forecasts::add);

        return WeatherForecastResponse.builder()
                .forecasts(forecasts)
                .build();
    }

    WeatherForecastResponse toResponse(String message);
}