package app.scheduler.impl;

import app.dto.WeatherDto;
import app.restclient.OpenWeatherRestClient;
import app.scheduler.CurrentWeatherTask;
import app.service.CurrentWeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class CurrentWeatherTaskImpl implements CurrentWeatherTask {

    private final CurrentWeatherService weatherService;
    private final OpenWeatherRestClient openWeatherRestClient;

    @Value("${open-weather.city}")
    private String city;

    @Override
    @Scheduled(cron = "${scheduler.weather.interval-in-cron}")
    @SchedulerLock(name = "syncCurrentWeatherData",
            lockAtLeastFor = "${scheduler.lock-at-least-for}", lockAtMostFor = "${scheduler.lock-at-most-for}")
    public void syncCurrentWeatherData() {
        try {
            Optional<WeatherDto> weatherDto = openWeatherRestClient.getWeatherDto(city);
            weatherService.createCurrentWeather(weatherDto.get());
            log.info("Синхронизация текущей погоды выполнена успешно");
        } catch (Exception e) {
            log.error("Ошибка при синхронизации текущей погоды: {}", e.getMessage());
        }
    }
}
