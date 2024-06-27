package app.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CityPopularityService {

    Flux<String> getPopularCities();

    Mono<Boolean> isCityPopular(String city);

    public Mono<Void> increaseCityPopularity(String city);

    public Mono<Void> syncCityPopularity();
}
