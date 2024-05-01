package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.config.InfluxDBConfig;
import live.smoothing.sensordata.entity.Kwh;
import live.smoothing.sensordata.repository.impl.KwhRepositoryImpl;
import live.smoothing.sensordata.util.TimeProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

class CustomTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.of(2024, 5, 1, 0, 0);
    }

    @Override
    public Instant nowInstant() {
        return now().toInstant(ZoneOffset.of("+09:00"));
    }
}

@SpringBootTest
class KwhServiceImplTest {

    private final String[] testTopic = {"data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/ac_indoor_unit/ph/kwh/de/sum"};
    @Autowired
    private InfluxDBConfig client;

    private TimeProvider timeProvider;

    @BeforeEach
    void setUp() {
        timeProvider = new CustomTimeProvider();
    }

    @Test
    @DisplayName("24시간 Raw 데이터 조회 테스트")
    void rawDataTest() {

        KwhRepositoryImpl kwhRepository = new KwhRepositoryImpl(client.rawInfluxClient(), client.aggregationInfluxClient(), timeProvider);
        List<Kwh> rawList = kwhRepository.get24Raw(testTopic);
        System.out.println("size: " + rawList.size());

        for(int i = 0; i < rawList.size(); i++) {
            System.out.println("Topic: " + rawList.get(i).getTopic());
            System.out.println("Place: " + rawList.get(i).getPlace());
            System.out.println("Time: " + rawList.get(i).getTime());
            System.out.println("Value: " + rawList.get(i).getValue());
            System.out.println("==================================================");
        }
    }

    @Test
    @DisplayName("24시간 데이터")
    void testGet24hours() {
        KwhRepositoryImpl kwhRepository = new KwhRepositoryImpl(client.rawInfluxClient(), client.aggregationInfluxClient(), timeProvider);

        List<Kwh> kwhList = kwhRepository.get24HourData(testTopic);

        for(int i = 0; i < kwhList.size(); i++) {
            System.out.println("place: " + kwhList.get(i).getPlace());
            System.out.println("time: " + kwhList.get(i).getTime());
            System.out.println("value: " + kwhList.get(i).getValue());
            System.out.println("topic: " + kwhList.get(i).getTopic());
            System.out.println("==================================================");
        }
    }

    @Test
    @DisplayName("최근 달의 시작 값과 끝 값 가져오기")
    void testGetCurrentMonthData() {
        double result = 0.0;

        KwhRepositoryImpl kwhRepository = new KwhRepositoryImpl(client.rawInfluxClient(), client.aggregationInfluxClient(), timeProvider);

        List<Kwh> startDataList = kwhRepository.getCurrentMonthStartData(testTopic);
        List<Kwh> endDataList = kwhRepository.getCurrentMonthEndData(testTopic);

        for (Kwh kwh : endDataList) result += kwh.getValue();
        for (Kwh kwh : startDataList) result -= kwh.getValue();

        for(int i = 0; i < startDataList.size(); i++) {
            System.out.println("place: " + startDataList.get(i).getPlace());
            System.out.println("time: " + startDataList.get(i).getTime());
            System.out.println("value: " + startDataList.get(i).getValue());
            System.out.println("topic: " + startDataList.get(i).getTopic());
            System.out.println("==================================================");
        }

        for(int i = 0; i < endDataList.size(); i++) {
            System.out.println("place: " + endDataList.get(i).getPlace());
            System.out.println("time: " + endDataList.get(i).getTime());
            System.out.println("value: " + endDataList.get(i).getValue());
            System.out.println("topic: " + endDataList.get(i).getTopic());
            System.out.println("==================================================");
        }

        System.out.println(result);
    }
}