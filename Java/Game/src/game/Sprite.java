package game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import javax.swing.ImageIcon;

/**
 *
 * @author Masato
 */
public abstract class Sprite {
    protected double x;
    protected double y;
    protected int width;
    protected int height;
    protected Image image;
    protected int count;
    protected Map map;
    
    /**
     * スプライトの初期化
     * @param x
     * @param y
     * @param fileName
     * @param map 
     */
    public Sprite(double x, double y, String fileName, Map map){
        this.x = x;
        this.y = y;
        this.map = map;
        width = 32;
        height = 32;
        
        loadImage(fileName);
        count=0;
    }
    
    /**
     * 画像データをロードする
     * @param fileName 
     */
    public void loadImage(String fileName){
        ImageIcon icon = new ImageIcon(getClass().getResource(fileName));
        image = icon.getImage();
    }
    
    /**
     * スプライトの状態を更新する
     */
    public abstract void update();
    
    /**
     * スプライトを描画する
     * @param g
     * @param offsetX
     * @param offsetY 
     */
    public void draw(Graphics g, int offsetX, int offsetY){
        int dX1 = (int)x+offsetX;   //スプライト描画位置x座標（左上）
        int dY1 = (int)y+offsetY;   //スプライト描画位置y座標（左上）
        int dX2 = dX1+width;        //スプライト描画位置x座標（右下）
        int dY2 = dY1+height;       //スプライト描画位置y座標（右下）
        
        int sX1 = count*width;      //スプライトに読み込む画像x座標（左上）
        int sY1 = 0;                //スプライトに読み込む画像y座標（左上）
        int sX2 = (count+1)*width;  //スプライトに読み込む画像x座標（右下）
        int sY2 = height;          //スプライトに読み込む画像y座標（右下）
        
        //スプライトを描画する
        g.drawImage(image, dX1, dY1, dX2, dY2, sX1, sY1, sX2, sY2, null);
    }
    
    /**
     * スプライトの接触判定
     * @param sprite
     * @return 
     */
    public boolean isCollision(Sprite sprite){
        Rectangle playerRect = new Rectangle((int)x, (int)y, width, height);
        Rectangle spriteRect = new Rectangle((int)sprite.getX(), (int)sprite.getY(),
        sprite.getWidth(), sprite.getHeight());
        
        //自分の矩形と相手の矩形が重なっているか判定
        if(playerRect.intersects(spriteRect)){
            return true;
        }
        return false;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
}