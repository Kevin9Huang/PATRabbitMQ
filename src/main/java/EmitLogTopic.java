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
public class EmitLogTopic {

    private static final String EXCHANGE_NAME = "topic_logs";

    private Connection connection;
    private Channel channel;
    private List<String> joinedChannels;

    public EmitLogTopic(ClientRabbit client) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost(client.host);
        factory.setPort(client.port);

        connection = factory.newConnection();
        channel = connection.createChannel();
        channel.exchangeDeclare(EXCHANGE_NAME, "topic");

        joinedChannels = new ArrayList<>();
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
            System.err.println("You must join a channel first");
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
        if (joinedChannels.contains(channelName)) {
            System.err.printf("Already joined channel %s!\n", channelName);
        } else {
            joinedChannels.add(channelName);
            System.out.println("Joined channel " + channelName);
        }
    }

    public void leaveChannel(String channelName){
        if (joinedChannels.contains(channelName)) {
            joinedChannels.remove(channelName);
        } else {
            System.err.printf("You are not a member of channel %s!\n", channelName);
        }
    }
    //...
}