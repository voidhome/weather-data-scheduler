package app.scheduler;

import reactor.core.publisher.Mono;

public interface CurrentWeatherTask {

    Mono<Void> syncCurrentWeatherData();
}
