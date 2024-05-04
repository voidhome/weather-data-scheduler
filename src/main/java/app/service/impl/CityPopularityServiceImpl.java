package app.service.impl;

import app.repository.CityPopularityRepository;
import app.service.CityPopularityService;
import app.table.CityPopularity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CityPopularityServiceImpl implements CityPopularityService {

    private final CityPopularityRepository cityPopularityRepository;

    @Override
    public List<String> getPopularCities() {
        List<String> popularCities = StreamSupport.stream(cityPopularityRepository.findAll().spliterator(), false)
                .filter(city -> city.getPopularity() >= 100)
                .map(CityPopularity::getCity)
                .toList();
        log.info("Найдено {} популярных городов.", popularCities.size());
        return popularCities;
    }

    @Override
    public boolean isCityPopular(String city) {
        CityPopularity cityPopularity = cityPopularityRepository.findByCity(city);
        return cityPopularity != null && cityPopularity.getPopularity() >= 100;
    }

    @Override
    @Transactional
    public void increaseCityPopularity(String city) {
        CityPopularity cityPopularity = Optional.ofNullable(cityPopularityRepository.findByCity(city))
                .orElse(CityPopularity.builder()
                        .city(city)
                        .popularity(0)
                        .build());

        cityPopularity.setPopularity(cityPopularity.getPopularity() + 1);
        cityPopularityRepository.save(cityPopularity);
        log.info("Популярность города {} увеличена до {}.", city, cityPopularity.getPopularity());
    }

    @Override
    @Transactional
    public void syncCityPopularity() {
        List<CityPopularity> citiesToUpdate = new ArrayList<>();

        cityPopularityRepository.findAll().forEach(city -> {
            int newPopularity = city.getPopularity() >= 100 ? 100 : 0;
            city.setPopularity(newPopularity);
            citiesToUpdate.add(city);
        });

        cityPopularityRepository.saveAll(citiesToUpdate);
        log.info("Синхронизация популярности городов завершена. Обновлено {} записей.", citiesToUpdate.size());
    }
}
