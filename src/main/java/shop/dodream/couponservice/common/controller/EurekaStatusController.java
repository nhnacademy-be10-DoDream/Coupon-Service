package shop.dodream.couponservice.common.controller;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/actuator")
public class EurekaStatusController {

    private final Optional<ApplicationInfoManager> aim;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/drain")
    public ResponseEntity<Void> drain() {
        publisher.publishEvent(
                new AvailabilityChangeEvent<>(this, ReadinessState.REFUSING_TRAFFIC)
        );
        aim.ifPresent(applicationInfoManager ->
                applicationInfoManager.setInstanceStatus(InstanceInfo.InstanceStatus.OUT_OF_SERVICE));

        return ResponseEntity.noContent().build();
    }
}

