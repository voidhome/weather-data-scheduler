package app.controller;

import app.service.WeatherForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class WeatherForecastRestController {

    private final WeatherForecastService forecastService;

    @GetMapping
    public Mono<String> getOutfitRecommendation(@RequestHeader("X-Location-City") String city,
                                                @RequestParam LocalDateTime startDateTime,
                                                @RequestParam LocalDateTime endDateTime) {
        return forecastService.analyzeWeatherForecast(forecastService.getWeatherForecast(city, startDateTime, endDateTime));
    }
}
