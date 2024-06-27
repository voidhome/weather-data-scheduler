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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

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
//    @Cacheable(value = "dailyCache", key = "#city", condition = "@cityPopularityServiceImpl.isCityPopular(#city)",
//            unless = "!#result.forecasts.iterator().next().forecastDateTime.toLocalDate().isEqual(T(java.time.LocalDate).now())")
    public Mono<WeatherForecastResponse> getWeatherForecast(String city, LocalDateTime startDateTime,
                                                            LocalDateTime endDateTime) {
        log.info("Запрос прогноза погоды для города {} с {} по {}", city, startDateTime, endDateTime);

        LocalDate now = LocalDate.now();

        if (startDateTime.toLocalDate().isBefore(now) || startDateTime.toLocalDate().isAfter(now.plusDays(5))) {
            log.warn(forecastBeforeLimitMessage);
            return Mono.just(WeatherForecastMapper.INSTANCE.toResponse(forecastBeforeLimitMessage));
        }

        if (endDateTime.toLocalDate().isAfter(startDateTime.toLocalDate().plusDays(5))) {
            log.warn(forecastAfterLimitMessage);
            return Mono.just(WeatherForecastMapper.INSTANCE.toResponse(forecastAfterLimitMessage));
        }

        cityPopularityService.increaseCityPopularity(city);

        return forecastRepository.findByCityAndForecastDateTimeBetween(city, startDateTime, endDateTime)
                .collectList()
                .flatMap(weatherForecasts -> {
                    if (!weatherForecasts.isEmpty()) {
                        log.info("Данные о прогнозе погоды успешно получены из БД: {}", weatherForecasts);
                        return Mono.just(WeatherForecastMapper.INSTANCE.toResponse(weatherForecasts));
                    } else {
                        return Mono.just(WeatherForecastMapper.INSTANCE.toResponse(createWeatherForecast(city).toIterable()));
                    }
                });
    }

    @Override
    public Mono<ForecastDto> getForecastDtoFromExternalApi(String city) {
        return restClient.getForecastDto(city)
                .doOnNext(forecastDto -> log.info("Данные о прогнозе погоды успешно получены из внешнего API: {}", forecastDto))
                .switchIfEmpty(Mono.defer(() -> {
                    log.warn("Для города {} не удалось получить данные о прогнозе погоды из внешнего API.", city);
                    return Mono.empty();
                }));
    }

    @Override
    @Transactional
//    @CachePut(value = "dailyCache", key = "#city", condition = "@cityPopularityServiceImpl.isCityPopular(#city)",
//            unless = "!#result.iterator().next().forecastDateTime.toLocalDate().isEqual(T(java.time.LocalDate).now())")
    public Flux<WeatherForecast> createWeatherForecast(String city) {
        return getForecastDtoFromExternalApi(city)
                .flatMapMany(forecastDto -> {
                    if (forecastDto != null) {
                        Flux<WeatherForecast> weatherForecasts = Flux.fromIterable(WeatherForecastMapper.INSTANCE.map(forecastDto));
                        return forecastRepository.saveAll(weatherForecasts);
                    } else {
                        return Flux.error(new IllegalArgumentException("Невозможно сохранить данные о прогнозе погоды"));
                    }
                });
    }

    @Override
    @Transactional
    @CacheEvict(value = "dailyCache", allEntries = true)
    public Mono<Void> deleteAllWeatherForecast() {
        return forecastRepository.deleteAll()
                .doOnSuccess(unused -> log.info("Все данные о прогнозе погоды успешно удалены"))
                .onErrorMap(e -> new RuntimeException("Ошибка при удалении данных о прогнозе погоды", e));
    }

    @Override
    @Transactional
    public Mono<String> analyzeWeatherForecast(Mono<WeatherForecastResponse> responseMono) {

        return responseMono.flatMap(response -> {
            if (response.getMessage() != null) {
                return Mono.just(response.getMessage());
            }

            log.info("Применение рекомендаций по одежде на основе прогноза погоды");

            return Flux.fromIterable(response.getForecasts())
                    .map(WeatherForecast::getWeatherType)
                    .collect(Collectors.toSet())
                    .map(uniqueWeatherTypes -> {
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
                    });
        });
    }
}