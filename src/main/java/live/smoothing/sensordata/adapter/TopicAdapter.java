package live.smoothing.sensordata.adapter;

import live.smoothing.sensordata.dto.watt.SensorTopicResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("device-service")
public interface TopicAdapter {

    @GetMapping("/api/device/topics")
    SensorTopicResponse getTopicWithTopics(@RequestParam("tags") String tags,
                                           @RequestParam("type") String type);

    @GetMapping("/api/device/topics/all")
    SensorTopicResponse getTopicAll(@RequestParam("type") String type);
}
