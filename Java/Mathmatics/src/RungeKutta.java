
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author MasatoKondo
 */
public class RungeKutta {
    
    public static void main(String args[]){
        ArrayList<Double> NumList = new ArrayList<>();
        double lambda = 0.08664;
        double h = 0.1;
        double[] k = new double[4];        
        double Num;
        double nextNum;
        double initNum = 100;   //Nの初期値
        
        ArrayList<Point2D.Double> result = new ArrayList<>();
        double t = 0;
        result.add(new Point2D.Double(t, initNum));
       
        //微分方程式を解く
        for(int i=0; i<800; i++){
            t+=h;
            Num = result.get(i).getY();
            k[0] = -lambda*h*Num;
            k[1] = -lambda*h*(Num+k[0]/2);
            k[2] = -lambda*h*(Num+k[1]/2);
            k[3] = -lambda*h*(Num+k[2]);

            nextNum = Num+(k[0] +2*k[1] +2*k[2] +k[3])/6;
            result.add(new Point2D.Double(t, nextNum));
            
            System.out.println(nextNum);
        }
        
        //結果をファイルに出力
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File("RungeKutta.txt")))){
            
            //調整回数と誤差の組から値を取得する
            for(Point2D p : result){
                StringBuilder sb = new StringBuilder(); //データ連結
                
                //データを連結して書き込む
                sb.append(p.getX()).append(" ").append(p.getY()).append("\n");
                bw.write(new String(sb));
            }                
        }
        catch (IOException ex) {
            System.err.println("ImageRecognizerクラスのwriteTransitionメソッドでエラー：ファイル入出力エラー");
            System.exit(-1);
        }
        
    }
    
}
