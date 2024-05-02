package live.smoothing.sensordata.config;

import live.smoothing.sensordata.adapter.openApi.Url;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Value("${kepco.api.url}")
    private String baseUrl;

    @Value("${kepco.api.key}")
    private String apiKey;

    @Bean
    public Url kepcoUrlProvider() {
        return new Url(baseUrl, apiKey);
    }
}
