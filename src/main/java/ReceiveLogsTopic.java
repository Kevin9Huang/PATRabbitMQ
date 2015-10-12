/**
 * Created by Kevin on 10/12/2015.
 */
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsTopic implements Runnable {
    private static final String EXCHANGE_NAME = "topic_logs";
    private ConnectionFactory factory;
    private Connection connection;
    private Channel channel;
    private List<String> channels;
    private String queueName;

    public ReceiveLogsTopic() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        channels = new ArrayList<String>();
        queueName = channel.queueDeclare().getQueue();
    }

    @Override
    public void run() {
        if(!channels.isEmpty()) {
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
                for(String channelName : channels) {
                    channel.basicConsume(channelName, true, consumer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
       }
    }

    public void addChannel(String channelName) throws IOException {
        channels.add(queueName);
        channel.queueBind(queueName,EXCHANGE_NAME,channelName);
    }

    public void leaveChannel(String channelName) throws IOException {
        channels.remove(queueName);
        channel.queueUnbind(queueName,EXCHANGE_NAME,channelName);
    }
}