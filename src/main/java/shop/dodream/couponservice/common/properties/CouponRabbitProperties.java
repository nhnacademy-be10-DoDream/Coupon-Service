package shop.dodream.couponservice.common.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "coupon.rabbit")
@Getter
@Setter
public class CouponRabbitProperties {
    private String exchange;
    private String queue;
    private String routingKey;

    private String dlxExchange;
    private String dlxQueue;
    private String dlxRoutingKey;

    private String parkingExchange;
    private String parkingQueue;
    private String parkingRoutingKey;
}
