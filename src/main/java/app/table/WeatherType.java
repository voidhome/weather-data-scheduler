package app.table;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WeatherType {

    THUNDERSTORM("Осторожно! Гроза. Надень защитную одежду и держись подальше от открытых пространств.", 0),
    SNOW("Сегодня снежно! Подготовься к снегу и оденься тепло.", 1),
    RAIN("Дождливо. Не забудь зонт и водонепроницаемую одежду!", 2),
    COLD("Оденься потеплее! Пальто, шарф и перчатки могут пригодиться.", 3),
    MILD("Можно надеть легкую куртку или свитер.", 4),
    WARM("Погода теплая! Одевайся свободно.", 5);

    private final String description;
    private final int priority;
}
