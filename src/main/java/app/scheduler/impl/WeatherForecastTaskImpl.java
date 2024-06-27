package app.scheduler.impl;

import app.scheduler.WeatherForecastTask;
import app.service.CityPopularityService;
import app.service.WeatherForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

@Component
@Slf4j
@RequiredArgsConstructor
public class WeatherForecastTaskImpl implements WeatherForecastTask {

    private final WeatherForecastService forecastService;
    private final CityPopularityService cityPopularityService;

    @Override
    @Transactional
    @Scheduled(cron = "${scheduler.forecast.interval-in-cron}")
    @SchedulerLock(name = "syncWeatherForecastData",
            lockAtLeastFor = "${scheduler.lock-at-least-for}", lockAtMostFor = "${scheduler.lock-at-most-for}")
    public Mono<Void> syncWeatherForecastData() {
        forecastService.deleteAllWeatherForecast();

        return cityPopularityService.getPopularCities()
                .flatMap(city -> forecastService.createWeatherForecast(city))
                .collectList()
                .doOnError(e -> log.error("Ошибка при синхронизации прогноза погоды: {}", e.getMessage()))
                .then()
                .doFinally(signalType -> {
                    if (signalType == SignalType.ON_COMPLETE) {
                        log.info("Синхронизация прогноза погоды выполнена успешно");
                    }
                });
    }
}