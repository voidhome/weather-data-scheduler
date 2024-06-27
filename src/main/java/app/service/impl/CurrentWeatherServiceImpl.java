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
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CurrentWeatherServiceImpl implements CurrentWeatherService {

    private final CurrentWeatherRepository weatherRepository;

    @Override
    @Transactional
    public Mono<CurrentWeather> createCurrentWeather(WeatherDto weatherData) {
        return Mono.justOrEmpty(weatherData)
                .map(CurrentWeatherMapper.INSTANCE::map)
                .flatMap(weatherRepository::save)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Невозможно сохранить текущие данные о погоде")))
                .doOnSuccess(savedWeather -> log.info("Данные о текущей погоде сохранены: {}", savedWeather));
    }
}
