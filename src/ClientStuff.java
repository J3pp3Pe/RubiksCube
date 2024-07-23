import java.io.*;
import java.net.Socket;
import static rendering.Bajs.Move.*;

public class ClientStuff {

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    public ClientStuff(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            sendMessage(this.username);
        } catch (IOException e) {
            closeEverything();
        }
    }

    public void sendMessage(String msg){
        try{
            bufferedWriter.write(msg);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        }   catch (IOException e) {
            closeEverything();
        }
    }
    public void listenForMessages(RubikCube cube){
        new Thread(new Runnable() {
            @Override
            public void run(){
                String messageFromChat;
                try{
                    while(socket.isConnected()){
                        messageFromChat = bufferedReader.readLine();
                        parseMessage(messageFromChat, cube);                          
                    }
                }catch (IOException e) {
                closeEverything();
                }
            }
        }).start();
    }

    private void parseMessage(String message, RubikCube cube){
        switch(message){
            case "R":
                cube.move(R);
                break;
            case "L":
                cube.move(L);
                break;
            case "U":
                cube.move(U);
                break;
            case "D":
                cube.move(D);
                break;
            case "F":
                cube.move(F);
                break;
            case "B":
                cube.move(B);
                break;
            case "R_":
                cube.move(R_);
                break;
            case "L_":
                cube.move(L_);
                break;
            case "U_":
                cube.move(U_);
                break;
            case "D_":
                cube.move(D_);
                break;
            case "F_":
                cube.move(F_);
                break;
            case "B_":
                cube.move(B_);
                break;
        }
    }


    public void closeEverything() {
        try {
            sendMessage("exit");
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    
}
