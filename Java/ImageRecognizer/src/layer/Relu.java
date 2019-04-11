package layer;
import comonent.Matrix;
import java.util.ArrayList;

/**
 *ReLUレイヤ
 * @author MasatoKondo
 */
public class Relu extends Layer{
    private Matrix Mask;  //Reluの出力を決めるための行列
   
    /**
     * コンストラクタ
     * @param n 
     */
    public Relu(int n){
        name = "ReLU["+(n+1)+"]";
    }
    
    /**
     *順伝播処理
     * @param A
     * @return 
     */
    public Matrix forward(Matrix A){
        //System.out.println(name+"の順伝播");
        Mask = this.calcMask(A);
        return Matrix.multEntrywise(A, Mask);
    }
    
    /**
     * 逆伝播処理
     * @param dZ
     * @return 
     */
    public Matrix backward(Matrix dZ){
        //System.out.println(name+"の逆伝播");
        Matrix dA = Matrix.multEntrywise(dZ, Mask); 
        return dA;
    }
    
    /**
     * Reluの出力を決定するためのマスク処理行列を計算
     * @param A
     * @return 
     */
    private Matrix calcMask(Matrix A){
        ArrayList<Double> dx = new ArrayList<>();
        
        for(int i=0; i<A.getNumRow(); i++){
            for(int j=0; j<A.getNumCol(); j++){
                double value = A.getValue(i, j);//入力Aの要素を取得
                //double value = 0;
                
                //取得した要素が0より大きいならば、そのまま出力。そうでないならば0を出力
                if(value>0){
                    dx.add(1.0);
                }
                else{
                    dx.add(0.0);
                }
            }
        }
        return Mask = new Matrix(A.getNumRow(), A.getNumCol(), dx);
    }
}
