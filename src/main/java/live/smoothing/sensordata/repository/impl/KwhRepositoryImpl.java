package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import live.smoothing.sensordata.entity.Kwh;
import live.smoothing.sensordata.repository.KwhRepository;
import live.smoothing.sensordata.util.TimeProvider;
import live.smoothing.sensordata.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static live.smoothing.sensordata.util.FluxUtil.*;

/**
 * InfluxDB를 이용한 KwhRepository 구현체
 *
 * @author 신민석, 박영준
 */
@Service
@RequiredArgsConstructor
public class KwhRepositoryImpl implements KwhRepository {

    private final InfluxDBClient rawInfluxClient;
    private final InfluxDBClient aggregationInfluxClient;
    private final TimeProvider timeProvider;

    private static final String AGGREGATION_BUCKET = "aggregation";
    private static final String RAW_BUCKET = "powermetrics_data";


    /**
     * InfluxDB에 저장된 데이터를 조회하여 24시간 동안의 데이터를 1시간 단위로 반환(집계된 데이터)
     *
     * @param topics 조회할 topic의 이름들
     * @return 집계된 Kwh의 리스트를 반환
     */
    @Override
    public List<Kwh> get48HourData(String[] topics) {
        Flux query =
                getKwhFromStart(
                        AGGREGATION_BUCKET,
                        "kwh_hour",
                        timeProvider.nowInstant().minus(2, ChronoUnit.DAYS),
                        timeProvider.nowInstant(),
                        topics
                );

        return aggregationInfluxClient.getQueryApi().query(query.toString(), Kwh.class);
    }

    /**
     * InfluxDB를 조회하여 7일 동안의 데이터를 1일 단위로 반환(집계 데이터)
     *
     * @param topics 조회할 topic의 이름들
     * @return 집계된 Kwh의 리스트를 반환
     */
    @Override
    public List<Kwh> getAggregation2WeekData(String[] topics) {
        Flux query =
                getKwhFromStart(
                        AGGREGATION_BUCKET,
                        "kwh_daily4",
                        timeProvider.nowInstant().minus(14, ChronoUnit.DAYS),
                        timeProvider.nowInstant(),
                        topics
                );

        return aggregationInfluxClient.getQueryApi().query(query.toString(), Kwh.class);
    }

    /**
     * InfluxDB를 조회하여 현재 달의 처음과 끝 값을 반환
     *
     * @param topics 조회할 topic의 이름들
     * @return 현재 달의 처음과 끝 값 리스트
     */
    @Override
    public List<Kwh> getStartData(String[] topics, Instant start) {

        Flux firstQuery = getFirstKwhFromStart(
                RAW_BUCKET,
                "mqtt_consumer",
                start,
                topics
        );

        return rawInfluxClient.getQueryApi().query(firstQuery.toString(), Kwh.class);
    }

    @Override
    public List<Kwh> getEndData(String[] topics, Instant start) {

        Flux lastQuery = getLastKwhFromStart(
                RAW_BUCKET,
                "mqtt_consumer",
                start,
                topics
        );

        return rawInfluxClient.getQueryApi().query(lastQuery.toString(), Kwh.class);
    }

    @Override
    public List<Kwh> getWeekDataByHour(String[] topics) {
        Flux query =
                getKwhFromStart(
                        AGGREGATION_BUCKET,
                        "kwh_hour",
                        TimeUtil.getRecentDay(timeProvider.nowInstant()
                                .minus(7, ChronoUnit.DAYS)),
                        TimeUtil.getRecentDay(timeProvider.nowInstant())
                                .plus(30, ChronoUnit.MINUTES),
                        topics
                );

        return aggregationInfluxClient.getQueryApi().query(query.toString(), Kwh.class);
    }

    @Override
    public List<Kwh> getDailyDataByPeriod(String[] topics, Instant start, Instant end) {
        Flux query =
                getKwhFromStart(
                        AGGREGATION_BUCKET,
                        "kwh_daily4",
                        start,
                        end,
                        topics
                );

        return aggregationInfluxClient.getQueryApi().query(query.toString(), Kwh.class);
    }

    @Override
    public List<Kwh> getHourlyTotalData(String[] topics) {
        Flux query =
                getKwhFromStart(
                        AGGREGATION_BUCKET,
                        "kwh_hour",
                        Instant.ofEpochMilli(0),
                        timeProvider.nowInstant(),
                        topics
                );

        return aggregationInfluxClient.getQueryApi().query(query.toString(), Kwh.class);
    }
}
