package app.service;

import java.util.List;

public interface CityPopularityService {

    List<String> getPopularCities();

    boolean isCityPopular(String city);

    public void increaseCityPopularity(String city);

    public void syncCityPopularity();
}
