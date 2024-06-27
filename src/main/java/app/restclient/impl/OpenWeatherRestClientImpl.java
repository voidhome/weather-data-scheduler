package app.restclient.impl;

import app.dto.ForecastDto;
import app.dto.WeatherDto;
import app.restclient.OpenWeatherRestClient;
import app.restclient.exception.WeatherServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenWeatherRestClientImpl implements OpenWeatherRestClient {

    private final WebClient webClient;

    @Value("${open-weather.weather-path}")
    private String weatherPath;

    @Value("${open-weather.forecast-path}")
    private String forecastPath;

    @Override
    public Mono<WeatherDto> getWeatherDto(String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(weatherPath)
                        .queryParam("q", city)
                        .build())
                .retrieve()
                .bodyToMono(WeatherDto.class)
                .onErrorResume(e -> Mono.error(new WeatherServiceException("Ошибка при получении данных о текущей погоде", e)));
    }

    @Override
    public Mono<ForecastDto> getForecastDto(String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(forecastPath)
                        .queryParam("q", URLEncoder.encode(city, StandardCharsets.UTF_8))
                        .build())
                .retrieve()
                .bodyToMono(ForecastDto.class)
                .onErrorResume(e -> Mono.error(new WeatherServiceException("Ошибка при получении данных о прогнозе погоды", e)));
    }
}
