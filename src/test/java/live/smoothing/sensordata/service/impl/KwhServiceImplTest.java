package live.smoothing.sensordata.service.impl;

import live.smoothing.sensordata.config.InfluxDBConfig;
import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.dto.SensorPowerMetric;
import live.smoothing.sensordata.dto.kwh.KwhTimeZoneResponse;
import live.smoothing.sensordata.dto.topic.SensorTopicResponse;
import live.smoothing.sensordata.dto.topic.SensorWithTopic;
import live.smoothing.sensordata.entity.Kwh;
import live.smoothing.sensordata.repository.impl.KwhRepositoryImpl;
import live.smoothing.sensordata.util.TimeProvider;
import live.smoothing.sensordata.util.TimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

class CustomTimeProvider implements TimeProvider {
    @Override
    public LocalDateTime now() {
        return LocalDateTime.of(2024, 5, 2, 5, 0);
    }

    @Override
    public Instant nowInstant() {
        return now().toInstant(ZoneOffset.of("+09:00"));
    }
}

@SpringBootTest
class KwhServiceImplTest {

}