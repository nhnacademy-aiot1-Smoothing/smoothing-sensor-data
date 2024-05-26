package live.smoothing.sensordata.prop;

public class Url {

    private final String baseUrl;
    private final String apiKey;

    public Url(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    public String getUrl(int year, String month, String bizCd) {
        return String.format(this.baseUrl, year, month, bizCd) + "&apiKey=" + this.apiKey + "&returnType=json";
    }
}