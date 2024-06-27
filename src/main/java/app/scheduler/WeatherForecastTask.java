package app.scheduler;

import reactor.core.publisher.Mono;

public interface WeatherForecastTask {

    Mono<Void> syncWeatherForecastData();
}
