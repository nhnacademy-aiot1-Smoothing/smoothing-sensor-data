package live.smoothing.sensordata.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OpenApiConfigTest {

    private final String result = "https://bigdata.kepco.co.kr/openapi/v1/powerUsage/industryType.do?year=2023&month=11&bizCd=P&apiKey=kG5U7b415709zni9P0588V4R2KHh65ITvO95GNM9&returnType=json";

    @Test
    void getUrlTest() {
        OpenApiConfig config = new OpenApiConfig();

        String url = config.getUrl(2023, "11", "P");

        Assertions.assertEquals(result, url);
    }
}