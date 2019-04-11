package gui;

import comonent.Mnist;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *データを表示する
 * @author MasatoKondo
 */
public class DataViewer extends JFrame{
    Mnist mnist;
    
    /**
     * ウィンドウの初期化処理
     * @param title 
     */
    public DataViewer(String title){
        mnist = new Mnist("TRAIN", 0);
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        Container contentPane = getContentPane();
        //ViewerPanel vp = new ViewerPanel();
        //contentPane.add(vp);
        
        HandWritingPanel hwp = new HandWritingPanel();
        contentPane.add(hwp);
        //JButton btn = new JButton("button");
        //contentPane.add(btn);
        
        pack();
    }
    
    public static void main(String args[]){
        DataViewer dv = new DataViewer("MNISTを表示する");
        dv.setVisible(true);
    }
    
    /**
     * 手書き数字を入力するインタフェース
     */
    class HandWritingPanel extends JPanel implements MouseMotionListener{
        
        int x = -10;
        int y = -10;
        
        public HandWritingPanel(){
            setPreferredSize(new Dimension(Mnist.SIZE_IMAGE*10, Mnist.SIZE_IMAGE*10));
            setFocusable(true);
            //addMouseListener(this);
            addMouseMotionListener(this);
        }
        
        /**
         * 画面描画処理
         * @param g 
         */
        @Override
        public void paintComponent(Graphics g){
            g.setColor(Color.BLACK);
            g.fillOval(x, y, 10, 10);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            x = e.getX();
            y = e.getY();
            repaint();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    /**
     * 描画画面
     */
    class ViewerPanel extends JPanel{  
        private static final int WIDTH = Mnist.SIZE_IMAGE*20;
        private static final int HEIGHT = Mnist.SIZE_IMAGE*10;
        BufferedImage[] img;
        String[] label;
        
        /**
         * 表示データ（手書き数字画像、正解ラベル）を配列に格納
         */
        public ViewerPanel(){
            setPreferredSize(new Dimension(WIDTH, HEIGHT));
            setFocusable(true);
            img = new BufferedImage[Mnist.BATCH_SIZE];
            label = new String[Mnist.BATCH_SIZE];
            
            for(int n=0; n<Mnist.BATCH_SIZE; n++){
                img[n] = mnist.getImageData(n);
                label[n] = Integer.toString(mnist.getLabelsData(n));
            }
        }
        
        
        @Override
        /**
         * 手書き数字画像と正解ラベルを描画
         */
        public void paint(Graphics g){
            for(int i=0; i<10; i++){
                for(int j=0; j<10; j++){
                    int margin = Mnist.SIZE_IMAGE;
                    g.drawImage(img[10*i+j], j*margin, i*margin, null);
                    g.drawRect(margin*(10+j), i*margin, margin, margin);
                    g.drawString(label[10*i+j], margin*(10+j)+margin/2, i*margin+margin/2);
                }
            }
        }
    }
    
    
}
