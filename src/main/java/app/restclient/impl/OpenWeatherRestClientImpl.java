package app.restclient.impl;

import app.dto.ForecastDto;
import app.dto.WeatherDto;
import app.restclient.OpenWeatherRestClient;
import app.restclient.exception.WeatherServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenWeatherRestClientImpl implements OpenWeatherRestClient {

    private final RestClient restClient;

    @Value("${open-weather.weather-path}")
    private String weatherPath;

    @Value("${open-weather.forecast-path}")
    private String forecastPath;

    @Override
    public Optional<WeatherDto> getWeatherDto(String city) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(weatherPath)
                            .queryParam("q", city)
                            .build())
                    .retrieve()
                    .body(WeatherDto.class));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Ошибка при получении данных о текущей погоде", e);
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Ошибка REST-клиента при получении данных о текущей погоде", e);
            throw new WeatherServiceException("Ошибка при получении данных о текущей погоде", e);
        }
    }

    @Override
    public Optional<ForecastDto> getForecastDto(Integer cityId) {
        try {
            return Optional.ofNullable(restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(forecastPath)
                            .queryParam("id", cityId)
                            .build())
                    .retrieve()
                    .body(ForecastDto.class));
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            log.error("Ошибка при получении данных о прогнозе погоды", e);
            return Optional.empty();
        } catch (RestClientException e) {
            log.error("Ошибка REST-клиента при получении данных о прогнозе погоды", e);
            throw new WeatherServiceException("Ошибка при получении данных о прогнозе погоды", e);
        }
    }
}