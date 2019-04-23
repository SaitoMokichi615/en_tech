package game;

import java.awt.*;

public class Player extends Sprite{
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private double vx;
    private double vy;
    private int dir;
    private double speed;
    private double jumpSpeed;
    private boolean onGround;
    private static final double MAX_SPEED = 8.0;
     
    private double lx;
    private double rx;
    public double data1;
    public double data2;
    
    public Player(double x, double y, String fileName, Map map){
        super(x, y, fileName, map);
        
        vx = 0;
        vy = 0;
        lx = x+width/4;
        rx = x+width-width/4;
        speed = 1.0;
        jumpSpeed  = 16.0;
        onGround = false;
        dir = RIGHT;        
    }
    
    /**
     * 停止する
     */
    public void stop(){
        if(dir==LEFT){
            
            if(onGround){vx++;}
            else{vx+=0.2;}
            
            if(vx>0){vx=0;}
        }
        else{
            if(onGround){vx--;}
            else{vx-=0.2;}
            if(vx<0){vx=0;}
        }
        count=0;
    }
    
    /**
     * 左に加速する
     */
    public void accelerateLeft(int amount){
        vx -=speed;
        if(vx<-MAX_SPEED){vx=-MAX_SPEED;}
        dir = LEFT;
        if(amount%6<3){
            count=3;
        }
        else{
            count=4;
        }
    }
    
    /**
     * 右に加速する
     */
    public void accelerateRight(int amount){
        vx += speed;
        if(vx>MAX_SPEED){vx=MAX_SPEED;}
        dir = RIGHT;
        if(amount%6<3){
            count=3;
        }
        else{
            count=4;
        }
        
    }
    
    /**
     * ジャンプする
     */
    public void jump(){
        if(onGround){
            vy = -jumpSpeed;
            onGround = false;
        }
     }
    
    /*@Override
    public int getWidth(){
        return width/2;
    }*/
    
    public double getVx(){
        return vx;
    }
    
    public double getLx(){
        return lx;
    }
    
    /**
     * プレイヤーの状態を更新する
     */
    @Override
    public void update(){
        
        vy += Map.GRAVITY;
        
        //double newX = lx+vx;
        double newX = x+vx;
        Point tile = map.getTileCollision(this, newX, y);
        if(tile==null){
            /*lx =newX;
            rx = lx+width/2;
            x=lx-width/4;*/
            x = newX;
        }
        else{
            if(vx>0){
                //lx = Map.tilesToPixels(tile.x)-width/2+1;
                //if(x>lx-1){x=lx-1;}
                x = Map.tilesToPixels(tile.x)-width;
            }
            else if(vx<0){
                x = Map.tilesToPixels(tile.x+1);
                /*lx = Map.tilesToPixels(tile.x+1);
                if(x <lx-width/4){x = lx-width/4-1;}
                data1 =lx;
                data2 = x;*/
            }
            vx = 0;
        }
        
        double newY = y+vy;
        tile = map.getTileCollision(this, x, newY);
        
        if(tile==null){
            y = newY;
            onGround = false;
        }
        else{
            if(vy>0){
                y = Map.tilesToPixels(tile.y)-height;
                onGround = true;
            }
            else if(vy<0){
                y = Map.tilesToPixels(tile.y +1);
            }
            vy = 0;
        }
        
        if(!onGround){
            if(vy<0){count=1;}
            else{count=2;}
        }
    }
    
    /**
     * プレイヤーを描画
     * @param g 
     */
    @Override
    public void draw(Graphics g, int offsetX, int offsetY){
        int dX1 = (int)x+offsetX;   //スプライト描画位置x座標（左上）
        int dY1 = (int)y+offsetY;   //スプライト描画位置y座標（左上）
        int dX2 = dX1+width;        //スプライト描画位置x座標（右下）
        int dY2 = dY1+height;       //スプライト描画位置y座標（右下）
        
        int sX1 = count*width;      //スプライトに読み込む画像x座標（左上）
        int sY1 = 0;                //スプライトに読み込む画像y座標（左上）
        int sX2 = (count+1)*width;  //スプライトに読み込む画像x座標（右下）
        int sY2 = height;          //スプライトに読み込む画像y座標（右下）
        
        if(dir==RIGHT){
            g.setColor(Color.red);
            g.drawRect(dX1, dY1, width, height);
            g.drawImage(image, dX2, dY1, dX1, dY2, sX1, sY1, sX2, sY2, null);
            //g.drawLine((int)lx, dY1, (int)lx, dY1+height);
            g.setColor(Color.blue);
            //g.drawLine((int)x, dY1, (int)x, dY1+height);
        }
        else{
            g.setColor(Color.red);
            g.drawRect(dX1, dY1, width, height);
            g.drawImage(image, dX1, dY1, dX2, dY2, sX1, sY1, sX2, sY2, null);
            //g.drawLine((int)lx, dY1, (int)lx,dY1+height);
            g.setColor(Color.blue);
            //g.drawLine((int)x, dY1, (int)x, dY1+height);
        }
    }
}
