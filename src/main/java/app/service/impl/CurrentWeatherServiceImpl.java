package app.service.impl;

import app.dto.WeatherDto;
import app.mapper.CurrentWeatherMapper;
import app.repository.CurrentWeatherRepository;
import app.service.CurrentWeatherService;
import app.table.CurrentWeather;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrentWeatherServiceImpl implements CurrentWeatherService {

    private final CurrentWeatherRepository weatherRepository;

    @Override
    @Transactional
    public Optional<CurrentWeather> createCurrentWeather(WeatherDto weatherData) {
        Optional<CurrentWeather> currentWeather = Optional.ofNullable(Optional.ofNullable(weatherData)
                .map(CurrentWeatherMapper.INSTANCE::map)
                .map(weatherRepository::save)
                .orElseThrow(() -> {
                    log.warn("Не удалось сохранить текущие данные о погоде");
                    return new IllegalArgumentException("Невозможно сохранить текущие данные о погоде");
                }));

        log.info("Данные о текущей погоде сохранены: {}", currentWeather);
        return currentWeather;
    }
}
