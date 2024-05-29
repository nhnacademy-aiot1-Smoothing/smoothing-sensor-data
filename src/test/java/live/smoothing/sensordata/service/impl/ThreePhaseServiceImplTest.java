package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.dto.phase.PhaseResponse;
import live.smoothing.sensordata.entity.Point;
import live.smoothing.sensordata.repository.SeriesRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThreePhaseServiceImplTest {

    @Mock
    private SeriesRepository seriesRepository;

    @InjectMocks
    private ThreePhaseServiceImpl threePhaseService;

    private static final String CLASS_LL = "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ll_average";
    private static final String CLASS_LN = "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ln_average";
    private static final String OFFICE_LL = "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ll_average";
    private static final String OFFICE_LN = "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/voltage/ph/total/de/v123_ln_average";

    @Test
    @DisplayName("3상 전류 조회")
    void getThreePhase() {
        // given
        List<Point> voltageData = new ArrayList<>();
        List.of(CLASS_LL, CLASS_LN, OFFICE_LL, OFFICE_LN).forEach(topic -> {
            Point point = new Point();
            ReflectionTestUtils.setField(point, "value", 1.0);
            ReflectionTestUtils.setField(point, "topic", topic);
            ReflectionTestUtils.setField(point, "time", Instant.now());
            voltageData.add(point);
        });

        when(seriesRepository.getEndData(anyString(), anyString(), any(), any())).thenReturn(voltageData);

        // when
        PhaseResponse threePhase = threePhaseService.getThreePhase();

        // then
        assertAll(
                () -> assertEquals(1.0, threePhase.getThreePhases().get(0).getTop().getValue()),
                () -> assertEquals(1.0, threePhase.getThreePhases().get(0).getTop().getValue()),
                () -> assertEquals(1.0, threePhase.getThreePhases().get(1).getTop().getValue()),
                () -> assertEquals(1.0, threePhase.getThreePhases().get(1).getTop().getValue())
        );
    }

    @Test
    @DisplayName("3상 전류 조회시 데이터가 없을 경우 -1.0 반환")
    void getThreePhase_not_exists_data() {
        // given
        when(seriesRepository.getEndData(anyString(), anyString(), any(), any())).thenReturn(new ArrayList<>());

        // when
        PhaseResponse threePhase = threePhaseService.getThreePhase();

        // then
        assertAll(
                () -> assertEquals(-1.0, threePhase.getThreePhases().get(0).getTop().getValue()),
                () -> assertEquals(-1.0, threePhase.getThreePhases().get(0).getTop().getValue()),
                () -> assertEquals(-1.0, threePhase.getThreePhases().get(1).getTop().getValue()),
                () -> assertEquals(-1.0, threePhase.getThreePhases().get(1).getTop().getValue())
        );
    }
}