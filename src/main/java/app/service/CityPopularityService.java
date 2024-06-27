package app.service;

import reactor.core.publisher.Mono;

import java.util.List;

public interface CityPopularityService {

    List<String> getPopularCities();

    Mono<Boolean> isCityPopular(String city);

    public Mono<Void> increaseCityPopularity(String city);

    public Mono<Void> syncCityPopularity();
}
