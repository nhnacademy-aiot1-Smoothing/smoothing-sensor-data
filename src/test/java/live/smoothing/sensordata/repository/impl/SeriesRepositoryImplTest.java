//package live.smoothing.sensordata.repository.impl;
//
//import com.influxdb.client.InfluxDBClient;
//import com.influxdb.client.InfluxDBClientFactory;
//import com.influxdb.client.WriteApiBlocking;
//import com.influxdb.client.domain.Bucket;
//import com.influxdb.client.domain.Organization;
//import com.influxdb.client.domain.WritePrecision;
//import live.smoothing.sensordata.entity.Point;
//import live.smoothing.sensordata.repository.SeriesRepository;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//import java.time.ZoneOffset;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@DataJpaTest
//class SeriesRepositoryImplTest {
//
//    private SeriesRepository seriesRepository;
//
//    private InfluxDBClient influxDBClient;
//
//    @Value("${influxdb.test.url}")
//    private String testUrl;
//
//    @Value("${influxdb.test.token}")
//    private String testToken;
//
//    @Value("${influxdb.test.org}")
//    private String testOrg;
//
//    private String testBucketId;
//
//    @BeforeEach
//    void setUp() {
//        this.influxDBClient= InfluxDBClientFactory
//                .create(
//                        testUrl,
//                        testToken.toCharArray(),
//                        testOrg
//                );
//
//        Organization organizations = influxDBClient.getOrganizationsApi().findOrganizations().stream().findAny().orElseThrow();
//        Organization orgId = influxDBClient.getOrganizationsApi().findOrganizationByID(organizations.getId());
//        Bucket testBucket = influxDBClient.getBucketsApi().createBucket("test", orgId);
//        this.testBucketId = testBucket.getId();
//
//        this.seriesRepository = new SeriesRepositoryImpl(influxDBClient, influxDBClient);
//
//        Instant start = LocalDateTime.of(2024, 5, 1, 0, 0, 0).toInstant(ZoneOffset.of("+09:00"));
//        Instant next = LocalDateTime.of(2024, 5, 2, 0, 0, 0).toInstant(ZoneOffset.of("+09:00"));
//
//        WriteApiBlocking writeApiBlocking = influxDBClient.getWriteApiBlocking();
//
//        writeApiBlocking.writeRecords(
//                "test",
//                "smoothing",
//                WritePrecision.MS,
//                List.of(
//                        "kwh,topic=topic1 value=1.0 " + start.toEpochMilli(),
//                        "kwh,topic=topic1 value=2.0 " + next.toEpochMilli()
//                )
//        );
//    }
//
//    @AfterEach
//    void cleanUp() {
//        influxDBClient.getBucketsApi().deleteBucket(testBucketId);
//    }
//
//    @Test
//    void testPing() {
//        Boolean rawPing = influxDBClient.ping();
//
//        assertTrue(rawPing);
//    }
//
//    @Test
//    @DisplayName("시작 시간을 기준으로 첫번째 값을 반환")
//    void getStartData() {
//        List<Point> startData = seriesRepository.getStartData(
//                "test",
//                "kwh",
//                Instant.ofEpochMilli(0),
//                new String[]{"topic1"}
//        );
//
//        assertAll(
//                () -> assertThat(startData).hasSize(1),
//                () -> assertThat(startData.get(0).getValue()).isEqualTo(1.0)
//        );
//    }
//
//    @Test
//    @DisplayName("시작 시간을 기준으로 마지막 값을 반환")
//    void getEndData() {
//        List<Point> endData = seriesRepository.getEndData(
//                "test",
//                "kwh",
//                Instant.ofEpochMilli(0),
//                new String[]{"topic1"}
//        );
//
//        assertAll(
//                () -> assertThat(endData).hasSize(1),
//                () -> assertThat(endData.get(0).getValue()).isEqualTo(2.0)
//        );
//    }
//
//    @Test
//    @DisplayName("시작 시간과 끝 시간을 기준으로 값을 반환")
//    void getDataByPeriod() {
//        List<Point> dataByPeriod = seriesRepository.getDataByPeriod(
//                "test",
//                "kwh",
//                Instant.ofEpochMilli(0),
//                Instant.now(),
//                new String[]{"topic1"}
//        );
//
//        assertThat(dataByPeriod).hasSize(2);
//    }
//
//    @Test
//    @DisplayName("시작 시간을 기준으로 합계 값을 반환")
//    void getSumData() {
//        List<Point> sumData = seriesRepository.getSumDataFromStart(
//                "test",
//                "kwh",
//                Instant.ofEpochMilli(0),
//                new String[]{"topic1"}
//        );
//
//        assertAll(
//                () -> assertThat(sumData).hasSize(1),
//                () -> assertThat(sumData.get(0).getValue()).isEqualTo(3.0)
//        );
//    }
//}