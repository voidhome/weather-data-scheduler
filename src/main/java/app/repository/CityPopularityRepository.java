package app.repository;

import app.table.CityPopularity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityPopularityRepository extends CrudRepository<CityPopularity, Integer> {

    CityPopularity findByCity(String city);
}
