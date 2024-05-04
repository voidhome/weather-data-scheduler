package app.scheduler.impl;

import app.scheduler.CityPopularityTask;
import app.service.CityPopularityService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CityPopularityTaskImpl implements CityPopularityTask {

    private final CityPopularityService popularityService;

    @Override
    @Transactional
    @Scheduled(cron = "${scheduler.city-popularity.interval-in-cron}")
    @SchedulerLock(name = "syncCityPopularityData",
            lockAtLeastFor = "${scheduler.lock-at-least-for}", lockAtMostFor = "${scheduler.lock-at-most-for}")
    public void syncCityPopularityData() {
        popularityService.syncCityPopularity();
    }
}
