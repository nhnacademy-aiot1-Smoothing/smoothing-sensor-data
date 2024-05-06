package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxTable;
import com.influxdb.query.dsl.Flux;
import live.smoothing.sensordata.entity.Kwh;
import live.smoothing.sensordata.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static live.smoothing.sensordata.util.FluxUtil.getKwhFromStart;

@Service
public class StatisticsRepositoryImpl implements StatisticsRepository {

    private final InfluxDBClient aggregationInfluxClient;
    //Todo: 버킷이름
    private static final String BUCKET = "";

    @Autowired
    public StatisticsRepositoryImpl(InfluxDBClient aggregationInfluxClient) {
        this.aggregationInfluxClient = aggregationInfluxClient;
    }

    public List<Kwh> getPowerUsageData(String measurement,
                                       Instant start,
                                       Instant end,
                                       String[] topics) {

        Flux query =
                getKwhFromStart(
                        BUCKET,
                        measurement,
                        start,
                        end,
                        topics
                );

        QueryApi queryApi = aggregationInfluxClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(query.toString());

        return tables.stream()
                .flatMap(table -> table.getRecords().stream())
                .map(record -> {
                    Kwh kwh = new Kwh();
                    kwh.setTime(record.getTime());
                    kwh.setValue((Double) record.getValueByKey("_value"));
                    return kwh;
                }).collect(Collectors.toList());
    }
}
