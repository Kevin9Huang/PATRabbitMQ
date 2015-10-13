/**
 * Created by Kevin on 10/12/2015.
 */
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsTopic implements Runnable {
    private static final String EXCHANGE_NAME = "topic_logs";
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private String queueName;

    public ReceiveLogsTopic() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        queueName = channel.queueDeclare().getQueue();
    }

    public void run() {
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                String output = String.format("[%s]%s",envelope.getRoutingKey(),message);
                System.out.println(output);
            }
        };
        try {
            channel.basicConsume(queueName, true, consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addChannel(String channelName) throws IOException {
        channel.queueBind(queueName,EXCHANGE_NAME,channelName);
    }

    public void leaveChannel(String channelName) throws IOException {
        channel.queueUnbind(queueName,EXCHANGE_NAME,channelName);
    }
}