package shop.dodream.couponservice.common.rabbitmq;


import lombok.RequiredArgsConstructor;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.ImmediateAcknowledgeAmqpException;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.util.ErrorHandler;

@RequiredArgsConstructor
public class CustomErrorHandler implements ErrorHandler {

    private final FatalExceptionStrategy exceptionStrategy;

    @Override
    public void handleError(Throwable t) {
        if (this.exceptionStrategy.isFatal(t) && t instanceof ListenerExecutionFailedException) {
            throw new ImmediateAcknowledgeAmqpException(
                    "Fatal exception encountered. Retry is futile: " + t.getMessage(), t);
        }

        throw new AmqpRejectAndDontRequeueException(
                "Retryable exception encountered. Moving to DLX for retries: " + t.getMessage(), t);
    }
}
