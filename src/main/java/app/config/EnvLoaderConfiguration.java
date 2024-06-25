package app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@PropertySource(value = "file:../weather-data.env")
@ConfigurationProperties(prefix = "open-weather")
public class EnvLoaderConfiguration {
}