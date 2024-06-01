package live.smoothing.sensordata.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class DateFormatConfiguration {

    @Bean
    public Converter<String, LocalDateTime> localDateTimeConverter() {
        return new LocalDateTimeConverter();
    }

    private static class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

        @Override
        public LocalDateTime convert(@NotNull String source) {
            return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd")).atStartOfDay();
        }
    }
}