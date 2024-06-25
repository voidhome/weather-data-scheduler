package app.service.impl;

import app.dto.ForecastDto;
import app.dto.response.WeatherForecastResponse;
import app.mapper.WeatherForecastMapper;
import app.repository.WeatherForecastRepository;
import app.restclient.OpenWeatherRestClient;
import app.service.CityPopularityService;
import app.service.WeatherForecastService;
import app.table.WeatherForecast;
import app.table.WeatherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherForecastServiceImpl implements WeatherForecastService {

    private final OpenWeatherRestClient restClient;
    private final WeatherForecastRepository forecastRepository;
    private final CityPopularityService cityPopularityService;

    @Value("${weather.forecast-after-limit-message}")
    private String forecastAfterLimitMessage;

    @Value("${weather.forecast-before-limit-message}")
    private String forecastBeforeLimitMessage;

    @Override
    @Transactional
    @Cacheable(value = "dailyCache", key = "#city", condition = "@cityPopularityServiceImpl.isCityPopular(#city)",
            unless = "!#result.forecasts.iterator().next().forecastDateTime.toLocalDate().isEqual(T(java.time.LocalDate).now())")
    public WeatherForecastResponse getWeatherForecast(String city, LocalDateTime startDateTime,
                                                      LocalDateTime endDateTime) {
        log.info("Запрос прогноза погоды для города {} с {} по {}", city, startDateTime, endDateTime);

        LocalDate now = LocalDate.now();

        if (startDateTime.toLocalDate().isBefore(now) || startDateTime.toLocalDate().isAfter(now.plusDays(5))) {
            log.warn(forecastBeforeLimitMessage);
            return WeatherForecastMapper.INSTANCE.toResponse(forecastBeforeLimitMessage);
        }

        if (endDateTime.toLocalDate().isAfter(startDateTime.toLocalDate().plusDays(5))) {
            log.warn(forecastAfterLimitMessage);
            return WeatherForecastMapper.INSTANCE.toResponse(forecastAfterLimitMessage);
        }

        List<WeatherForecast> weatherForecasts = forecastRepository.findWeatherForecastByCityAndDateTime(
                city, startDateTime, endDateTime);

        cityPopularityService.increaseCityPopularity(city);

        if (!weatherForecasts.isEmpty()) {
            log.info("Данные о прогнозе погоды успешно получены из БД: {}", weatherForecasts);
            return WeatherForecastMapper.INSTANCE.toResponse(weatherForecasts);
        } else {
            Iterable<WeatherForecast> forecasts = createWeatherForecast(city);
            return WeatherForecastMapper.INSTANCE.toResponse(forecasts);
        }
    }

    @Override
    public Optional<ForecastDto> getForecastDtoFromExternalApi(String city) {
        Optional<ForecastDto> forecastDto = restClient.getForecastDto(city);
        if (forecastDto.isPresent()) {
            log.info("Данные о прогнозе погоды успешно получены из внешнего API: {}", forecastDto);
        } else {
            log.warn("Для города {} не удалось получить данные о прогнозе погоды из внешнего API.", city);
        }
        return forecastDto;
    }

    @Override
    @Transactional
    @CachePut(value = "dailyCache", key = "#city", condition = "@cityPopularityServiceImpl.isCityPopular(#city)",
            unless = "!#result.iterator().next().forecastDateTime.toLocalDate().isEqual(T(java.time.LocalDate).now())")
    public Iterable<WeatherForecast> createWeatherForecast(String city) {
        Optional<ForecastDto> forecastDto = getForecastDtoFromExternalApi(city);

        Iterable<WeatherForecast> weatherForecast = Optional.ofNullable(forecastDto.get())
                .map(WeatherForecastMapper.INSTANCE::map)
                .map(forecastRepository::saveAll)
                .orElseThrow(() -> {
                    log.warn("Не удалось сохранить данные о прогнозе погоды");
                    return new IllegalArgumentException("Невозможно сохранить данные о прогнозе погоды");
                });

        log.info("Данные о прогнозе погоды сохранены: {}", weatherForecast);
        return weatherForecast;
    }

    @Override
    @Transactional
    @CacheEvict(value = "dailyCache", allEntries = true)
    public void deleteAllWeatherForecast() {
        try {
            forecastRepository.deleteAll();
            log.info("Все данные о прогнозе погоды успешно удалены");
        } catch (Exception e) {
            log.error("Ошибка при удалении данных о прогнозе погоды: {}", e.getMessage());
            throw new RuntimeException("Ошибка при удалении данных о прогнозе погоды", e);
        }
    }

    @Override
    @Transactional
    public String analyzeWeatherForecast(WeatherForecastResponse response) {

        if (response.getMessage() != null) return response.getMessage();

        log.info("Применение рекомендаций по одежде на основе прогноза погоды");

        Set<WeatherType> uniqueWeatherTypes = new HashSet<>();
        response.getForecasts().forEach(forecast -> uniqueWeatherTypes.add(forecast.getWeatherType()));

        PriorityQueue<WeatherType> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(WeatherType::getPriority));
        priorityQueue.addAll(uniqueWeatherTypes);
        WeatherType mostSignificantWeatherType = priorityQueue.peek();

        StringBuilder clothingRecommendation = new StringBuilder("Рекомендации по одежде: ");
        String mostSignificantDescription = mostSignificantWeatherType.getDescription();

        if (!mostSignificantDescription.isEmpty()) {
            clothingRecommendation.append(mostSignificantDescription);
        }

        log.info(clothingRecommendation.toString());
        return clothingRecommendation.toString();
    }
}
