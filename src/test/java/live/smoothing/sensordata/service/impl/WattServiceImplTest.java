package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.adapter.TopicAdapter;
import live.smoothing.sensordata.dto.TagPowerMetricResponse;
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
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WattServiceImplTest {

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private TopicAdapter topicAdapter;

    @InjectMocks
    private WattServiceImpl wattService;

    @Test
    @DisplayName("10분 단위 전력량 데이터 조회시 데이터 수는 12개를 반환한다.")
    void get10MinuteWattData() {
        // given
        TopicResponse topicResponse = new TopicResponse();
        ReflectionTestUtils.setField(topicResponse, "topics", List.of("topic1", "topic2", "topic3"));

        Instant time = UTCTimeUtil.getRecentMinute(Instant.now(), 10)
                .minus(2, ChronoUnit.HOURS)
                .plus(20, ChronoUnit.MINUTES);

        Point point = new Point();
        ReflectionTestUtils.setField(point, "time", time);
        ReflectionTestUtils.setField(point, "value", 100.0);
        ReflectionTestUtils.setField(point, "topic", "topic1");

        when(seriesRepository.getDataByPeriod(anyString(), anyString(), any(), any(), any())).thenReturn(new ArrayList<>());
        when(seriesRepository.getSumDataFromStart(anyString(), anyString(), any(), any())).thenReturn(List.of(point));
        when(topicAdapter.getTopicAll(anyString())).thenReturn(topicResponse);

        // when
        TagPowerMetricResponse response = wattService.get10MinuteWattData("");

        // then
        verify(seriesRepository, times(1)).getDataByPeriod(anyString(), anyString(), any(), any(), any());
        verify(seriesRepository, times(1)).getSumDataFromStart(anyString(), anyString(), any(), any());
        verify(topicAdapter, times(1)).getTopicAll(anyString());


        assertThat(response.getData()).hasSize(12);
    }

    @Test
    @DisplayName("1시간 단위 전력량 데이터 조회시 데이터 수는 25개를 반환한다.")
    void get1HourWattData() {
        // given
        TopicResponse topicResponse = new TopicResponse();
        ReflectionTestUtils.setField(topicResponse, "topics", List.of("topic1", "topic2", "topic3"));

        Instant time = UTCTimeUtil.getRecentHour(Instant.now())
                .minus(1, ChronoUnit.DAYS);

        Point point = new Point();
        ReflectionTestUtils.setField(point, "time", time);
        ReflectionTestUtils.setField(point, "value", 100.0);
        ReflectionTestUtils.setField(point, "topic", "topic1");

        when(seriesRepository.getDataByPeriod(anyString(), anyString(), any(), any(), any())).thenReturn(new ArrayList<>());
        when(seriesRepository.getSumDataFromStart(anyString(), anyString(), any(), any())).thenReturn(List.of(point));
        when(topicAdapter.getTopicWithTags(anyString(), anyString(), any())).thenReturn(topicResponse);

        // when
        TagPowerMetricResponse response = wattService.get1HourWattData("tag");

        // then
        verify(seriesRepository, times(1)).getDataByPeriod(anyString(), anyString(), any(), any(), any());
        verify(seriesRepository, times(1)).getSumDataFromStart(anyString(), anyString(), any(), any());
        verify(topicAdapter, times(1)).getTopicWithTags(anyString(), anyString(), any());

        assertThat(response.getData()).hasSize(25);
    }
}