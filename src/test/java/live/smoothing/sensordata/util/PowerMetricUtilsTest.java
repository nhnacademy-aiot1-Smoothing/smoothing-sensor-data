package live.smoothing.sensordata.util;

import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.entity.Point;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PowerMetricUtilsTest {

    @Test
    @DisplayName("시간과 토픽이 겹치는 데이터 중복을 제거한다.")
    void testGetDeduplicationList() {
        // given
        Instant now = Instant.now();

        List<Point> timeValueList = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            Point point = new Point();
            ReflectionTestUtils.setField(point, "topic", "topic");
            ReflectionTestUtils.setField(point, "time", now);
            ReflectionTestUtils.setField(point, "value", (double) i);
            timeValueList.add(point);
        }

        // when
        List<Point> result = PowerMetricUtils.getDeduplicationList(timeValueList);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("시간과 토픽이 겹치는 중복을 제거했을 때 제일 큰 값만 남긴다.")
    void testGetDeduplicationList_max() {
        //given
        Instant now = Instant.now();

        List<Point> timeValueList = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            Point point = new Point();
            ReflectionTestUtils.setField(point, "topic", "topic");
            ReflectionTestUtils.setField(point, "time", now);
            ReflectionTestUtils.setField(point, "value", (double) i);
            timeValueList.add(point);
        }

        // when
        List<Point> result = PowerMetricUtils.getDeduplicationList(timeValueList);

        // then
        assertThat(result.get(0).getValue()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("같은 시간대별로 합산한다.")
    void getSumByTimezone() {

        // given
        Instant now = Instant.now();

        List<Point> timeValueList = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            Point point = new Point();
            ReflectionTestUtils.setField(point, "topic", "topic");
            ReflectionTestUtils.setField(point, "time", now);
            ReflectionTestUtils.setField(point, "value", (double) i);
            timeValueList.add(point);
        }

        //when
        Map<Instant, Double> sumByTimezone = PowerMetricUtils.getSumByTimezone(timeValueList);

        // then
        assertAll(
                () -> assertThat(sumByTimezone).hasSize(1),
                () -> assertThat(sumByTimezone).containsEntry(now, 10.0)
        );
    }

    @Test
    @DisplayName("시간대별로 정렬한다.")
    void getSortedByTimeList() {
        // given
        Instant now = Instant.now();

        List<Point> timeValueList = new ArrayList<>();

        for (int i = 4; i >= 1; i--) {
            Point point = new Point();
            ReflectionTestUtils.setField(point, "topic", "topic");
            ReflectionTestUtils.setField(point, "time", now.plusSeconds(i));
            ReflectionTestUtils.setField(point, "value", (double) i);
            timeValueList.add(point);
        }

        Map<Instant, Double> sumByTimezone = PowerMetricUtils.getSumByTimezone(timeValueList);

        // when
        List<Map.Entry<Instant, Double>> collect = PowerMetricUtils.getSortedByTimeList(sumByTimezone);

        // then
        assertThat(collect).hasSize(4);
        assertThat(collect.get(0).getKey()).isEqualTo(now.plusSeconds(1));
        assertThat(collect.get(1).getKey()).isEqualTo(now.plusSeconds(2));
        assertThat(collect.get(2).getKey()).isEqualTo(now.plusSeconds(3));
        assertThat(collect.get(3).getKey()).isEqualTo(now.plusSeconds(4));
    }

    @Test
    @DisplayName("비어있는 시간을 이전 값으로 채워넣는다.")
    void testGetFillTimeMap() {
        // given
        Map<Instant, Double> expectedMap = new HashMap<>();
        expectedMap.put(Instant.parse("2023-05-01T00:00:00Z"), 5.0);
        expectedMap.put(Instant.parse("2023-05-01T01:00:00Z"), 10.0);
        expectedMap.put(Instant.parse("2023-05-01T02:00:00Z"), 10.0);
        expectedMap.put(Instant.parse("2023-05-01T03:00:00Z"), 15.0);
        expectedMap.put(Instant.parse("2023-05-01T04:00:00Z"), 15.0);

        Map<Instant, Double> sumByTimezone = new HashMap<>();
        sumByTimezone.put(Instant.parse("2023-05-01T00:00:00Z"), 5.0);
        sumByTimezone.put(Instant.parse("2023-05-01T01:00:00Z"), 10.0);
        sumByTimezone.put(Instant.parse("2023-05-01T03:00:00Z"), 15.0);

        Instant start = Instant.parse("2023-05-01T00:00:00Z");
        Instant end = Instant.parse("2023-05-01T04:00:00Z");
        ChronoUnit intervalUnit = ChronoUnit.HOURS;
        long intervalAmount = 1;

        // when
        Map<Instant, Double> filledMap = PowerMetricUtils.getFillTimeMap(
                sumByTimezone,
                start.minus(9, ChronoUnit.HOURS),
                end.minus(9, ChronoUnit.HOURS),
                intervalUnit,
                intervalAmount
        );

        // then
        assertEquals(expectedMap, filledMap);
    }

    @Test
    @DisplayName("시간대별로 정렬된 데이터를 이용해 WattPowerMetrics를 생성한다.")
    void getWattPowerMetricsByMap() {
        // given
        Instant now = Instant.now();

        List<Map.Entry<Instant, Double>> collect = new ArrayList<>();

        for (int i = 1; i <= 4; i++) {
            collect.add(Map.entry(now.plusSeconds(i), (double) i));
        }

        // when
        List<PowerMetric> powerMetrics = PowerMetricUtils.getWattPowerMetricsByMap(collect, "type", "unit", "per");

        // then
        assertThat(powerMetrics).hasSize(4);
        assertThat(ReflectionTestUtils.getField(powerMetrics.get(0), "value")).isEqualTo(1.0);
        assertThat(ReflectionTestUtils.getField(powerMetrics.get(1), "value")).isEqualTo(2.0);
        assertThat(ReflectionTestUtils.getField(powerMetrics.get(2), "value")).isEqualTo(3.0);
        assertThat(ReflectionTestUtils.getField(powerMetrics.get(3), "value")).isEqualTo(4.0);
    }
}