package shop.dodream.couponservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.FatalExceptionStrategy;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ErrorHandler;
import shop.dodream.couponservice.common.properties.CouponRabbitProperties;
import shop.dodream.couponservice.common.rabbitmq.CustomErrorHandler;
import shop.dodream.couponservice.common.rabbitmq.CustomFatalExceptionStrategy;


@RequiredArgsConstructor
@Configuration
public class RabbitMQConfig {

    private final CouponRabbitProperties couponProperties;

    @Bean
    public DirectExchange couponExchange() {
        return new DirectExchange(couponProperties.getExchange(), true, false);
    }

    @Bean
    public DirectExchange couponDlxExchange() {
        return new DirectExchange(couponProperties.getDlxExchange(), true, false);
    }

    @Bean
    public DirectExchange couponParkingExchange() {
        return new DirectExchange(couponProperties.getParkingExchange(), true, false);
    }

    @Bean
    public Queue couponQueue() {
        return QueueBuilder.durable(couponProperties.getQueue())
                .withArgument("x-dead-letter-exchange", couponProperties.getDlxExchange())
                .withArgument("x-dead-letter-routing-key", couponProperties.getDlxRoutingKey())
                .build();
    }

    @Bean
    public Queue couponDlxQueue() {
        return QueueBuilder.durable(couponProperties.getDlxQueue())
                .withArgument("x-dead-letter-exchange", couponProperties.getParkingExchange())
                .withArgument("x-dead-letter-routing-key", couponProperties.getParkingRoutingKey())
                .build();
    }

    @Bean
    public Queue couponParkingQueue() {
        return new Queue(couponProperties.getParkingQueue());
    }


    @Bean
    public Binding couponBinding(Queue couponQueue, DirectExchange couponExchange) {
        return BindingBuilder
                .bind(couponQueue)
                .to(couponExchange)
                .with(couponProperties.getRoutingKey());
    }

    @Bean
    public Binding couponDlxBinding(Queue couponDlxQueue, DirectExchange couponDlxExchange) {
        return BindingBuilder
                .bind(couponDlxQueue)
                .to(couponDlxExchange)
                .with(couponProperties.getDlxRoutingKey());
    }


    @Bean
    public Binding couponParkingBinding(Queue couponParkingQueue, DirectExchange couponParkingExchange) {
        return BindingBuilder
                .bind(couponParkingQueue)
                .to(couponParkingExchange)
                .with(couponProperties.getParkingRoutingKey());
    }





    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setErrorHandler(errorHandler());
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory manualAckFactory(
            ConnectionFactory connectionFactory,
            SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        return factory;
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new CustomErrorHandler(fatalExceptionStrategy());
    }

    @Bean
    FatalExceptionStrategy fatalExceptionStrategy() {
        return new CustomFatalExceptionStrategy();
    }

}
