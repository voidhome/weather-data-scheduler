package app.table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("city_popularity")
@Builder
@Setter
@Getter
public class CityPopularity {

    @Id
    Integer id;

    String city;
    int popularity;
}

