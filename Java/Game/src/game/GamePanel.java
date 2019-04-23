package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements Runnable, KeyListener{
    
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    
    private Player player;
    private Map map;
    /*private boolean leftPressed;
    private boolean rightPressed;
    private boolean jumpPressed;
     */
    private ActionKey goLeftKey;
    private ActionKey goRightKey;
    private ActionKey jumpKey;
    
    private Thread gameLoop;
    
    public GamePanel(){
        goLeftKey = new ActionKey(ActionKey.NORMAL);
        goRightKey = new ActionKey(ActionKey.NORMAL);
        jumpKey = new ActionKey(ActionKey.DETECT_INITIAL_PRESS_ONLY);
        
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setFocusable(true);
        
        map = new Map();
        player = new Player(192, 32, "pl.png", map);
        
        addKeyListener(this);
        
        gameLoop = new Thread(this);
        gameLoop.start();   
    }
    
    @Override
    public void run(){
        while(true){
            if(goLeftKey.isPressed()){
                player.accelerateLeft(goLeftKey.getCount());
            }
            else if(goRightKey.isPressed()){
                player.accelerateRight(goRightKey.getCount());
            }
            else{
                player.stop();
            }
            
            if(jumpKey.isPressed()){
                player.jump();
            }
            player.update();
            
            repaint();
            
            try{
                Thread.sleep(60);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        int offsetX = WIDTH/2-(int)player.getX();
        
        if(player.getX()<WIDTH/2){
            offsetX = Math.min(offsetX, 0);
        }
        else if(player.getX()>map.getWidth()-WIDTH/2){
            offsetX = -WIDTH;
        }
        map.draw(g, offsetX, 0);
        player.draw(g, offsetX, 0);
        
        g.setColor(Color.red);
        Font font = new Font("SansSerif", Font.BOLD, 16);
        g.setFont(font);
        g.drawString("("+goLeftKey.getState() +":"+goLeftKey.getCount()+")", 64, 64);
        g.drawString("("+goRightKey.getState() +":"+goRightKey.getCount()+")", 64, 80);
        g.drawString("(vx:"+Math.floor(player.getVx())+")", 64, 96);
        g.drawString("(x:"+Math.floor(player.getX())+")", 64, 112);
        g.drawString("(offsetX:"+Math.floor(offsetX)+")", 64, 128);
        g.drawString("(X:"+(map.getWidth()-WIDTH/2)+")", 64, 128+16);
    }
    
    @Override
    public void keyPressed(KeyEvent e){
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_LEFT){
            goLeftKey.press();
        }
        if(key == KeyEvent.VK_RIGHT){
            goRightKey.press();
        }
        if(key == KeyEvent.VK_SPACE){
            jumpKey.press();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        
        if(key == KeyEvent.VK_LEFT){
            goLeftKey.release();
        }
        if(key == KeyEvent.VK_RIGHT){
            goRightKey.release();
        }
        if(key == KeyEvent.VK_SPACE){
            jumpKey.release();
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) { 
    }
    
}
