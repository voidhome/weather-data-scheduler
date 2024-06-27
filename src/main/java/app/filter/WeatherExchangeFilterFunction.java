package app.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Component
public class WeatherExchangeFilterFunction implements ExchangeFilterFunction {

    @Value("${open-weather.api-key}")
    private String apiKey;

    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {
        URI originalUri = request.url();
        log.info("Перехват запроса: " + originalUri);

        ClientRequest modifiedRequest = ClientRequest.from(request)
                .url(UriComponentsBuilder.fromUri(originalUri)
                        .queryParam(request.url().getQuery().isEmpty() ? "?" : "appid", apiKey)
                        .build().toUri())
                .build();

        log.info("Запрос успешно модифицирован.");
        return next.exchange(modifiedRequest);
    }
}
