package app.config;

import app.filter.WeatherExchangeFilterFunction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class WebClientConfiguration {

    @Bean
    public WebClient webClient(@Value("${open-weather.base-url}") String baseUrl,
                               @Value("${open-weather.lang}") String lang,
                               @Value("${open-weather.units}") String units,
                               WeatherExchangeFilterFunction filterFunction) {
        return WebClient.builder()
                .baseUrl(UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .queryParam("lang", lang)
                        .queryParam("units", units)
                        .build().toUriString())
                .filter(filterFunction)
                .build();
    }
}