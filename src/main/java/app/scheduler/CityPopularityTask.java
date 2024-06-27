package app.scheduler;

import reactor.core.publisher.Mono;

public interface CityPopularityTask {

    Mono<Void> syncCityPopularityData();
}
