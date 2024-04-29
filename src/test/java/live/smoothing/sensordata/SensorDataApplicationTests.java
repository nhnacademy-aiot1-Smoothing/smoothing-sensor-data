package live.smoothing.sensordata;

import live.smoothing.sensordata.config.InfluxDBConfig;
import live.smoothing.sensordata.repository.impl.KwhRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.temporal.ChronoUnit;

@SpringBootTest
class SensorDataApplicationTests {

    @Autowired
    private InfluxDBConfig client;

    @Test
    void contextLoads() {
    }

    @Test
    void Raw_Data_Test() {

        KwhRepositoryImpl kwhRepository = new KwhRepositoryImpl(client);
        System.out.println("Data: " + kwhRepository.getRaw("aggregation2", "kwh_daily", -1L, ChronoUnit.HOURS).get(0).getPlace());

    }

    @Test
    void 시24간데이터() {
        KwhRepositoryImpl kwhRepository = new KwhRepositoryImpl(client);
        System.out.println("Data: " + kwhRepository.get24HourData().get(0).getPlace());
    }

}
