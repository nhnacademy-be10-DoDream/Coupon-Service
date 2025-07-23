package shop.dodream.couponservice.common.rabbitmq;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.listener.ConditionalRejectingErrorHandler;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.stereotype.Component;
import shop.dodream.couponservice.exception.CouponNotFoundException;
import shop.dodream.couponservice.exception.CouponPolicyNotFoundException;
import shop.dodream.couponservice.exception.UserNotFoundException;


@RequiredArgsConstructor
@Component
public class CustomFatalExceptionStrategy implements FatalExceptionStrategy {

    private final FatalExceptionStrategy fatalExceptionStrategy = new ConditionalRejectingErrorHandler.DefaultExceptionStrategy();

    @Override
    public boolean isFatal(Throwable t) {
        return fatalExceptionStrategy.isFatal(t)
                || t.getCause() instanceof CouponPolicyNotFoundException
                || t.getCause() instanceof CouponNotFoundException
                || t.getCause() instanceof UserNotFoundException;
    }
}
