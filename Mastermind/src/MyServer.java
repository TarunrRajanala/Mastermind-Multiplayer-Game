import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.*;

public class MyServer {
    private static List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());
    private static Game sharedGame;
    static String clientCode = SecretCodeGenerator.getInstance().getNewSecretCode();
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        sharedGame = new Game(new Scanner(System.in), false, new GuessHistory(), GameConfiguration.guessNumber, GameConfiguration.colors, clientCode);

        try {
             serverSocket = new ServerSocket(6666);
            System.out.println("Server is running...");

            while (true) {
                System.out.println("Waiting for connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("Accepted Socket from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();

                startGame(clientHandler);

            }
        } catch (IOException e) {
            System.out.println("Error creating server socket: " + e.getMessage());
        }
        finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing server socket: " + e.getMessage());
            }
        }
    }

    public static void broadcast(String message) throws IOException {
        synchronized (clients) {
            Iterator<ClientHandler> iterator = clients.iterator();
            while (iterator.hasNext()) {
                ClientHandler client = iterator.next();
                if (client.isClosed()) {
                    iterator.remove();
                } else {
                    client.sendMessage(message);
                }
            }
        }
    }

    private static void startGame(ClientHandler clientHandler) {
        Game initialGame = new Game(new Scanner(System.in), false, new GuessHistory(), GameConfiguration.guessNumber, GameConfiguration.colors, clientCode);
        clientHandler.setGame(initialGame);
    }


    static class ClientHandler implements Runnable {
        private DataInputStream dis;
        private DataOutputStream dos;
        private Socket clientSocket;
        private Game clientGame;
        private boolean closed;

         volatile boolean play = true;

        public ClientHandler(Socket clientSocket) {
            try {
                this.clientSocket = clientSocket;
                this.dis = new DataInputStream(clientSocket.getInputStream());
                this.dos = new DataOutputStream(clientSocket.getOutputStream());
                this.clientGame = new Game(new Scanner(System.in), false, new GuessHistory(), GameConfiguration.guessNumber, GameConfiguration.colors, clientCode);
            } catch (Exception e) {
                System.out.println("Error setting up client: " + e.getMessage());
                closed = true;
            }
        }

        public void setGame(Game game) {
            this.clientGame = game;
        }

        public boolean isClosed() {
            return closed;
        }

        @Override
        public void run() {
            try {
                while (play) {
                    int n;
                    for(n=clientGame.guessNum; n>0; n--) {
                        System.out.println("Waiting for client");
                        dos.writeUTF("You have " + n + " number of guesses left" +" \n" + " Enter your guess: ");
                        String clientGuess = dis.readUTF();
                        System.out.println("Client Guess: " + clientGuess);

                        if (!(clientGame.ValidGuess(clientGuess))) {
                            dos.writeUTF("Guess is Invalid");
                            n++;
                        } else if (clientGuess.equalsIgnoreCase("History")) {
                            dos.writeUTF(clientGame.printHistory());     //double check
                            n++;
                        } else {
                            String feedback = clientGame.processGuess(clientGuess);
                            Boolean winner = clientGame.Winner(clientGuess); //check if they won
                            clientGame.addGuessHistory(clientGuess, feedback);
                            dos.writeUTF(feedback);

                            if (winner) {
                                broadcast("Game Over! Someone has Won!");
                                synchronized (clients){
                                    for(ClientHandler client : clients){
                                        if(!client.isClosed()){
                                            client.closeSocket();
                                        }
                                    }
                                }
                            play = false;
                            }
                        }
                    }
                    if(n==0) {
                        dos.writeUTF("Sorry, you are out of guesses");
                        clientSocket.close();
                    }
                    System.out.print("Client Game has Ended");
                    break;
                }
            } catch(SocketException se){
                System.out.println("Client Disconnected");
            }
            catch (Exception e) {
                System.out.println("Error handling client connection: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error closing client socket: " + e.getMessage());
                }
            }
        }

            public void closeSocket() throws IOException {
                clientSocket.close();
            }

            public void sendMessage(String message) throws IOException {
                dos.writeUTF(message);
                dos.flush();
            }
        public boolean endCurrentGame() {
            play = false;
            try {
                broadcast("Game Over! Do you want to play again? (Y/N)");
                dos.flush();
                String response = dis.readUTF();
                return response.equalsIgnoreCase("Y");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void startNewGame() {
            Game newGame = new Game(new Scanner(System.in), false, new GuessHistory(), GameConfiguration.guessNumber, GameConfiguration.colors, clientCode);
            setGame(newGame);
        }



    }
    }

