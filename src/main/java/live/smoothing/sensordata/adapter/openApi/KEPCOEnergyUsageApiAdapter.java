package live.smoothing.sensordata.adapter.openApi;

import com.fasterxml.jackson.databind.ObjectMapper;
import live.smoothing.common.exception.CommonException;
import live.smoothing.sensordata.adapter.EnergyUsageApiAdapter;
import live.smoothing.sensordata.dto.usage.EnergyUsageResponse;
import live.smoothing.sensordata.prop.Url;
import live.smoothing.sensordata.util.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KEPCOEnergyUsageApiAdapter implements EnergyUsageApiAdapter {

    private final Url urlProvider;

    @Autowired
    public KEPCOEnergyUsageApiAdapter(Url urlProvider) {
        this.urlProvider = urlProvider;
    }

    @Override
    public EnergyUsageResponse fetchEnergyUsage(int year, String month, String bizCd) {

        RestTemplate restTemplate = new RestTemplate();
        String url = urlProvider.getUrl(year, month, bizCd);
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        return parseResponse(resp.getBody());
    }

    private EnergyUsageResponse parseResponse(String json) throws CommonException {
        try {

            ObjectMapper mapper = new ObjectMapper();
            String fixedJson = JsonUtil.fixJsonArray(json);
            return mapper.readValue(fixedJson, EnergyUsageResponse.class);
        } catch(Exception e) {
            throw  new CommonException(HttpStatus.BAD_REQUEST, "KEPCO API 호출 실패: " + e.getMessage());
        }
    }
}
