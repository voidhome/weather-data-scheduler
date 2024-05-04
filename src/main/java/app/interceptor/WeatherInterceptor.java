package app.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@Slf4j
@Component
public class WeatherInterceptor implements ClientHttpRequestInterceptor {

    @Value("${open-weather.api-key}")
    private String apiKey;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        URI originalUri = request.getURI();
        log.info("Перехват запроса: " + originalUri);

        HttpRequest modifiedRequest = new HttpRequestWrapper(request) {
            @Override
            public URI getURI() {
                return UriComponentsBuilder.fromUri(originalUri)
                        .queryParam(originalUri.getQuery().isEmpty() ? "?" : "appid", apiKey)
                        .build().toUri();
            }
        };

        log.info("Запрос успешно модифицирован.");
        return execution.execute(modifiedRequest, body);
    }
}
