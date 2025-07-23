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
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class DlqReplayListener {

    private final RabbitTemplate tpl;
    private final CouponRabbitProperties prop;

    @RabbitListener(
            queues = "${coupon.rabbit.dlxQueue}",
            containerFactory = "manualAckFactory",
            concurrency = "1")
    public void replay(Message msg, Channel ch) throws IOException {

        long tag = msg.getMessageProperties().getDeliveryTag();
        long deathCnt = getDeathCount(msg.getMessageProperties());

        try {
            if (deathCnt >= 4) {
                tpl.send(prop.getParkingExchange(),
                        prop.getParkingRoutingKey(),
                        msg);
                log.warn("[Parking] message moved after {} failures: {}", deathCnt, msg);
            } else {
                tpl.send(prop.getExchange(),
                        prop.getRoutingKey(),
                        msg);
                log.info("[Replay] redelivered (x-death={}): {}", deathCnt, msg);
            }
            ch.basicAck(tag, false);
        } catch (Exception ex) {
            log.error("DLQ replay failed: {}", ex.getMessage(), ex);
            ch.basicNack(tag, false, true);
        }
    }

    private long getDeathCount(MessageProperties props) {
        List<Map<String, ?>> deaths = props.getXDeathHeader();
        if (deaths == null || deaths.isEmpty()) {
            return 0;
        }
        long deathCnt = props.getXDeathHeader().stream().findFirst().map(d -> (Long)d.get("count"))
                .orElse(0L);
        return deathCnt;
    }
}
