import javax.swing.JFrame;

import rendering.Bajs.Move;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
public class App extends JFrame implements KeyListener, WindowListener{

    private RubikCube cube;
    private ClientStuff client;
    protected JFrame frame = new JFrame();

    public App() throws IOException, InterruptedException{
        cube = new RubikCube(3, 1000, 1000);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your username: ");
        String username = scanner.nextLine();
        scanner.close();
		initApp(username);

        Socket socket = new Socket("localhost", 1234);
        client = new ClientStuff(socket, username);
        client.listenForMessages(cube);
        update();
    }
    private void update() throws InterruptedException{
        new Thread(new Runnable() {
            @Override
            public void run(){
                try{
                    long lastTime = 0;
                    long time = System.currentTimeMillis();
                    while(true) {
                        lastTime = time;
                        time = System.currentTimeMillis();
                        float dt = (time - lastTime) / 1000.0f;
                        cube.renderer.update(dt);
                        TimeUnit.MILLISECONDS.sleep(50);
                    }
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
    
    private void initApp(String username){
        setContentPane(cube.renderer);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(500,500);
		setVisible(true);
        setTitle(username);
        addKeyListener(this);
        cube.renderer.addKeyListener(this);
        
    }
    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        Move move = null;
        switch(e.getKeyCode()) {
			case KeyEvent.VK_J:
				move = Move.U;
				break;
			case KeyEvent.VK_K:
				move = Move.R_;
				break;
			case KeyEvent.VK_L:
				move = Move.D_;
				break;
			case KeyEvent.VK_I:
				move = Move.R;
				break;
			case KeyEvent.VK_F:
				move = Move.U_;
				break;
			case KeyEvent.VK_D:
				move = Move.L;
				break;
			case KeyEvent.VK_S:
				move = Move.D;
				break;
			case KeyEvent.VK_E:
				move = Move.L_;
				break;
			case KeyEvent.VK_H:
				move = Move.F;
				break;
			case KeyEvent.VK_G:
				move = Move.F_;
				break;
			case KeyEvent.VK_O:
				move = Move.B_;
				break;
			case KeyEvent.VK_W:
				move = Move.B;
				break;
            }
        if(move != null){
            client.sendMessage(move.toString());
            cube.move(move);
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    public static void main(String[] args) throws Exception {
        new App();
    }
    @Override
    public void windowOpened(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowOpened'");
    }
    @Override
    public void windowClosing(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowClosing'");
    }
    @Override
    public void windowClosed(WindowEvent e) {
        client.closeEverything();
    }
    @Override
    public void windowIconified(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowIconified'");
    }
    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowDeiconified'");
    }
    @Override
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowActivated'");
    }
    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'windowDeactivated'");
    }
}

