package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.Igr;
import live.smoothing.sensordata.entity.Point;
import live.smoothing.sensordata.repository.SeriesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IgrServiceImplTest {

    @Mock
    SeriesRepository seriesRepository;

    @InjectMocks
    IgrServiceImpl igrService;

    @Test
    @DisplayName("클래스 A 누전 조회")
    void getClassIgr() {
        // given
        Point point = new Point();
        ReflectionTestUtils.setField(point, "value", 1.0);

        when(seriesRepository.getEndData(anyString(), anyString(), any(), any())).thenReturn(List.of(point));

        // when
        Igr igr = igrService.getClassIgr();

        // then
        assertEquals(1.0, igr.getValue());
    }

    @Test
    @DisplayName("클래스 A 누전 조회시 데이터가 없을 경우 -1.0 반환")
    void getClassIgr_not_exists_data() {
        // given

        when(seriesRepository.getEndData(anyString(), anyString(), any(), any())).thenReturn(List.of());

        // when
        Igr igr = igrService.getClassIgr();

        // then
        assertEquals(-1.0, igr.getValue());
    }

    @Test
    @DisplayName("Office 누전 조회")
    void getOfficeIgr() {
        // given
        Point point = new Point();
        ReflectionTestUtils.setField(point, "value", 1.0);

        when(seriesRepository.getEndData(anyString(), anyString(), any(), any())).thenReturn(List.of(point));

        // when
        Igr igr = igrService.getOfficeIgr();

        // then
        assertEquals(1.0, igr.getValue());
    }

    @Test
    @DisplayName("Office 누전 조회시 데이터가 없을 경우 -1.0 반환")
    void getOfficeIgr_not_exists_data() {
        // given

        when(seriesRepository.getEndData(anyString(), anyString(), any(), any())).thenReturn(List.of());

        // when
        Igr igr = igrService.getOfficeIgr();

        // then
        assertEquals(-1.0, igr.getValue());
    }
}