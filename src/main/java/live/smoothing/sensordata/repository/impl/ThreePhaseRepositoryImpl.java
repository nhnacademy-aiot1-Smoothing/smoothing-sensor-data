package live.smoothing.sensordata.repository.impl;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.query.dsl.Flux;
import com.influxdb.query.dsl.functions.restriction.Restrictions;
import live.smoothing.sensordata.dto.phase.Phase;
import live.smoothing.sensordata.repository.ThreePhaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.isismtt.x509.Restriction;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static live.smoothing.sensordata.util.FluxUtil.getRawThreePhase;

@Slf4j
@Service
@RequiredArgsConstructor
public class ThreePhaseRepositoryImpl implements ThreePhaseRepository {

    private final InfluxDBClient rawInfluxClient;
    private static final String RAW_BUCKET = "powermetrics_data";

    //Todo: RAW 버킷에서 Three-Phase
    @Override
    public Phase getThreePhase(String[] topics) {

        Flux query =
                getRawThreePhase(
                        RAW_BUCKET,
                        "mqtt_consumer",
                        Instant.now().minus(2, ChronoUnit.DAYS),
                        topics
                );

        List<Phase> phases = rawInfluxClient.getQueryApi().query(query.toString(), Phase.class);
        log.error("size: {}", phases.size());

        if(phases.isEmpty()) {
            return null;
        }
        return phases.get(0);


    }
}