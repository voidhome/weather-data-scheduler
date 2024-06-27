package app.service.impl;

import app.repository.CityPopularityRepository;
import app.service.CityPopularityService;
import app.table.CityPopularity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityPopularityServiceImpl implements CityPopularityService {

    private final CityPopularityRepository cityPopularityRepository;

    @Override
    public Flux<String> getPopularCities() {
        return cityPopularityRepository.findAll()
                .filter(cityPopularity -> cityPopularity.getPopularity() >= 100)
                .map(CityPopularity::getCity)
                .collectList()
                .doOnSuccess(cities -> log.info("Найдено {} популярных городов.", cities.size()))
                .flatMapMany(Flux::fromIterable);
    }

    @Override
    public Mono<Boolean> isCityPopular(String city) {
        return cityPopularityRepository.findByCity(city)
                .map(cityPopularity -> cityPopularity.getPopularity() >= 100)
                .defaultIfEmpty(false);
    }

    @Override
    @Transactional
    public Mono<Void> increaseCityPopularity(String city) {
        return cityPopularityRepository.findByCity(city)
                .switchIfEmpty(Mono.defer(() -> Mono.just(CityPopularity.builder()
                        .city(city)
                        .popularity(0)
                        .build())))
                .flatMap(cityPopularity -> {
                    cityPopularity.setPopularity(cityPopularity.getPopularity() + 1);
                    return cityPopularityRepository.save(cityPopularity);
                })
                .then();
    }

    @Override
    @Transactional
    public Mono<Void> syncCityPopularity() {
        return cityPopularityRepository.findAll()
                .map(city -> {
                    int newPopularity = city.getPopularity() >= 100 ? 100 : 0;
                    city.setPopularity(newPopularity);
                    return city;
                })
                .collectList()
                .flatMapMany(cityPopularityRepository::saveAll)
                .doOnNext(savedCity -> log.info("Популярность города {} обновлена до {}.", savedCity.getCity(), savedCity.getPopularity()))
                .then();
    }
}
