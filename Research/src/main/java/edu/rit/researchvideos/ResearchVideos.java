package edu.rit.researchvideos;

import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;


import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;


class PlayerPanel extends JPanel {

    private File vlcInstallPath = new File("D:/vlc");
    private EmbeddedMediaPlayer player;
    private boolean isPlaying = false;
    private boolean isFirsTimePlaying = true;
    private String videoLoc;

    public PlayerPanel(String videoLoc) {
        EmbeddedMediaPlayerComponent videoCanvas = new EmbeddedMediaPlayerComponent();
        this.setLayout(new BorderLayout());
        this.add(videoCanvas, BorderLayout.CENTER);
        this.player = videoCanvas.getMediaPlayer();
        this.videoLoc = videoLoc;
        this.setFocusable(true);
        requestFocus();
    }

    public void play() {
        player.prepareMedia(videoLoc);
        player.parseMedia();
        player.play();
        isFirsTimePlaying = false;
        isPlaying = true;
    }
    public boolean getFirstTimePlaying(){
        return isFirsTimePlaying;
    }

    public void prepareMediaButDontPlay(){
        player.prepareMedia(videoLoc);
        player.parseMedia();
    }
    public void playWhilePaused(){
        player.play();
        isPlaying = true;
    }
    public void pause(){
        player.pause();
        isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}

class ResearchVideos extends JFrame {

    public ResearchVideos(PlayerPanel player) {
        this.setTitle("Swing Video Player");
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());
        this.setSize(640, 480);
        this.setLocationRelativeTo(null);
        this.add(player, BorderLayout.CENTER);
        this.validate();
        this.setVisible(true);


    }

    static PlayerPanel[] players = new PlayerPanel[3];

    public static void main(String[] args) throws UnknownHostException {
        InetAddress IP= InetAddress.getLocalHost();
        System.out.println("IP of my system is := "+IP.getHostAddress());
            Thread t = new Thread(new Runnable() {
                public void run() {

                    try {
                        new Server(players).start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (AWTException e) {
                        e.printStackTrace();
                    }
                }
            });
        t.start();
        //NativeLibrary.addSearchPath("libvlc","C:\\Program Files\\VideoLAN\\VLC");



        for(int i = 0; i < 3;i++){
            players[i] = new PlayerPanel("res/video"+String.valueOf(i+1)+".mp4");
            new ResearchVideos(players[i]);

        }
//        for(int i = 0 ; i < 3; i++){
//            players[i].play();
//        }

        JFrame someFrame = new JFrame("Controller");
        someFrame.setSize(new Dimension(400,400));
        someFrame.setVisible(true);
        someFrame.addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {

            }

            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_SPACE){
                    for(PlayerPanel p : players){
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
        });
    }

}
