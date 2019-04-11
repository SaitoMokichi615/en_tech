package gui;


import java.awt.Container;
import javax.swing.JFrame;
import gui.DataViewer;
import gui.PaintPanel;


  
  class Window extends JFrame{
        public Window(String title){
        setTitle(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(640, 480);
        setResizable(false);
        
        Container contentPane = getContentPane();
        PaintPanel vp = new PaintPanel();
        contentPane.add(vp);
        
    }
    
    public static void main(String args[]){
        Window wd= new Window("自由描画");
        wd.setVisible(true);
    }
  }