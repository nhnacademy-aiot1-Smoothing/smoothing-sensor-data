package live.smoothing.sensordata.adapter.openApi;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import live.smoothing.sensordata.dto.usage.EnergyUsageResponse;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static live.smoothing.sensordata.util.JsonUtil.fixJsonArray;

public class tempAPI {
    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();

        Url u = new Url();
        String url = u.getUrl(2023, "11", "P");

        ResponseEntity<String> result =
                restTemplate.
                        exchange(
                                url,
                                HttpMethod.GET,
                                null,
                                String.class);

        String body = result.getBody();

        try {
            assert body != null;
            ObjectMapper mapper = new ObjectMapper();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            JsonParser parser = new JsonParser();

            JsonNode node = mapper.readTree(fixJsonArray(body));

            JsonNode totData = node.get(0).get("totData");
            JsonNode data = node.get(1).get("data");

            JsonElement firstKey = parser.parse(totData.toString());
            JsonElement secondKey = parser.parse(data.toString());

            String pretty1 = gson.toJson(firstKey);
            String pretty2 = gson.toJson(secondKey);

            pretty1 = pretty1.replace("[","").replace("]","");
            System.out.println(pretty1);

            pretty2 = pretty2.replace("[","").replace("]","");

            EnergyUsageResponse r1 = mapper.readValue(pretty1, EnergyUsageResponse.class);
            EnergyUsageResponse r2 = mapper.readValue(pretty2, EnergyUsageResponse.class);

            System.out.println(r1);

        } catch(Exception e) {

            e.getMessage();
        }


    }
}
