package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.config.InfluxDBConfig;
import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.dto.PowerMetricResponse;
import live.smoothing.sensordata.repository.KwhRepository;
import live.smoothing.sensordata.repository.impl.KwhRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class KwhServiceImplTest {

    @Autowired
    private InfluxDBConfig client;

    @Test
    void get24HourData() {

        KwhRepositoryImpl repository = new KwhRepositoryImpl(client);
        KwhServiceImpl service = new KwhServiceImpl(repository, null);

        List<PowerMetric> metrics = service.get24HourData("kwh", "hour", "1", "place").getData();

        System.out.println("size: " + metrics.size());

        for(int i = 0; i < metrics.size(); i++) {
            System.out.println("type: " + metrics.get(i).getType());
            System.out.println("unit: " + metrics.get(i).getUnit());
            System.out.println("per: " + metrics.get(i).getPer());
            System.out.println("time: " + metrics.get(i).getTime());
            System.out.println("value: " + metrics.get(i).getValue());
            System.out.println("==================================================");
        }
    }

    @Test
    void splitTest() {
        String test = "a,b,c,d,e,f";
        String[] split = test.split(",");
        System.out.println(split[0]);
    }
}