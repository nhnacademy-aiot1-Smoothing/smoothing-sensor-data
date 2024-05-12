package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.config.InfluxDBConfig;
import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.dto.SensorPowerMetric;
import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import live.smoothing.sensordata.dto.topic.SensorTopicResponse;
import live.smoothing.sensordata.dto.topic.SensorWithTopic;
import live.smoothing.sensordata.entity.Kwh;
import live.smoothing.sensordata.repository.impl.KwhRepositoryImpl;
import live.smoothing.sensordata.util.TimeProvider;
import live.smoothing.sensordata.util.TimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

class CustomTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.of(2024, 5, 2, 5, 0);
    }

    @Override
    public Instant nowInstant() {
        return now().toInstant(ZoneOffset.of("+09:00"));
    }
}

@SpringBootTest
class KwhServiceImplTest {

    private final String[] testTopic = {
            "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/ac_indoor_unit/ph/kwh/de/sum",
            "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/air_conditioner/ph/kwh/de/sum",
            "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/pair_rm_heating/ph/kwh/de/sum",
            "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/meeting_rm_heating/ph/kwh/de/sum",
            "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/main/ph/kwh/de/sum",
            "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/main/ph/kwh/de/sum"
    };
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
        List<Kwh> rawList = kwhRepository.getStartData(testTopic, TimeUtil.getRecentHour(Instant.now()));
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

        List<Kwh> kwhList = kwhRepository.get48HourData(testTopic);

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

        List<Kwh> startDataList = kwhRepository.getStartData(testTopic, Instant.now().minus(1, ChronoUnit.HOURS));
        List<Kwh> endDataList = kwhRepository.getEndData(testTopic, Instant.now().minus(1, ChronoUnit.HOURS));

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

    @Test
    @DisplayName("최근 일주일 1시간 단위로 가져와 시간대별로 데이터 합산하기")
    void testGetWeekDataByHour() {
        List<KwhTimeZoneResponse> kwhTimeZoneResponses = List.of(
                new KwhTimeZoneResponse("evening", 0.0),
                new KwhTimeZoneResponse("afternoon", 0.0),
                new KwhTimeZoneResponse("morning", 0.0),
                new KwhTimeZoneResponse("dawn", 0.0)
        );

        KwhRepositoryImpl kwhRepository = new KwhRepositoryImpl(client.rawInfluxClient(), client.aggregationInfluxClient(), timeProvider);

        List<Kwh> kwhList = kwhRepository.getWeekDataByHour(testTopic);

        Map<Instant, Double> sumByTimezone = kwhList.stream()
                .collect(Collectors.groupingBy(Kwh::getTime,
                        Collectors.summingDouble(Kwh::getValue)));

        List<Map.Entry<Instant, Double>> collect = sumByTimezone.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (int i = collect.size()-1, valueIndex = 0; i > 0; i -= 6, valueIndex = (valueIndex + 1) % 4) {
            for (int j = 0; j <= 5; j++) {
                kwhTimeZoneResponses.get(valueIndex)
                        .setValue(kwhTimeZoneResponses.get(valueIndex).getValue() + (collect.get(i - j).getValue() - collect.get(i - j - 1).getValue()));
            }
        }

        for (KwhTimeZoneResponse kwhTimeZoneRespons : kwhTimeZoneResponses) {
            System.out.println("time: " + kwhTimeZoneRespons.getLabel());
            System.out.println("value: " + kwhTimeZoneRespons.getValue());
            System.out.println("==================================================");

        }
    }

    @Test
    @DisplayName("특정 기간 1일 단위 전체 데이터 가져오기")
    void testGetDailyTotalDataByPeriod() {
        KwhRepositoryImpl kwhRepository = new KwhRepositoryImpl(client.rawInfluxClient(), client.aggregationInfluxClient(), timeProvider);

        LocalDateTime start = LocalDate.parse("2024-04-20", DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        LocalDateTime end = LocalDate.parse("2024-04-27", DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();

        Instant startInstant = TimeUtil.getRecentDay(start.toInstant(ZoneOffset.UTC));
        Instant endInstant = TimeUtil.getRecentDay(end.toInstant(ZoneOffset.UTC))
                .plus(1, ChronoUnit.DAYS)
                .plus(30, ChronoUnit.MINUTES);

        List<Kwh> kwhList = kwhRepository.getDailyDataByPeriod(testTopic, startInstant, endInstant);
        List<PowerMetric> powerMetrics = new LinkedList<>();

        Map<Instant, Double> sumByTimezone = kwhList.stream()
                .collect(Collectors.groupingBy(Kwh::getTime,
                        Collectors.summingDouble(Kwh::getValue)));

        List<Map.Entry<Instant, Double>> collect = sumByTimezone.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        for (int i = collect.size()-1; i > 0; i--) {
            powerMetrics.add(
                    0,
                    new PowerMetric(
                            "kwh",
                            "day",
                            "1",
                            collect.get(i-1).getKey(),
                            collect.get(i).getValue() - collect.get(i - 1).getValue()
                    )
            );
        }

        for (PowerMetric powerMetric : powerMetrics) {
            System.out.println("type: " + powerMetric.getType());
            System.out.println("unit: " + powerMetric.getUnit());
            System.out.println("per: " + powerMetric.getPer());
            System.out.println("time: " + powerMetric.getTime());
            System.out.println("value: " + powerMetric.getValue());
            System.out.println("==================================================");
        }
    }

    @Test
    @DisplayName("특정 기간 1일 단위 센서별 데이터 가져오기")
    void testGetDailyDataByPeriod() {
        KwhRepositoryImpl kwhRepository = new KwhRepositoryImpl(client.rawInfluxClient(), client.aggregationInfluxClient(), timeProvider);

        LocalDateTime start = LocalDate.parse("2024-04-20", DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        LocalDateTime end = LocalDate.parse("2024-04-27", DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();

        SensorTopicResponse sensorWithTopics = new SensorTopicResponse();
        sensorWithTopics.setSensorWithTopics(List.of(
                new SensorWithTopic("sensor1", "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/meeting_rm_heating/ph/kwh/de/sum"),
                new SensorWithTopic("sensor1", "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/main/ph/kwh/de/sum"),
                new SensorWithTopic("sensor2", "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/main/ph/kwh/de/sum")
        ));

        Instant startInstant = TimeUtil.getRecentDay(start.toInstant(ZoneOffset.UTC));
        Instant endInstant = TimeUtil.getRecentDay(end.toInstant(ZoneOffset.UTC))
                .plus(1, ChronoUnit.DAYS)
                .plus(30, ChronoUnit.MINUTES);

        Map<String, String> topicSensorNameMap = new HashMap<>();
        Map<String, List<Kwh>> sensorNameKwhMap = new HashMap<>();
        String[] topics = sensorWithTopics.getSensorWithTopics().stream().map(SensorWithTopic::getTopic).toArray(String[]::new);

        for (SensorWithTopic sensorWithTopic : sensorWithTopics.getSensorWithTopics()) {
            topicSensorNameMap.put(sensorWithTopic.getTopic(), sensorWithTopic.getSensorName());
            sensorNameKwhMap.put(sensorWithTopic.getSensorName(), new ArrayList<>());
        }

        List<Kwh> weekDataByPeriod = kwhRepository.getDailyDataByPeriod(topics, startInstant, endInstant);
        for (Kwh kwh : weekDataByPeriod) {
            sensorNameKwhMap.get(topicSensorNameMap.get(kwh.getTopic())).add(kwh);
        }

        List<SensorPowerMetric> sensorPowerMetrics = new ArrayList<>();

        for (Map.Entry<String, List<Kwh>> entry : sensorNameKwhMap.entrySet()) {
            List<PowerMetric> powerMetrics = new LinkedList<>();
            List<Kwh> kwhList = entry.getValue();

            Map<Instant, Double> sumByTimezone = kwhList.stream()
                    .collect(Collectors.groupingBy(Kwh::getTime,
                            Collectors.summingDouble(Kwh::getValue)));

            List<Map.Entry<Instant, Double>> collect = sumByTimezone.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .collect(Collectors.toList());

            for (int i = collect.size()-1; i > 0; i--) {
                powerMetrics.add(
                        0,
                        new PowerMetric(
                                "kwh",
                                "day",
                                "1",
                                collect.get(i-1).getKey(),
                                collect.get(i).getValue() - collect.get(i - 1).getValue()
                        )
                );
            }

            sensorPowerMetrics.add(new SensorPowerMetric(entry.getKey(), powerMetrics));
        }

        for (SensorPowerMetric sensorPowerMetric : sensorPowerMetrics) {
            System.out.println("sensorName: " + sensorPowerMetric.getSensorName());
            for (PowerMetric metric : sensorPowerMetric.getPowerMetrics()) {
                System.out.println("type: " + metric.getType());
                System.out.println("unit: " + metric.getUnit());
                System.out.println("per: " + metric.getPer());
                System.out.println("time: " + metric.getTime());
                System.out.println("value: " + metric.getValue());
                System.out.println("==================================================");
            }
        }
    }
}