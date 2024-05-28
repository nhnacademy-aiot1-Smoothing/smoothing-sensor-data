package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.adapter.TopicAdapter;
import live.smoothing.sensordata.dto.SensorPowerMetricResponse;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
import live.smoothing.sensordata.dto.TimeZoneResponse;
import live.smoothing.sensordata.dto.kwh.TagSensorValueResponse;
import live.smoothing.sensordata.dto.topic.SensorTopicResponse;
import live.smoothing.sensordata.dto.topic.SensorWithTopic;
import live.smoothing.sensordata.dto.topic.TopicResponse;
import live.smoothing.sensordata.entity.Point;
import live.smoothing.sensordata.repository.SeriesRepository;
import live.smoothing.sensordata.util.UTCTimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KwhServiceImplTest {

    @Mock
    SeriesRepository seriesRepository;

    @Mock
    TopicAdapter topicAdapter;

    @InjectMocks
    KwhServiceImpl kwhService;

    @Test
    @DisplayName("최근 48시간 데이터 조회 시 데이터 수는 48개여야 한다.")
    void get48HourData() {
        // given
        TopicResponse topicResponse = new TopicResponse();
        ReflectionTestUtils.setField(topicResponse, "topics", List.of("topic1", "topic2", "topic3"));

        List<Point> aggregation = new ArrayList<>();
        Instant time = UTCTimeUtil.getRecentHour(Instant.now())
                .minus(2, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS);

        for (int i = 0; i < 48; i++) {
            Point point = new Point();
            ReflectionTestUtils.setField(point, "time", time);
            ReflectionTestUtils.setField(point, "value", (double) i);
            ReflectionTestUtils.setField(point, "topic", "topic" + i);
            aggregation.add(point);

            time = time.plus(1, ChronoUnit.HOURS);
        }

        List<Point> raw = new ArrayList<>();
        Point point = new Point();
        ReflectionTestUtils.setField(point, "time", time);
        ReflectionTestUtils.setField(point, "value", 48.0);
        ReflectionTestUtils.setField(point, "topic", "topic" + 48);
        raw.add(point);

        when(seriesRepository.getDataByPeriod(anyString(), anyString(), any(), any(), any())).thenReturn(aggregation);
        when(seriesRepository.getEndData(anyString(), anyString(), any(), any())).thenReturn(raw);
        when(topicAdapter.getTopicAll(anyString())).thenReturn(topicResponse);

        // when
        TagPowerMetricResponse response = kwhService.get48HourData("");

        // then
        verify(seriesRepository, times(1)).getDataByPeriod(anyString(), anyString(), any(), any(), any());
        verify(seriesRepository, times(1)).getEndData(anyString(), anyString(), any(), any());
        verify(topicAdapter, times(1)).getTopicAll(anyString());


        assertThat(response.getData()).hasSize(48);
    }

    @Test
    @DisplayName("최근 2주 데이터 조회 시 데이터 수는 14개여야 한다.")
    void get2WeekData() {
        // given
        TopicResponse topicResponse = new TopicResponse();
        ReflectionTestUtils.setField(topicResponse, "topics", List.of("topic1", "topic2", "topic3"));

        List<Point> aggregation = new ArrayList<>();
        Instant time = UTCTimeUtil.getRecentHour(Instant.now())
                .minus(14, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS);

        for (int i = 0; i < 14; i++) {
            Point point = new Point();
            ReflectionTestUtils.setField(point, "time", time);
            ReflectionTestUtils.setField(point, "value", (double) i);
            ReflectionTestUtils.setField(point, "topic", "topic" + i);
            aggregation.add(point);

            time = time.plus(1, ChronoUnit.DAYS);
        }

        List<Point> raw = new ArrayList<>();
        Point point = new Point();
        ReflectionTestUtils.setField(point, "time", time);
        ReflectionTestUtils.setField(point, "value", 14.0);
        ReflectionTestUtils.setField(point, "topic", "topic" + 14);
        raw.add(point);

        when(seriesRepository.getDataByPeriod(anyString(), anyString(), any(), any(), any())).thenReturn(aggregation);
        when(seriesRepository.getEndData(anyString(), anyString(), any(), any())).thenReturn(raw);
        when(topicAdapter.getTopicWithTags(anyString(), anyString(), any())).thenReturn(topicResponse);

        // when
        TagPowerMetricResponse response = kwhService.get2WeekData("tag");

        // then
        verify(seriesRepository, times(1)).getDataByPeriod(anyString(), anyString(), any(), any(), any());
        verify(seriesRepository, times(1)).getEndData(anyString(), anyString(), any(), any());
        verify(topicAdapter, times(1)).getTopicWithTags(anyString(), anyString(), any());

        assertThat(response.getData()).hasSize(14);
    }

    @Test
    @DisplayName("최근 1달 전력량 조회 시 끝 값의 합에서 처음 값의 합의 뺀 값을 반환한다.")
    void getCurrentMonthKwh() {
        // given
        TopicResponse topicResponse = new TopicResponse();
        ReflectionTestUtils.setField(topicResponse, "topics", List.of("topic1", "topic2", "topic3"));

        List<Point> start = new ArrayList<>();
        Instant time = UTCTimeUtil.getRecentMonth(Instant.now());

        Point startPoint = new Point();
        ReflectionTestUtils.setField(startPoint, "time", time);
        ReflectionTestUtils.setField(startPoint, "value", (double) 1);
        ReflectionTestUtils.setField(startPoint, "topic", "topic");
        start.add(startPoint);

        List<Point> end = new ArrayList<>();
        Point endPoint = new Point();
        ReflectionTestUtils.setField(endPoint, "time", time);
        ReflectionTestUtils.setField(endPoint, "value", 30.0);
        ReflectionTestUtils.setField(endPoint, "topic", "topic" + 30);
        end.add(endPoint);

        when(seriesRepository.getStartData(anyString(), anyString(), any(), any())).thenReturn(start);
        when(seriesRepository.getEndData(anyString(), anyString(), any(), any())).thenReturn(end);
        when(topicAdapter.getTopicAll(any())).thenReturn(topicResponse);

        // when
        Double value = kwhService.getCurrentMonthKwh();

        // then
        verify(seriesRepository, times(1)).getStartData(anyString(), anyString(), any(), any());
        verify(seriesRepository, times(1)).getEndData(anyString(), anyString(), any(), any());
        verify(topicAdapter, times(1)).getTopicAll(any());

        assertThat(value).isEqualTo(29.0);
    }

    @Test
    @DisplayName("최근 일주일 시간대별 전력량 조회시 값은 4개로 나누어 전력량을 반환한다")
    void getWeeklyDataByTimeOfDay() {
        // given
        TopicResponse topicResponse = new TopicResponse();
        ReflectionTestUtils.setField(topicResponse, "topics", List.of("topic1", "topic2", "topic3"));

        List<Point> aggregation = new ArrayList<>();
        Instant time = UTCTimeUtil.getRecentDay(Instant.now())
                .minus(7, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS);

        for (int i = 0; i < 25; i++) {
            Point point = new Point();
            ReflectionTestUtils.setField(point, "time", time);
            ReflectionTestUtils.setField(point, "value", (double) i);
            ReflectionTestUtils.setField(point, "topic", "topic" + i);
            aggregation.add(point);

            time = time.plus(1, ChronoUnit.HOURS);
        }

        when(seriesRepository.getDataByPeriod(anyString(), anyString(), any(), any(), any())).thenReturn(aggregation);
        when(topicAdapter.getTopicAll(anyString())).thenReturn(topicResponse);

        // when
        TimeZoneResponse timeZoneResponse = kwhService.getWeeklyDataByTimeOfDay();

        // then
        verify(seriesRepository, times(1)).getDataByPeriod(anyString(), anyString(), any(), any(), any());
        verify(topicAdapter, times(1)).getTopicAll(anyString());

        assertAll(
                () -> assertThat(timeZoneResponse.getData()).hasSize(4),
                () -> assertThat(timeZoneResponse.getData().get(0).getValue()).isEqualTo(6.0),
                () -> assertThat(timeZoneResponse.getData().get(1).getValue()).isEqualTo(6.0),
                () -> assertThat(timeZoneResponse.getData().get(2).getValue()).isEqualTo(6.0),
                () -> assertThat(timeZoneResponse.getData().get(3).getValue()).isEqualTo(6.0)
        );
    }

    @Test
    @DisplayName("특정 기간 동안 일별 전력량 조회 시 데이터 수는 해당 기간에 존재하는 데이터 수를 반환한다.")
    void getDailyTotalDataByPeriod() {
        // given
        TopicResponse topicResponse = new TopicResponse();
        ReflectionTestUtils.setField(topicResponse, "topics", List.of("topic1", "topic2", "topic3"));

        Instant start = LocalDateTime.of(2021, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
        Instant end = LocalDateTime.of(2021, 1, 8, 0, 0).toInstant(ZoneOffset.UTC);

        when(seriesRepository.getDataByPeriod(anyString(), anyString(), any(), any(), any())).thenReturn(new ArrayList<>());
        when(topicAdapter.getTopicAll(anyString())).thenReturn(topicResponse);

        // when
        TagPowerMetricResponse response = kwhService.getDailyTotalDataByPeriod(start, end, "");

        // then
        verify(seriesRepository, times(1)).getDataByPeriod(anyString(), anyString(), any(), any(), any());
        verify(topicAdapter, times(1)).getTopicAll(anyString());

        assertThat(response.getData()).isEmpty();
    }

    @Test
    @DisplayName("특정 기간 동안 일별 전력량 조회 시 데이터 수는 해당 기간에 존재하는 데이터 수를 반환한다.")
    void getDailySensorDataByPeriod() {
        // given
        SensorTopicResponse sensorTopicResponse = new SensorTopicResponse();
        SensorWithTopic sensorWithTopic = new SensorWithTopic();
        ReflectionTestUtils.setField(sensorWithTopic, "sensorName", "name");
        ReflectionTestUtils.setField(sensorWithTopic, "topic", "topic1");
        ReflectionTestUtils.setField(sensorTopicResponse, "sensorWithTopics", List.of(sensorWithTopic));

        Point point = new Point();
        ReflectionTestUtils.setField(point, "time", LocalDateTime.of(2021, 1, 1, 0, 0).toInstant(ZoneOffset.UTC));
        ReflectionTestUtils.setField(point, "value", 1.0);
        ReflectionTestUtils.setField(point, "topic", "topic1");

        Instant start = LocalDateTime.of(2021, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
        Instant end = LocalDateTime.of(2021, 1, 8, 0, 0).toInstant(ZoneOffset.UTC);

        when(seriesRepository.getDataByPeriod(anyString(), anyString(), any(), any(), any())).thenReturn(List.of(point));
        when(topicAdapter.getSensorWithTopicAll(anyString())).thenReturn(sensorTopicResponse);

        // when
        SensorPowerMetricResponse response = kwhService.getDailySensorDataByPeriod(start, end, "");

        // then
        verify(seriesRepository, times(1)).getDataByPeriod(anyString(), anyString(), any(), any(), any());
        verify(topicAdapter, times(1)).getSensorWithTopicAll(anyString());

        assertThat(response.getData()).hasSize(1);
    }

    @Test
    @DisplayName("특정 기간 동안 센서별 전체 전력량 조회 시 데이터 수는 센서 수를 반환한다.")
    void getTotalSensorData() {
        // given
        SensorTopicResponse sensorTopicResponse = new SensorTopicResponse();
        SensorWithTopic sensorWithTopic = new SensorWithTopic();
        ReflectionTestUtils.setField(sensorWithTopic, "sensorName", "name");
        ReflectionTestUtils.setField(sensorWithTopic, "topic", "topic1");
        ReflectionTestUtils.setField(sensorTopicResponse, "sensorWithTopics", List.of(sensorWithTopic));

        Point point = new Point();
        ReflectionTestUtils.setField(point, "time", LocalDateTime.of(2021, 1, 1, 0, 0).toInstant(ZoneOffset.UTC));
        ReflectionTestUtils.setField(point, "value", 1.0);
        ReflectionTestUtils.setField(point, "topic", "topic1");

        Instant start = LocalDateTime.of(2021, 1, 1, 0, 0).toInstant(ZoneOffset.UTC);
        Instant end = LocalDateTime.of(2021, 1, 8, 0, 0).toInstant(ZoneOffset.UTC);

        when(seriesRepository.getStartData(anyString(), anyString(), any(), any())).thenReturn(List.of(point));
        when(seriesRepository.getEndData(anyString(), anyString(), any(), any())).thenReturn(List.of(point));
        when(topicAdapter.getSensorWithTopics(anyString(), anyString(), any())).thenReturn(sensorTopicResponse);

        // when
        TagSensorValueResponse response = kwhService.getTotalSensorData("tag", start, end);

        // then
        verify(seriesRepository, times(1)).getStartData(anyString(), anyString(), any(), any());
        verify(seriesRepository, times(1)).getEndData(anyString(), anyString(), any(), any());
        verify(topicAdapter, times(1)).getSensorWithTopics(anyString(), anyString(), any());

        assertThat(response.getData()).hasSize(1);
    }

    @Test
    @DisplayName("전체 시간당 전력량 조회시 현재 존재하는 모든 데이터 수를 반환한다.")
    void getHourlyTotalData() {
        // given
        TopicResponse topicResponse = new TopicResponse();
        ReflectionTestUtils.setField(topicResponse, "topics", List.of("topic1", "topic2", "topic3"));

        List<Point> aggregation = new ArrayList<>();
        Instant time = UTCTimeUtil.getRecentHour(Instant.now())
                .minus(1, ChronoUnit.DAYS).plus(9, ChronoUnit.HOURS);

        for (int i = 0; i < 25; i++) {
            Point point = new Point();
            ReflectionTestUtils.setField(point, "time", time);
            ReflectionTestUtils.setField(point, "value", (double) i);
            ReflectionTestUtils.setField(point, "topic", "topic" + i);
            aggregation.add(point);

            time = time.plus(1, ChronoUnit.HOURS);
        }

        when(seriesRepository.getDataByPeriod(anyString(), anyString(), any(), any(), any())).thenReturn(aggregation);
        when(topicAdapter.getTopicAll(anyString())).thenReturn(topicResponse);

        // when
        TagPowerMetricResponse response = kwhService.getHourlyTotalData();

        // then
        verify(seriesRepository, times(1)).getDataByPeriod(anyString(), anyString(), any(), any(), any());
        verify(topicAdapter, times(1)).getTopicAll(anyString());

        assertThat(response.getData()).hasSize(24);
    }
}