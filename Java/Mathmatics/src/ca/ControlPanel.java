package ca;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Array;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * 操作パネル
 * @author MasatoKondo
 */
public class ControlPanel extends JPanel implements ActionListener{
   private JComboBox<Integer> selectRule;
   private JButton startButton;
   private MainPanel panel;
   int count = 0;
   
   public ControlPanel(MainPanel panel){
       this.panel = panel;
       
       //ボタンの設定
       startButton = new JButton("START");
       startButton.addActionListener(this);
       this.add(startButton);
       
       //セレクトボックスの設定
       Integer[] rule = new Integer[256];
       for(int i=0; i<rule.length; i++){
           rule[i] = i;
       }
       selectRule = new JComboBox(rule);
       selectRule.setSelectedIndex(90);
       selectRule.addActionListener(this);
       selectRule.setEnabled(false);
       this.add(selectRule);
       
       //ボーダーラインの設定
       BevelBorder border = new BevelBorder(BevelBorder.RAISED);
       this.setBorder(border);
   }

   /**
    * イベント処理
    * @param e 
    */
    @Override
    public void actionPerformed(ActionEvent e) {
        
        //スタートボタンが押された時の処理
        if(e.getSource()==startButton){
            startButton.setEnabled(false);
            selectRule.setEnabled(true);
            panel.start();
        }
        
        //コンボボックスを選択した時の処理
        if(e.getSource()==selectRule){
            if(panel.getCount()==MainPanel.MAG-1){//描画が完了したら、ボタンを押下可能にし、ルールに従って再描画
                panel.reset(selectRule.getSelectedIndex());
            }
        }
    }
}
