package live.smoothing.sensordata.util;

import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

class FluxQueryTest {

    @Test
    @DisplayName("getOrRestrictions 메서드 테스트")
    void testGetOrRestrictions() {
        // given
        String[] topics = {"topic1", "topic2", "topic3"};

        // when
        Restrictions result = (Restrictions) ReflectionTestUtils.invokeMethod(FluxQuery.class, "getOrRestrictions", (Object) topics);

        // then
    assertThat(result).hasToString("((r[\"topic\"] == \"topic1\" or r[\"topic\"] == \"topic2\") or r[\"topic\"] == \"topic3\")");
    }

    @Test
    @DisplayName("데이터를 처음부터 가져오는 Flux Query 생성")
    void fetchDataFromStart() {
        // given
        LocalDateTime start = LocalDateTime.of(2024, 5, 22, 10, 0);
        LocalDateTime end = LocalDateTime.of(2024, 5, 22, 10, 1);

        // when
        Flux query = FluxQuery.fetchDataFromStart(
                "bucketName",
                "measurementName",
                start.toInstant(ZoneOffset.UTC),
                end.toInstant(ZoneOffset.UTC),
                new String[]{"topic1", "topic2"}
        );

        // then
    assertThat(query).hasToString("from(bucket:\"bucketName\")\n" +
            "\t|> range(start:2024-05-22T10:00:00.000000000Z, stop:2024-05-22T10:01:00.000000000Z)\n" +
            "\t|> filter(fn: (r) => r[\"_measurement\"] == \"measurementName\")\n" +
            "\t|> filter(fn: (r) => (r[\"topic\"] == \"topic1\" or r[\"topic\"] == \"topic2\"))\n" +
            "\t|> timeShift(duration:9h)");
    }

    @Test
    @DisplayName("시작 범위부터 첫번째 데이터를 가져오는 Flux Query 생성")
    void fetchFirstDataFromStart() {
        // given
        LocalDateTime start = LocalDateTime.of(2024, 5, 22, 10, 0);

        // when
        Flux query = FluxQuery.fetchFirstDataFromStart(
                "bucketName",
                "measurementName",
                start.toInstant(ZoneOffset.UTC),
                new String[]{"topic1", "topic2"}
        );

        // then
    assertThat(query).hasToString("from(bucket:\"bucketName\")\n" +
            "\t|> range(start:2024-05-22T10:00:00.000000000Z)\n" +
            "\t|> filter(fn: (r) => r[\"_measurement\"] == \"measurementName\")\n" +
            "\t|> filter(fn: (r) => (r[\"topic\"] == \"topic1\" or r[\"topic\"] == \"topic2\"))\n" +
            "\t|> first()\n" +
            "\t|> timeShift(duration:9h)");
    }

    @Test
    @DisplayName("시작 범위부터 마지막 데이터를 가져오는 Flux Query 생성")
    void fetchLastDataFromStart() {
        // given
        LocalDateTime start = LocalDateTime.of(2024, 5, 22, 10, 0);

        // when
        Flux query = FluxQuery.fetchLastDataFromStart(
                "bucketName",
                "measurementName",
                start.toInstant(ZoneOffset.UTC),
                new String[]{"topic1", "topic2"}
        );

        // then
    assertThat(query).hasToString("from(bucket:\"bucketName\")\n" +
            "\t|> range(start:2024-05-22T10:00:00.000000000Z)\n" +
            "\t|> filter(fn: (r) => r[\"_measurement\"] == \"measurementName\")\n" +
            "\t|> filter(fn: (r) => (r[\"topic\"] == \"topic1\" or r[\"topic\"] == \"topic2\"))\n" +
            "\t|> last()\n" +
            "\t|> timeShift(duration:9h)");
    }

    @Test
    @DisplayName("시작 범위부터 합계 데이터를 가져오는 Flux Query 생성")
    void fetchSumDataFromStart() {
        // given
        LocalDateTime start = LocalDateTime.of(2024, 5, 22, 10, 0);

        // when
        Flux query = FluxQuery.fetchSumDataFromStart(
                "bucketName",
                "measurementName",
                start.toInstant(ZoneOffset.UTC),
                new String[]{"topic1", "topic2"}
        );

        // then
    assertThat(query).hasToString("from(bucket:\"bucketName\")\n" +
            "\t|> range(start:2024-05-22T10:00:00.000000000Z)\n" +
            "\t|> filter(fn: (r) => r[\"_measurement\"] == \"measurementName\")\n" +
            "\t|> filter(fn: (r) => (r[\"topic\"] == \"topic1\" or r[\"topic\"] == \"topic2\"))\n" +
            "\t|> sum()\n" +
            "\t|> timeShift(duration:9h)");
    }
}