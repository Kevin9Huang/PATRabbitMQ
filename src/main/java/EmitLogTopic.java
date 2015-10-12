import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * Created by Kevin on 10/12/2015.
 */
public class EmitLogTopic{

    private static final String EXCHANGE_NAME = "topic_logs";
    private ConnectionFactory factory;
    private Connection connection;
    private static Channel channel;
    private static List<String> joinedChannels;

    public EmitLogTopic() throws IOException, TimeoutException {
        factory = new ConnectionFactory();
        factory.setHost("localhost");
        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");
        joinedChannels = new ArrayList<String>();
    }
    public void sendMessage(String message){
        if(!joinedChannels.isEmpty()) {
            try {
                for (String joinedChannel : joinedChannels) {
                    channel.basicPublish(EXCHANGE_NAME, joinedChannel, null, message.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("You must join a channel first");
        }
    }

    public void sendMessageToChannel(String message,String channelName){
        try {
            channel.basicPublish(EXCHANGE_NAME,channelName, null, message.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection() throws IOException {
        connection.close();
    }

    public void addChannel(String channelName){
        joinedChannels.add(channelName);
    }

    public void leaveChannel(String channelName){
        joinedChannels.remove(channelName);
    }
    //...
}