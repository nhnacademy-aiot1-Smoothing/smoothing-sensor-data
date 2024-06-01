package live.smoothing.sensordata.adapter;

import live.smoothing.sensordata.dto.topic.SensorTopicResponse;
import live.smoothing.sensordata.dto.topic.TopicResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("device-service")
public interface TopicAdapter {

    @GetMapping("/api/device/topics")
    TopicResponse getTopicWithTags(@RequestParam("tags") String tags,
                                   @RequestParam("type") String type,
                                   @RequestParam("userId") String userId);

    @GetMapping("/api/device/topics/all")
    TopicResponse getTopicAll(@RequestParam("type") String type);

    @GetMapping("/api/device/topics/sensors")
    SensorTopicResponse getSensorWithTopics(@RequestParam("tags") String tags,
                                            @RequestParam("type") String type,
                                            @RequestParam("userId") String userId);

    @GetMapping("/api/device/topics/sensors/all")
    SensorTopicResponse getSensorWithTopicAll(@RequestParam("type") String type);
}
