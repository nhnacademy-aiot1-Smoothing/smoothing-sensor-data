package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import live.smoothing.sensordata.dto.Igr;
import live.smoothing.sensordata.repository.IgrRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static live.smoothing.sensordata.util.FluxUtil.getKwhFromStart;

@Service
@RequiredArgsConstructor
public class IgrRepositoryImpl implements IgrRepository {

    private final InfluxDBClient rawInfluxClient;
    private static final String RAW_BUCKET = "powermetrics_data";
    private final String IGR_CLASS_A = "data/s/nhnacademy/b/gyeongnam/p/class_a/d/gems-3500/e/electrical_energy/t/main/ph/total/de/igr";
    private final String IGR_OFFICE_A = "data/s/nhnacademy/b/gyeongnam/p/office/d/gems-3500/e/electrical_energy/t/main/ph/total/de/igr";

    @Override
    public Igr getClassIgr() {

        Flux query =
                getKwhFromStart(
                        RAW_BUCKET,
                        "mqtt_consumer",
                        Instant.now().minus(2, ChronoUnit.DAYS),
                        Instant.now(),
                        new String[]{IGR_CLASS_A}
                );

        List<Igr> igrs = rawInfluxClient.getQueryApi().query(query.toString(), Igr.class);
        return igrs.get(0);
    }

    @Override
    public Igr getOfficeIgr() {

        Flux query =
                getKwhFromStart(
                        RAW_BUCKET,
                        "mqtt_consumer",
                        Instant.now().minus(2, ChronoUnit.DAYS),
                        Instant.now(),
                        new String[]{IGR_OFFICE_A}
                );
        List<Igr> igrs = rawInfluxClient.getQueryApi().query(query.toString(), Igr.class);
        return igrs.get(0);
    }
}
