package live.smoothing.sensordata.util;

public class JsonUtil {

    private JsonUtil() {}
    
    public static String fixJsonArray(String fuckingBrokenJson) {
        return "[" + fuckingBrokenJson.replace("}{", "},{") + "]";
    }
}
