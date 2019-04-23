package game;

import java.awt.*;
import javax.swing.*;

public class Main extends JFrame{

    public Main(){
        setTitle("アクションゲーム");
        setResizable(false);   
        
        GamePanel panel = new GamePanel();
        Container contentPane = getContentPane();
        contentPane.add(panel);
        
        pack();
    }
    
    public static void main(String[] args) {
        Main frame = new Main();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
}
