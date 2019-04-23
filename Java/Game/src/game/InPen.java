import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class InPen extends JFrame implements Runnable{

    static double t0;
    
    double time = 0.0;
    
    double r, t, rdot, tdot;
    double rh, th, rdoth, tdoth;
    
    public static void main(String[] args){
        
        InPen wnd=new InPen();
        wnd.addWindowListener(new WindowAdapter(){public void windowClosing(WindowEvent e){System.exit(0);}});
        wnd.setVisible(true);
    }
    
    public InPen(){
        setTitle("Inverted Pendulum");    
        setBounds(300,100,800,700);
        
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.cancelButtonText", "Cancel");
        
        String inputValue;
        inputValue = JOptionPane.showInputDialog(null, "Please input a value of t0 (degree)", "Input t0", JOptionPane.PLAIN_MESSAGE);

        t0 = Double.parseDouble(inputValue);
        t0 = degreetoradian(t0);
        
        Thread thr = null;
        if(thr == null){
            thr = new Thread(this);
            thr.start();
        }
    }
    
    public void run(){
       double r0 = 0.0, rr0 = 0.00, tt0 = 0.00;
        r = r0; t = t0; rdot = rr0; tdot = tt0;
        
        try{
            Thread.sleep(1000);
        }catch(Exception e){}
        
        while(true){
        
        move();
        
        if(time > 5){
        System.exit(0);
        }
        try{
            repaint();
            Thread.sleep(30);
        }catch(Exception e){}
        time = time + 0.001;
      }
    }
    
    public void move(){
    
        double step = 0.0001;
        
        for(double ti = 0; ti < 0.01; ti = ti + step){

       rh   = r    + step * dadt(r, t, rdot, tdot);
       th   = t    + step * dbdt(r, t, rdot, tdot);
       rdoth = rdot + step * dcdt(r, t, rdot, tdot);
       tdoth = tdot + step * dddt(r, t, rdot, tdot);
        
        r = rh; t = th; rdot = rdoth; tdot = tdoth;
        }
    }
    
    public void update(Graphics g){
        paint(g);
    }
    
    public void paint(Graphics g){
        super.paint(g);
        int s = getSize().width/2,a = 1000;
        
         g.drawLine((int)(s+r*a-20)-100,375,(int)(s+r*a+20)+100,375); //台車上辺
        g.drawLine((int)(s+r*a+20)+100,375,(int)(s+r*a+20)+100,425); //台車右辺
        g.drawLine((int)(s+r*a-20)-100,425,(int)(s+r*a+20)+100,425); //台車下辺
        g.drawLine((int)(s+r*a-20)-100,375,(int)(s+r*a-20)-100,425); //台車左辺
        g.drawLine(0,450,s*2,450);
        g.drawLine(s,450,s,455);
        g.fillOval((int)(s+r*a-15)-65,400,50,50);
        g.fillOval((int)(s+r*a+5)+25,400,50,50);
        g.drawLine((int)(s+r*a),375,(int)(s+r*a+105.0f*Math.sin(t)),(int)(375-110.0f*Math.cos(t))); //振子の棒
        g.fillOval((int)(s+r*a+110.0f*Math.sin(t))-5,(int)(370-115.0f*Math.cos(t)),10,10); //振子の球
    }
    
    
    double dadt(double r, double t, double rdot, double tdot){
       double m = 0.1, M = 5.01, l = 0.115, R = 24.5;
        double drdot, dtdot;
        drdot = dcdt(r, t, rdot, tdot);
        dtdot = dddt(r, t, rdot, tdot);
    
        return (1 / R) * (m * l * Math.sin(t) * tdot * tdot - (M + m) * drdot - m * l * Math.cos(t) * dtdot);
    }
    
    double dbdt(double r, double t, double rdot, double tdot){
       double m = 0.1, J = 0.00214, l = 0.115, g = 9.8, c = 0.000598;
        double drdot, dtdot;
        drdot = dcdt(r, t, rdot, tdot);
        dtdot = dddt(r, t, rdot, tdot);
    
        return (1 / c) * (-m * l * Math.cos(t) * drdot - (J + m * l * l) * dtdot + m * l * g * Math.sin(t));
    }
    
    double dcdt(double r, double t, double rdot, double tdot){
        double u = 0.0, m = 0.1, M = 5.01, J = 0.00214, l = 0.115;
        double g = 9.8, R = 24.5, c = 0.000598, a = 30.9;
        return ((J + m * l * l) *(-R * rdot + m * l * Math.sin(t) * tdot * tdot + a * u) + (-m * l * Math.cos(t)) * (-c * tdot + m * g * l * Math.sin(t))) / ((M + m) * (J + m * l * l) - m * m * l * l * Math.cos(t) * Math.cos(t));
    }
    
    double dddt(double r, double t, double rdot, double tdot){
        double u = 0.0, m = 0.1, M = 5.01, J = 0.00214, l = 0.115;
        double g = 9.8, R = 24.5, c = 0.000598, a = 30.9;
        return ((-m * l * Math.cos(t)) * (-R * rdot + m * l * Math.sin(t) * tdot * tdot + a * u) + (M + m) * (-c * tdot + m* g * l * Math.sin(t))) / ((M + m) * (J + m * l * l) - m * m * l * l * Math.cos(t) * Math.cos(t));
    }
    
    double degreetoradian(double degree){
          return degree / 180.0 * Math.PI;
        }
}