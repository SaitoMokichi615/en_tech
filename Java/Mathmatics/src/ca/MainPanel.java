package ca;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * 描画画面
 * @author MasatoKondo
 */
public class MainPanel extends JPanel implements Runnable{
    public static final int CELL = 4;            //セルの幅
    public static final int MAG = 160;          //拡大率
    public static final int WIDTH = CELL*MAG*2;     //描画画面横幅
    public static final int HEIGHT = CELL*MAG;     //描画画面縦幅
    
    
    private CellAutomaton ca;   //セルオートマトン
    private int count;          //状態更新カウント
    private Thread thread;      //画面描画スレッド
    private int[][] cells;      //時間ごとのセルの状態を格納する二次元配列
    
    /**
     * コンストラクタ
     * @param rule
     */
    public MainPanel(int rule){
        ca = new CellAutomaton(WIDTH/CELL, rule);
        count = MAG-1;
        setPreferredSize(new Dimension(WIDTH, HEIGHT));//画面サイズを決定
        cells = new int[HEIGHT/CELL][WIDTH/CELL];
        cells[0] = ca.getCells();
    }
    
    /**
     * ルールを指定してセルを再初期化
     * @param rule 
     */
    public void reset(int rule){
        ca = new CellAutomaton(WIDTH/CELL, rule);
        cells = new int[HEIGHT/CELL][WIDTH/CELL];
        cells[0] = ca.getCells();
        start();
    }

    /**
     * スレッドの実行開始点（スレッドの処理を記述）
     */
    @Override
    public void run() {
        while(thread != null){//スレッドが起動していれば以下の処理を実行
            ca.updataCell();//セルの状態を更新
            
            if(count<HEIGHT/CELL-1){//画面最下部まで到達していなければ、描画を続ける
                count++;
                repaint();
            }
            else{
                stop();
            }
            
            try{
                Thread.sleep(30);//描画のために一定時間スリープ
            } catch (InterruptedException ex) {
                Logger.getLogger(MainPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * 描画処理
     * @param g 
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        cells[count] = ca.getCells();
        
        for(int i=0; i<cells.length; i++){
            for(int j=0; j<cells[0].length; j++){
                
                if(cells[i][j]==1){
                    g.setColor(Color.red);
                    g.fillRect(CELL*j, CELL*i, CELL, CELL);
                }
                else{
                    g.setColor(Color.LIGHT_GRAY);
                    g.drawRect(CELL*j, CELL*i, CELL, CELL);
                }
            }
        }
    }
    
    /**
     * 描画処理の起動
     */
    public void start(){
        if(thread==null){
            thread = new Thread(this);
            thread.start();
            count = 0;
        }
    }
    
    /**
     * 描画処理の停止
     */
    public void stop(){
        if(thread!=null){
            thread = null;
        }
    }
    
    /**
     * カウンタの値を返す
     * @return 
     */
    public int getCount(){
        return count;
    }
    
    
}
