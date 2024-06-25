package app.controller;

import app.dto.response.WeatherForecastResponse;
import app.service.WeatherForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/forecast")
@RequiredArgsConstructor
public class WeatherForecastRestController {

    private final WeatherForecastService forecastService;

    @GetMapping
    public ResponseEntity<String> getOutfitRecommendation(@RequestHeader("X-Location-City") String city,
                                                          @RequestParam LocalDateTime startDateTime,
                                                          @RequestParam LocalDateTime endDateTime) {
        WeatherForecastResponse response = forecastService.getWeatherForecast(city, startDateTime, endDateTime);
        return ResponseEntity.ok(forecastService.analyzeWeatherForecast(response));
    }
}
