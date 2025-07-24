package shop.dodream.couponservice.common.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import shop.dodream.couponservice.common.properties.CouponRabbitProperties;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReleaseDlqReplayListener {

    private final RabbitTemplate tpl;
    private final CouponRabbitProperties props;

    @RabbitListener(
            queues = "${coupon.rabbit.releaseDlxQueue}",
            containerFactory = "manualAckFactory",
            concurrency = "1")
    public void replay(Message msg, Channel ch) throws IOException {

        long tag = msg.getMessageProperties().getDeliveryTag();
        long deathCnt = getDeathCount(msg.getMessageProperties());

        try {
            if (deathCnt >= 4) {
                tpl.send(props.getParkingExchange(),
                        props.getParkingRoutingKey(),
                        msg);
                log.warn("[Parking] release-DLQ message moved after {} failures: {}", deathCnt, msg);
            } else {
                tpl.send("",
                        props.getReleaseQueue(),
                        msg);
                log.info("[Replay] redelivered (x-death={}): {}", deathCnt, msg);
            }
            ch.basicAck(tag, false);
        } catch (Exception ex) {
            log.error("Release DLQ replay failed: {}", ex.getMessage(), ex);
            ch.basicNack(tag, false, true);
        }
    }

    private long getDeathCount(MessageProperties props) {
        return props.getXDeathHeader() == null ? 0L
                : props.getXDeathHeader().stream()
                .findFirst()
                .map(d -> (Long) d.get("count"))
                .orElse(0L);
    }
}

