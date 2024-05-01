package live.smoothing.sensordata.config;

import lombok.Getter;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class OpenApiConfig {

    private final String url = "https://bigdata.kepco.co.kr/openapi/v1/powerUsage/industryType.do?" +
            "year=%d&" +
            "month=%s&" +
            "bizCd=%s&" +
            "apiKey=kG5U7b415709zni9P0588V4R2KHh65ITvO95GNM9&" +
            "returnType=json";

    public String getUrl(int year, String month, String bizCd) {
        return String.format(url, year, month, bizCd);
    }

}
