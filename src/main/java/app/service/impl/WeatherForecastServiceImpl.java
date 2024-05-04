package app.service.impl;

import app.dto.ForecastDto;
import app.mapper.WeatherForecastMapper;
import app.repository.WeatherForecastRepository;
import app.service.WeatherForecastService;
import app.table.WeatherForecast;
import app.table.WeatherType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WeatherForecastServiceImpl implements WeatherForecastService {

    private final WeatherForecastRepository forecastRepository;

    @Override
    @Cacheable(value = "dailyCache", key = "'forecast_' + #city + '_' + #startDateTime + '_' + #endDateTime")
    public List<WeatherForecast> getWeatherForecast(String city, LocalDateTime startDateTime,
                                                    LocalDateTime endDateTime) {
        log.info("Запрос прогноза погоды для города {} с {} по {}", city, startDateTime, endDateTime);
        try {
            List<WeatherForecast> weatherForecast = forecastRepository.findWeatherForecastByCityAndDateTime(
                    city, startDateTime, endDateTime);
            log.info("Данные о прогнозе погоды успешно получены: {}", weatherForecast);
            return weatherForecast;
        } catch (Exception e) {
            log.error("Ошибка при получении данных о прогнозе погоды: {}", e.getMessage());
            throw new RuntimeException("Ошибка при получении данных о прогнозе погоды", e);
        }
    }

    @Override
    @Transactional
    @Cacheable(value = "dailyCache", key = "'forecast_' + #forecastDto.city()",
            unless = "#result.iterator().next().forecastDateTime.toLocalDate().equals(T(java.time.LocalDate).now())")
    public Iterable<WeatherForecast> createWeatherForecast(ForecastDto forecastDto) {
        Iterable<WeatherForecast> weatherForecast = Optional.ofNullable(forecastDto)
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
    public String analyzeWeatherForecast(List<WeatherForecast> weatherForecasts) {
        log.info("Применение рекомендаций по одежде на основе прогноза погоды");
        if (weatherForecasts == null || weatherForecasts.isEmpty()) {
            log.warn("Нет данных для анализа погоды");
            return "Нет данных для анализа погоды";
        }

        Set<WeatherType> uniqueWeatherTypes = new HashSet<>();
        weatherForecasts.forEach(forecast -> uniqueWeatherTypes.add(forecast.getWeatherEnumType()));

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
