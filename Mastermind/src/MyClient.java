import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class MyClient {
    public static void main(String[] args) {
        try {
            Socket socket = new Socket("localhost", 6666);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            Scanner scanner = new Scanner(System.in);

            while (true) {
                try {
                    String input = dis.readUTF();
                    System.out.println(input);
                    if (input.equals("Game Over! Someone has Won!\n")) {
                        break;
                    }
                    String guess = scanner.nextLine();
                    dos.writeUTF(guess);
                    String serverOutput = dis.readUTF();
                    System.out.println(serverOutput);

                    if (serverOutput.equals("You Won")) {
                        break;
                    }

                } catch (SocketException se) {
                    // Handle the socket closed exception
                    System.out.println("Server disconnected. Exiting...");
                    break;
                }
            }
            socket.close();
            dis.close();
            dos.close();
        }
        catch (IOException e) {
            System.out.println("Server disconnected. Exiting...");
        }
    }
}
