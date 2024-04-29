package live.smoothing.sensordata.adapter;

import live.smoothing.sensordata.dto.SensorTopicResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("device-service")
public interface TopicAdapter {

    @GetMapping("/api/device/topics")
    SensorTopicResponse getTopicWithTopics(@RequestParam("tags") String tags);
}
