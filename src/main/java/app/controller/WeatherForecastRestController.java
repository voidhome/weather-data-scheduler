package app.controller;

import app.service.WeatherForecastService;
import app.table.WeatherForecast;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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
        List<WeatherForecast> weatherForecast = forecastService.getWeatherForecast(city, startDateTime, endDateTime);
        return ResponseEntity.ok(forecastService.analyzeWeatherForecast(weatherForecast));
    }
}
