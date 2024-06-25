package app.config;

import app.interceptor.WeatherInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
@EnableScheduling
public class RestClientConfiguration {

    @Bean
    public RestClient restClient(@Value("${open-weather.base-url}") String baseUrl,
                                 @Value("${open-weather.lang}") String lang,
                                 @Value("${open-weather.units}") String units,
                                 WeatherInterceptor interceptor) {
        return RestClient.builder()
                .baseUrl(UriComponentsBuilder.fromHttpUrl(baseUrl)
                        .queryParam("lang", lang)
                        .queryParam("units", units)
                        .build().toUriString())
                .requestInterceptor(interceptor)
                .build();
    }
}