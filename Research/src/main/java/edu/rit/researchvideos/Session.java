
package edu.rit.researchvideos;
import java.awt.AWTException;
import java.awt.Robot;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class Session implements Runnable {
	
	Robot robot;
	
	Socket socket;
	DataInputStream socketReader;

    private final int PLAY_PAUSE_VIDEOS = 32;

	boolean isRunning;
    private PlayerPanel[] playerPanels;
	
	public Session(Socket socket,PlayerPanel[] playerPanels) throws IOException, AWTException {
        this.playerPanels = playerPanels;
		this.socket = socket;
		this.socketReader = new DataInputStream(socket.getInputStream());
		this.robot = new Robot();
	}

	public void run() {
		System.out.println("Client connected!");
		isRunning = true;
		int button;
		boolean isDown;

		while (isRunning) {
			try {
				button = socketReader.readInt();
				isDown = socketReader.readBoolean();

				if (button == -1) {
					isRunning = false;
				} else {

					pressKey(32, isDown);
				}

			} catch (IOException e) {
				e.printStackTrace();
				isRunning = false;
			}
		}
	}


	
	public void pressKey(int i, boolean b){
        if(playerPanels == null){
            return;
        }
        for(PlayerPanel p : playerPanels){
            if(p != null){
                if(p.isPlaying()){
                    p.pause();
                }
                else{
					if(p.getFirstTimePlaying()){
						p.play();
					}
					else{
						p.playWhilePaused();
					}
                }
            }
        }

	}

}