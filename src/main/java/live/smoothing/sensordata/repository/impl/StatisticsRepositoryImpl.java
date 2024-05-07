package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.dsl.Flux;
import live.smoothing.sensordata.dto.statistics.KwhTimeSeriesResponse;
import live.smoothing.sensordata.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static live.smoothing.sensordata.util.FluxUtil.getAggregatedPowerUsage;

/**
 * InfluxDB를 이용한 StatisticsRepository 구현체
 *
 * @author 신민석
 */
@Service
public class StatisticsRepositoryImpl implements StatisticsRepository {

    private final InfluxDBClient aggregationInfluxClient;

    //Todo: 버킷이름
    private static final String BUCKET = "";

    @Autowired
    public StatisticsRepositoryImpl(InfluxDBClient aggregationInfluxClient) {
        this.aggregationInfluxClient = aggregationInfluxClient;
    }

    @Override
    public KwhTimeSeriesResponse getPowerUsageData(String measurement,
                                                   Instant start,
                                                   Instant end,
                                                   String[] topics,
                                                   String period) {

        Flux query =
                getAggregatedPowerUsage(
                        BUCKET,
                        measurement,
                        start,
                        end,
                        period,
                        topics
                );

        QueryApi queryApi = aggregationInfluxClient.getQueryApi();
        List<Double> values = queryApi.query(query.toString())
                .stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> (Double) record.getValueByKey("_value"))
                .collect(Collectors.toList());

        return new KwhTimeSeriesResponse(period, values);
    }
}
