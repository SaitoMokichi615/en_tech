package layer;

import comonent.Matrix;
import java.util.ArrayList;

/**
 * SoftmaxWithLossレイヤ
 * @author MasatoKondo
 */
public class SoftmaxWithLoss extends Layer{
    private Matrix Y;
    private Matrix T;
    
    
    public SoftmaxWithLoss(int n){
        name = "SoftmaxWithLoss["+(n+1)+"]";
    }
    
    /**
     * 順伝播処理
     * @param A
     * @param T
     * @return 
     */
    public double forward(Matrix A, Matrix T){
        //System.out.println(name+"の順伝播");
        Y = A.softmax();
        this.T = T;
        double loss = crossEntropyError(Y, T);
        return loss;
    }
    
    /**
     * 逆伝播処理
     * @return 
     */
    public Matrix backward(){
        //System.out.println(name+"の逆伝播");
        ArrayList<Double> dx = new ArrayList<>();
                
        for(int i=0; i<Y.getNumRow(); i++){
            for(int j=0; j<Y.getNumCol(); j++){
                double value = Y.getValue(i, j)-T.getValue(i, j);
                dx.add(value/Y.getNumRow());
            }
        }
        return new Matrix(Y.getNumRow(), Y.getNumCol(), dx);
    }
    
    /**
     * 交差エントロピー誤差を計算する
     * @param Y
     * @param T
     * @return 
     */
    private double crossEntropyError(Matrix Y, Matrix T){
        if(Y.getNumRow()!=T.getNumRow() || Y.getNumCol()!=T.getNumCol()){
            System.err.println("NeuralNetworkクラスのcrossEntropyErrorメソッドでエラー：予測値Yと正解Tのサイズの不一致");
            System.exit(0);
        }
        
        double sum = 0;
        double delta = 1e-7;
        
        for(int i=0; i<Y.getNumRow(); i++){
            for(int j=0; j<Y.getNumCol(); j++){
                sum += T.getValue(i, j)*Math.log(Y.getValue(i, j)+delta);
            }
        }
        return -sum/Y.getNumRow();
    }
    
    /**
     * 入力データの予測結果を返す
     * @param A
     * @return 
     */
    public Matrix prediction(Matrix A){
        return A.softmax();
    }
}
