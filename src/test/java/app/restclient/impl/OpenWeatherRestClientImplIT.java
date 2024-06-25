package app.restclient.impl;

import app.IntegrationTestBase;
import app.dto.WeatherDto;
import app.service.CurrentWeatherService;
import app.table.CurrentWeather;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.client.RestClient;

import java.util.Optional;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@WireMockTest
class OpenWeatherRestClientImplIT extends IntegrationTestBase {

    @Value("${open-weather.weather-path}")
    private String weatherPath;

    @Autowired
    private CurrentWeatherService weatherService;

    @RegisterExtension
    static WireMockExtension wireMockExtension = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort().usingFilesUnderClasspath("wiremock"))
            .build();

    @DynamicPropertySource
    public static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("${open-weather.base-url}", wireMockExtension::baseUrl);
    }

    @Test
    public void testEndToEnd() {
        // Arrange
        wireMockExtension.stubFor(
                WireMock.get(weatherPath)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBodyFile("get-weather-response.json"))
        );

        // Act
        WeatherDto weatherDto = getWeatherDto();
        Optional<CurrentWeather> savedWeather = weatherService.createCurrentWeather(weatherDto);

        // Assert
        assertNotNull(savedWeather);
        assertEquals("Москва", savedWeather.get().city());
        assertEquals(5.21, savedWeather.get().temp());
        assertEquals("пасмурно", savedWeather.get().description());
        assertNotNull(savedWeather);
    }

    private WeatherDto getWeatherDto() {
        RestClient restClient = RestClient.builder().baseUrl(wireMockExtension.baseUrl()).build();
        return restClient.get()
                .uri(weatherPath)
                .retrieve()
                .body(WeatherDto.class);
    }
}