package game;

import java.awt.*;
import javax.swing.ImageIcon;

public class Map {
    
    private static final int TILE_SIZE = 32;
    public static final double GRAVITY = 1.0;
    
    private Image blockImage;
    
    private int[][] map = {
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,1,1,0,0,0,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,0,0,0,1,1,1,1,1,1,1,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,1,1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,1,1,1,0,0,0,0,1,1,0,1,1,1,0,0,0,0,0,0,0,0,0,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,1,0,0,0,1,1,1,1,0,0,1,1,1,0,0,1,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,1,1,0,0,0,0,0,1,0,0,0,0,0,0,0,0,0,1,1,1,1,0,0,0,0,0,1,1,1,1,1,1,1,1,1,1},
        {1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
        {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,0,0,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1}
    };
    
    private int row;
    private int col;
    private int width;
    public Map(){
        row  = map.length;      //画面の縦幅に入るブロック数
        col = map[0].length;    //画面の横幅に入るブロック数
        width = TILE_SIZE*col;  //ステージの横幅
        loadImage();            //画像データを読み込む
    }
    
    private void loadImage(){
        ImageIcon icon = new ImageIcon(getClass().getResource("block.png"));
        blockImage = icon.getImage();
    }
    
    public void draw(Graphics g, int offsetX, int offsetY){
        int firstTileX = pixelsToTiles(-offsetX);
        int lastTileX = firstTileX + pixelsToTiles(GamePanel.WIDTH)+1;
        g.setColor(Color.ORANGE);
        lastTileX = Math.min(lastTileX, col);
        
        for(int i=0; i<row; i++){
            for(int j=firstTileX; j<lastTileX; j++){
                
                switch(map[i][j]){
                    case 1:
                        //g.fillRect(tilesToPixels(j),tilesToPixels(i), TILE_SIZE, TILE_SIZE);
                        g.drawImage(blockImage, tilesToPixels(j)+offsetX, tilesToPixels(i), 
                                tilesToPixels(j)+TILE_SIZE+offsetX, tilesToPixels(i)+TILE_SIZE,
                                7*TILE_SIZE, 0, 8*TILE_SIZE, TILE_SIZE, null);
                        break;
                }   
            }
        }
    }
    
    public Point getTileCollision(Sprite sprite, double newX, double newY){
        
        newX = Math.ceil(newX);
        newY = Math.ceil(newY);
        
        double fromX = Math.min(sprite.getX(), newX);
        double fromY = Math.min(sprite.getY(), newY);
        double toX = Math.max(sprite.getX(), newX);
        double toY = Math.max(sprite.getY(), newY);
        
        int fromTileX = pixelsToTiles(fromX);
        int fromTileY = pixelsToTiles(fromY);
        int toTileX = pixelsToTiles(toX + sprite.getWidth()-1);
        int toTileY = pixelsToTiles(toY + sprite.getHeight()-1);
        
        for(int x=fromTileX; x<=toTileX; x++){
            for(int y=fromTileY; y<=toTileY; y++){
                
                if(x<0 || x>=col){
                    return new Point(x, y);
                }
                if(y<0 || y>=row){
                    return new Point(x, y);
                }
                if(map[y][x] ==1){
                    return new Point(x, y);
                }
            }
        }
        return null;
    }
    
    public static int tilesToPixels(int tiles){
        return tiles*TILE_SIZE;
    }
    
    public static int pixelsToTiles(double pixels){
        return (int)Math.floor(pixels/TILE_SIZE);
    }
    
    public int getWidth(){
        return width;
    }
}
