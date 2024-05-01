package live.smoothing.sensordata.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class JsonUtil {

    private JsonUtil() {}

    public static String convertToValidJsonArray(String body) {
        return "[" + body.replace("]}{", "],") + "]";
    }

    public static String formatJsonString(JsonNode json) {

        String jsonString = "{\"data\":" + json.get(0).get("data").toString() + "}";
        return jsonString;
    }

    // {}{} -> [{},{}]
    public static String fixJsonArray(String fuckingBrokenJson) {
        return "[" + fuckingBrokenJson.replace("}{", "},{") + "]";
    }

    public static JsonElement[] parseJsonString(String jsonString) {
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonString);
        JsonArray jsonArray = jsonElement.getAsJsonArray();

        JsonElement[] jsonElements = new JsonElement[jsonArray.size()];
        for(int i = 0; i < jsonArray.size(); i++) {
            jsonElements[i] = jsonArray.get(i);
        }
        return jsonElements;
    }

    public static String extractValueForSpecificKey(String jsonArrayStr, String key) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode nodes = mapper.readTree(jsonArrayStr);

        for (JsonNode node : nodes) {
            if (node.has(key)) {
                return node.get(key).asText();
            }
        }

        return null;
    }
}
