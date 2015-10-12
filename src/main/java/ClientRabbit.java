import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * Created by Kevin on 10/12/2015.
 */
public class ClientRabbit {
    private static EmitLogTopic emitLogTopic;
    private static ReceiveLogsTopic receiveLogsTopic;
    private int RandomNameLength = 10;
    private static String name;

    public ClientRabbit() throws IOException, TimeoutException {
        emitLogTopic = new EmitLogTopic();
        name = createRandomName(RandomNameLength);
        System.out.println("Name : "+name);
        receiveLogsTopic = new ReceiveLogsTopic();
        JoinChannel("LogChannel");
        new Thread(receiveLogsTopic).run();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
       ClientRabbit client = new ClientRabbit();
        client.leaveChannel("LogChannel");
        Scanner sc = new Scanner(System.in);
        System.out.println("Client "+name+" created successfully");
        String input = "";
        do {
            input = sc.nextLine();
            ProcessInput(input);
        }while(!input.toLowerCase().startsWith("/exit"));
    }

    private static void JoinChannel(String channelName) throws IOException {
        receiveLogsTopic.addChannel(channelName);
        emitLogTopic.addChannel(channelName);
    }

    private static void leaveChannel(String channelName) throws IOException {
        receiveLogsTopic.leaveChannel(channelName);
        emitLogTopic.leaveChannel(channelName);
    }

    private void sendMessage(String message){
        emitLogTopic.sendMessage(message);
    }

    private String createRandomName(int length){
        StringBuffer buffer = new StringBuffer();
        String characters = "";
        characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";


        int charactersLength = characters.length();

        for (int i = 0; i < length; i++) {
            double index = Math.random() * charactersLength;
            buffer.append(characters.charAt((int) index));
        }
        return buffer.toString();
    }
    
    private static void setNick(String WantedName){
        name = WantedName;
    }

    private static void Exit() throws IOException {
        emitLogTopic.closeConnection();
        System.exit(0);
    }

    private static void Say(String message){
        String outmessage = String.format("(%s)%s",name,message);
        emitLogTopic.sendMessage(outmessage);
    }

    private static void Say(String message,String channelName){
        String outmessage = String.format("(%s)%s",name,message);
        emitLogTopic.sendMessageToChannel(outmessage,channelName);
    }

    public static void ProcessInput(String input) throws IOException {
        String command, commandArg = "";

        if (input.startsWith("/")) {
            // parse command
            int spaceIndex = input.indexOf(' ', 1);
            if (spaceIndex != -1) {
                command = input.substring(1, spaceIndex);

                if (input.length() > spaceIndex + 1) {
                    commandArg = input.substring(spaceIndex + 1);
                }
            } else {
                command = input.substring(1);
            }

            if (command.equalsIgnoreCase("nick")) {
                setNick(commandArg);
            } else if (command.equalsIgnoreCase("join")) {
                JoinChannel(commandArg);
            } else if (command.equalsIgnoreCase("leave")) {
                leaveChannel(commandArg);
            } else if (command.equalsIgnoreCase("exit")) {
                Exit();
            }

        } else if (input.startsWith("@")) {
            // send to specific channel
            if (input.contains(" ") && input.charAt(input.length() - 1) != ' ') {
                int spaceIndex = input.indexOf(' ');
                String channel = input.substring(1, spaceIndex);
                String message = input.substring(spaceIndex + 1);
                Say(message,channel);
            }
        } else {
            // send to all subscribed channels
            Say(input);
        }
    }

}
