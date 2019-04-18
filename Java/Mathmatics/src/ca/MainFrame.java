package ca;

import java.awt.BorderLayout;
import javax.swing.JFrame;

/**
 *　画面表示
 * @author MasatoKondo
 */
public class MainFrame extends JFrame{
    
    public MainFrame(){
        setTitle("CellAutomaton");  //ウィンドウに表示するタイトルを設定
        setResizable(false);        //サイズ変更不可
        
        MainPanel panel = new MainPanel(90);    //描画画面を設置（ルールの初期値は90）
        this.add(panel, BorderLayout.CENTER);
        
        ControlPanel ctrPanel = new ControlPanel(panel);    //操作パネルを設置
        this.add(ctrPanel, BorderLayout.SOUTH);
        pack();
    }
    
    public static void main(String args[]){
        MainFrame frame = new MainFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
