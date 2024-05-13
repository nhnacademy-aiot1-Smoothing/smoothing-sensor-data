package live.smoothing.sensordata.adapter.openApi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.smoothing.common.exception.CommonException;
import live.smoothing.sensordata.adapter.EnergyUsageApiAdapter;
import live.smoothing.sensordata.dto.usage.EnergyUsage;
import live.smoothing.sensordata.dto.usage.EnergyUsageResponse;
import live.smoothing.sensordata.prop.Url;
import live.smoothing.sensordata.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
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

        if(resp.getStatusCode() != HttpStatus.OK) {
            log.error("API 호출 실패: HTTP 상태 코드 {}", resp.getStatusCode());
            throw new CommonException(resp.getStatusCode(), "API 호출 실패: HTTP 상태 코드 " + resp.getStatusCode());
        }

        String fixedJson = JsonUtil.fixJsonArray(resp.getBody());

        return parseResponse(fixedJson);
    }

    private EnergyUsageResponse parseResponse(String json) throws CommonException {
        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(json);
            EnergyUsageResponse response = new EnergyUsageResponse();

            //Todo: 전국 교육서비스업 데이터 추출
            JsonNode totNode = rootNode.path(0); //totData
            EnergyUsage wholeCountry;
            for (JsonNode node : totNode) {
                JsonNode total = node.path(15); //전국교육서비스업
                wholeCountry = mapper.treeToValue(total, EnergyUsage.class);
                response.setWholeCountry(wholeCountry);
            }

            //Todo: 김해시 교육서비스업 데이터 추출
            JsonNode dataNode = rootNode.path(1);//data
            EnergyUsage kimCity;
            for (JsonNode node : dataNode) {
                JsonNode kim = node.path(52);   // 김해시
                kimCity = mapper.treeToValue(kim, EnergyUsage.class);
                response.setKimCity(kimCity);
            }

            return response;

        } catch(Exception e) {
            throw new CommonException(HttpStatus.BAD_REQUEST, "KEPCO API 호출 실패: " + e.getMessage());
        }
    }
}
