package live.smoothing.sensordata.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import live.smoothing.sensordata.entity.Point;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

class Info {
    Instant start = Instant.MAX;
    Instant end = Instant.MIN;
    List<String> notCachedTopics = new ArrayList<>();
}

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class CacheAspect {

    static class Offset {
        ChronoUnit unit;
        long offset;
    }

    private final RedisTemplate<String, String > redisTemplate;
    private final ObjectMapper objectMapper;
    private static final Map<String, Offset> OFFSET_MAP = new HashMap<>();

    static {
        OFFSET_MAP.put("kwh_hour", new Offset() {{
            unit = ChronoUnit.HOURS;
            offset = 1;
        }});

        OFFSET_MAP.put("kwh_daily4", new Offset() {{
            unit = ChronoUnit.DAYS;
            offset = 1;
        }});
    }

    @Pointcut("@annotation(live.smoothing.sensordata.annotation.StaticPeriodCacheable)")
    public void staticCacheable() {}

    @Pointcut("@annotation(live.smoothing.sensordata.annotation.DynamicAggCacheable)")
    public void dynamicAggCacheable() {}

    @Pointcut("@annotation(live.smoothing.sensordata.annotation.DynamicRawCacheable)")
    public void dynamicRawCacheable() {}


    @SuppressWarnings("unchecked")
    @Around(value = "staticCacheable() && args(bucket, measurement, start, end, topics)", argNames = "joinPoint,bucket,measurement,start,end,topics")
    public Object staticCache(ProceedingJoinPoint joinPoint, String bucket, String measurement, Instant start, Instant end, String[] topics) throws Throwable {

        List<Point> result = new ArrayList<>();
        List<List<Point>> inMemoryData = getInMemoryDataFromOrderSet(bucket, measurement, start, end, topics);
        Info notCachedInfo = getNotCachedInfo(topics, inMemoryData, start, end, measurement);

        if (!inMemoryData.isEmpty()) {
            for (List<Point> inMemoryDatum : inMemoryData) {
                result.addAll(inMemoryDatum);
            }
        }

        if (!notCachedInfo.notCachedTopics.isEmpty()) {
            List<Point> proceedResult = (List<Point>) joinPoint.proceed(new Object[]{bucket, measurement, notCachedInfo.start, notCachedInfo.end.plus(30, ChronoUnit.MINUTES), getTopics(notCachedInfo.notCachedTopics)});
            result.addAll(proceedResult);
            saveOrderSet(bucket, measurement, proceedResult);
        }

        result.forEach(point -> point.setTime(point.getTime().plus(9, ChronoUnit.HOURS)));

        return result;
    }

    @SuppressWarnings("unchecked")
    @Around(value = "dynamicAggCacheable() && args(bucket, measurement, start, topics)", argNames = "joinPoint,bucket,measurement,start,topics")
    public Object dynamicAggCache(ProceedingJoinPoint joinPoint, String bucket, String measurement, Instant start, String[] topics) throws Throwable {
        List<String> unCacheTopics = new ArrayList<>();
        List<Point> result = new ArrayList<>();
        List<List<Point>> points = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String topic : topics) {
                connection.get((bucket + ":" + measurement + ":" + topic + ":" + start.toEpochMilli()).getBytes());
            }
            return null;
        }).stream().map(data -> {
            try {
                if (data != null) {
                    return objectMapper.readValue((String) data, objectMapper.getTypeFactory().constructCollectionType(List.class, Point.class));
                }
            } catch (JsonProcessingException e) {
                log.error("Json 변환 실패 : {}", e.getMessage());
            }
            return new ArrayList<Point>();
        }).collect(Collectors.toList());

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).isEmpty()) unCacheTopics.add(topics[i]);
            result.addAll(points.get(i));
        }

        if (!unCacheTopics.isEmpty()) {
            List<Point> proceedResult = (List<Point>) joinPoint.proceed(new Object[]{bucket, measurement, start, getTopics(unCacheTopics)});
            result.addAll(proceedResult);

            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (Point point : proceedResult) {
                    try {
                        connection.setEx((bucket + ":" + measurement + ":" + point.getTopic() + ":" + start.toEpochMilli()).getBytes(), 300, objectMapper.writeValueAsString(proceedResult).getBytes());
                    } catch (JsonProcessingException e) {
                        log.error("Json 변환 실패 : {}", e.getMessage());
                    }
                }
                return null;
            });
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Around(value = "dynamicRawCacheable() && args(bucket, measurement, start, topics)", argNames = "joinPoint,bucket,measurement,start,topics")
    public Object dynamicRawCache(ProceedingJoinPoint joinPoint, String bucket, String measurement, Instant start, String[] topics) throws Throwable {
        List<String> unCacheTopics = new ArrayList<>();
        List<Point> result = new ArrayList<>();
        List<List<Point>> points = redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String topic : topics) {
                connection.get((topic + ":" + start.toEpochMilli()).getBytes());
            }
            return null;
        }).stream().map(data -> {
            try {
                if (data != null) {
                    return objectMapper.readValue((String) data, objectMapper.getTypeFactory().constructCollectionType(List.class, Point.class));
                }
            } catch (JsonProcessingException e) {
                log.error("Json 변환 실패 : {}", e.getMessage());
            }
            return new ArrayList<Point>();
        }).collect(Collectors.toList());

        for (int i = 0; i < points.size(); i++) {
            if (points.get(i).isEmpty()) unCacheTopics.add(topics[i]);
            result.addAll(points.get(i));
        }

        if (!unCacheTopics.isEmpty()) {
            List<Point> proceedResult = (List<Point>) joinPoint.proceed(new Object[]{bucket, measurement, start, getTopics(unCacheTopics)});
            result.addAll(proceedResult);

            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (Point point : proceedResult) {
                    try {
                        connection.setEx((point.getTopic() + ":" + start.toEpochMilli()).getBytes(), 30, objectMapper.writeValueAsString(proceedResult).getBytes());
                    } catch (JsonProcessingException e) {
                        log.error("Json 변환 실패 : {}", e.getMessage());
                    }
                }
                return null;
            });
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private List<List<Point>> getInMemoryDataFromOrderSet(String bucket, String measurement, Instant start, Instant end, String[] topics) {
        return redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (String topic : topics) {
                connection.zSetCommands().zRangeByScore((bucket + ":" + measurement + ":" + topic).getBytes(), start.toEpochMilli(), end.toEpochMilli());
            }
            return null;
        }).stream().map(data -> {
            if (data != null) {
                Set<String> stringSet = (Set<String>) data;
                return stringSet.stream().map(json -> {
                    try {
                        return objectMapper.readValue(json, Point.class);
                    } catch (IOException e) {
                        return null;
                    }
                }).collect(Collectors.toList());
            }
            return new ArrayList<Point>();
        }).collect(Collectors.toList());
    }

    private void saveOrderSet(String bucket, String measurement, List<Point> points) {
        redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
            for (Point point : points) {
                try {
                    point.setTime(point.getTime().minus(9, ChronoUnit.HOURS));
                    connection.zSetCommands().zAdd((bucket + ":" + measurement + ":" + point.getTopic()).getBytes(), point.getTime().toEpochMilli(), objectMapper.writeValueAsString(point).getBytes());
                } catch (JsonProcessingException e) {
                    log.error("Json 변환 실패 : {}", e.getMessage());
                }
            }
            return null;
        });

    }

    private String[] getTopics(List<String> topics) {
        return topics.toArray(String[]::new);
    }

    private Info getNotCachedInfo(
            String[] topics,
            List<List<Point>> points,
            Instant start,
            Instant end,
            String measurement
    ) {

        Info notCachedInfo = new Info();
        for (int i = 0; i < points.size(); i++) {
            List<Point> pointList = points.get(i);

            if (pointList.isEmpty()) {
                notCachedInfo.start = start;
                notCachedInfo.end = end;
                notCachedInfo.notCachedTopics.add(topics[i]);
                continue;
            }

            Instant cacheStart = getStartTime(start, end, pointList, measurement);
            Instant cacheEnd = getEndTime(start, end, pointList, measurement);

            if (cacheStart.equals(Instant.MAX) || cacheEnd.equals(Instant.MIN)) {
                continue;
            }

            notCachedInfo.notCachedTopics.add(topics[i]);
            notCachedInfo.start = cacheStart.isAfter(notCachedInfo.start) ? notCachedInfo.start : cacheStart;
            notCachedInfo.end = cacheEnd.isBefore(notCachedInfo.end) ? notCachedInfo.end : cacheEnd;
        }

        return notCachedInfo;
    }

    private Instant getStartTime(Instant start,
                                 Instant end,
                                 List<Point> points,
                                 String measurement
    ) {

        int index = 0;
        Instant time = start;
        Instant currentTime = null;

        while (!time.isAfter(end)) {
            if (index >= points.size()) {
                return time;
            }

            Instant pointTime = points.get(index).getTime();
            if (Objects.nonNull(currentTime) && currentTime.equals(pointTime)) {
                index++;
                continue;
            }

            if (!time.equals(pointTime)) {
                return time;
            }

            currentTime = pointTime;
            index++;
            time = time.plus(
                    OFFSET_MAP.get(measurement).offset,
                    OFFSET_MAP.get(measurement).unit
            );
        }

        return Instant.MAX;
    }

    private Instant getEndTime(Instant start,
                                 Instant end,
                                 List<Point> points,
                                 String measurement
    ) {

        int index = points.size()-1;
        Instant time = end;
        Instant currentTime = null;

        while (!time.isBefore(start)) {
            if (index < 0) {
                return time;
            }

            Instant pointTIme = points.get(index).getTime();
            if (Objects.nonNull(currentTime) && currentTime.equals(pointTIme)) {
                index--;
                continue;
            }

            if (!time.equals(pointTIme)) {
                return time;
            }

            currentTime = pointTIme;
            index--;
            time = time.minus(
                    OFFSET_MAP.get(measurement).offset,
                    OFFSET_MAP.get(measurement).unit
            );
        }

        return Instant.MIN;
    }
}
