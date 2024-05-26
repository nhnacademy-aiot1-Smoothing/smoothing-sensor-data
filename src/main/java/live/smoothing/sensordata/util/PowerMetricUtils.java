package live.smoothing.sensordata.util;

import live.smoothing.sensordata.dto.PowerMetric;
import live.smoothing.sensordata.entity.Point;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class PowerMetricUtils {

    private PowerMetricUtils() {}

    public static List<Point> getDeduplicationList(List<Point> timeValueList) {
        Map<String, Point> uniqueValues = timeValueList.stream()
                .collect(Collectors.toMap(
                        value -> value.getTime().toString() + value.getTopic(),
                        value -> value,
                        (existing, replacement) -> existing.getValue() > replacement.getValue() ? existing : replacement
                ));

        return List.copyOf(uniqueValues.values());
    }

    public static Map<Instant, Double> getSumByTimezone(List<Point> timeValueList) {
        return timeValueList.stream()
                .collect(Collectors.groupingBy(Point::getTime,
                        Collectors.summingDouble(Point::getValue)));
    }

    public static List<Map.Entry<Instant, Double>> getSortedByTimeList(Map<Instant, Double> sumByTimezone) {
        return sumByTimezone.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());
    }

    public static Map<Instant, Double> getFillTimeMap(Map<Instant, Double> sumByTimezone,
                                                             Instant start,
                                                             Instant end,
                                                             ChronoUnit intervalUnit,
                                                             long intervalAmount) {

        Map<Instant, Double> fillTimeMap = new HashMap<>();
        Instant currentTime = start.plus(9, ChronoUnit.HOURS);
        Instant endTime  = end.plus(9, ChronoUnit.HOURS)
                .plus(intervalAmount, intervalUnit);

        double currentValue = 0.0;

        while (!currentTime.equals(endTime)) {
            fillTimeMap.put(currentTime, sumByTimezone.getOrDefault(currentTime, currentValue));
            currentValue = fillTimeMap.get(currentTime);
            currentTime = currentTime.plus(intervalAmount, intervalUnit);
        }

        return fillTimeMap;
    }

    public static List<PowerMetric> getWattPowerMetricsByMap(List<Map.Entry<Instant, Double>> collect, String type, String unit, String per) {
        List<PowerMetric> powerMetrics = new ArrayList<>();

        for (Map.Entry<Instant, Double> entry : collect) {
            powerMetrics.add(
                    new PowerMetric(
                            type,
                            unit,
                            per,
                            entry.getKey(),
                            entry.getValue()
                    )
            );
        }

        return powerMetrics;
    }
}
