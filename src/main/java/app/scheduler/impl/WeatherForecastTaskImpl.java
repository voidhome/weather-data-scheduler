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

import java.util.List;

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
    public void syncWeatherForecastData() {
        try {
            forecastService.deleteAllWeatherForecast();

            List<String> popularCity = cityPopularityService.getPopularCities();

            for (String city : popularCity) {
                forecastService.createWeatherForecast(city);
            }

            log.info("Синхронизация прогноза погоды выполнена успешно");
        } catch (Exception e) {
            log.error("Ошибка при синхронизации прогноза погоды: {}", e.getMessage());
        }
    }
}