package app.scheduler.impl;

import app.dto.ForecastDto;
import app.restclient.OpenWeatherRestClient;
import app.scheduler.WeatherForecastTask;
import app.service.WeatherForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class WeatherForecastTaskImpl implements WeatherForecastTask {

    private final WeatherForecastService forecastService;
    private final OpenWeatherRestClient openWeatherRestClient;

    @Value("${open-weather.popular-city-ids}")
    private List<Integer> popularCityIds;

    @Override
    @Transactional
    @Scheduled(cron = "${scheduler.forecast.interval-in-cron}")
    @SchedulerLock(name = "syncWeatherForecastData",
            lockAtLeastFor = "${scheduler.lock-at-least-for}", lockAtMostFor = "${scheduler.lock-at-most-for}")
    public void syncWeatherForecastData() {
        try {
            forecastService.deleteAllWeatherForecast();
            for (Integer cityId : popularCityIds) {
                Optional<ForecastDto> forecastDto = openWeatherRestClient.getForecastDto(cityId);
                forecastService.createWeatherForecast(forecastDto.get());
            }
            log.info("Синхронизация прогноза погоды выполнена успешно");
        } catch (Exception e) {
            log.error("Ошибка при синхронизации прогноза погоды: {}", e.getMessage());
        }
    }
}