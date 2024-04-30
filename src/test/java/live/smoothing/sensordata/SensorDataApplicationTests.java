package live.smoothing.sensordata;

import live.smoothing.sensordata.config.InfluxDBConfig;
import live.smoothing.sensordata.dto.Kwh;
import live.smoothing.sensordata.repository.impl.KwhRepositoryImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;

@SpringBootTest
class SensorDataApplicationTests {

    private final String topic = "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/ac_indoor_unit/ph/kwh/de/sum";
    private final String[] testTopic = {"office"};
    @Autowired
    private InfluxDBConfig client;

    @Test
    void Raw_Data_Test() {

        KwhRepositoryImpl kwhRepository = new KwhRepositoryImpl(client);
        List<Kwh> rawList = kwhRepository.get24Raw(testTopic);
        System.out.println("size: " + rawList.size());

        for(int i = 0; i < rawList.size(); i++) {
            if(rawList.get(i).getTopic().equals(topic)) {
                System.out.println("Topic: " + rawList.get(i).getTopic());
                System.out.println("Place: " + rawList.get(i).getPlace());
                System.out.println("Time: " + rawList.get(i).getTime());
                System.out.println("Value: " + rawList.get(i).getValue());
                System.out.println("==================================================");
            }
        }
    }

    @Test
    @DisplayName("24시간 데이터")
    void testGet24hours() {
        KwhRepositoryImpl kwhRepository = new KwhRepositoryImpl(client);

        List<Kwh> kwhList = kwhRepository.get24HourData(testTopic);

        for(int i = 0; i < kwhList.size(); i++) {
            if(Objects.equals(kwhList.get(i).getTopic(), topic)) {
                System.out.println("place: " + kwhList.get(i).getPlace());
                System.out.println("time: " + kwhList.get(i).getTime());
                System.out.println("value: " + kwhList.get(i).getValue());
                System.out.println("topic: " + kwhList.get(i).getTopic());
                System.out.println("==================================================");
            }
        }
    }
}