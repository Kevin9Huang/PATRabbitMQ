import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * Created by Kevin on 10/12/2015.
 */
public class ClientRabbit {
    private final EmitLogTopic emitLogTopic;
    private final ReceiveLogsTopic receiveLogsTopic;
    private int RandomNameLength = 10;
    private String name;

    public final String host;
    public final int port;

    private List<String> messages;

    public void pushMessage(String message) {
        messages.add(message);
    }

    public ClientRabbit(String host, int port) throws IOException, TimeoutException {
        messages = new ArrayList<>();

        this.name = createRandomName(RandomNameLength);
        this.host = host;
        this.port = port;

        emitLogTopic = new EmitLogTopic(this);
        receiveLogsTopic = new ReceiveLogsTopic(this);

        //JoinChannel("LogChannel");
        new Thread(receiveLogsTopic).run();
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        String host = "localhost";
        int port = 5672;

        if (args.length > 0) {
            host = args[0];

            if (args.length > 1) {
                port = Integer.parseInt(args[1]);
            }
        }
        System.out.printf("Connecting to RabbitMQ server at %s:%d\n", host, port);

        ClientRabbit client = new ClientRabbit(host, port);

        //client.leaveChannel("LogChannel");
        Scanner sc = new Scanner(System.in);
        System.out.println("Client " + client.name + " created successfully");
        String input;
        do {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                //
            }
            if (!client.messages.isEmpty()) {
                client.Show();
            }

            System.out.printf("[%s] > ", client.name);
            input = sc.nextLine();
            client.ProcessInput(input);
        } while (!input.toLowerCase().startsWith("/exit"));
    }

    private void JoinChannel(String channelName) throws IOException {
        receiveLogsTopic.addChannel(channelName);
        emitLogTopic.addChannel(channelName);
    }

    private void leaveChannel(String channelName) throws IOException {
        receiveLogsTopic.leaveChannel(channelName);
        emitLogTopic.leaveChannel(channelName);
    }

    private void sendMessage(String message){
        emitLogTopic.sendMessage(message);
    }

    private String createRandomName(int length){
        StringBuilder buffer = new StringBuilder();
        String characters;
        characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";

        int charactersLength = characters.length();

        for (int i = 0; i < length; i++) {
            double index = Math.random() * charactersLength;
            buffer.append(characters.charAt((int) index));
        }
        return buffer.toString();
    }
    
    private void setNick(String WantedName){
        name = WantedName;
    }

    private void Exit() throws IOException {
        emitLogTopic.closeConnection();
        System.exit(0);
    }

    private void Say(String message){
        String outmessage = String.format("(%s)%s",name,message);

        emitLogTopic.sendMessage(outmessage);
    }

    private void Say(String message,String channelName){
        String outmessage = String.format("(%s)%s", name, message);

        emitLogTopic.sendMessageToChannel(outmessage,channelName);
    }

    private void Show() {
        if (messages.isEmpty()) {
            System.out.println("No new messages");
        } else {
            for (String message: messages) {
                System.out.println(message);
            }
            messages.clear();
        }
    }

    public void ProcessInput(String input) throws IOException {
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
            } else if (command.equalsIgnoreCase("show")) {
                Show();
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
