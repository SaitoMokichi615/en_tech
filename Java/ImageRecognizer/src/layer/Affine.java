package layer;

import comonent.Matrix;

/**
 *重み付き和の計算を行うレイヤ
 * @author MasatoKondo
 */
public class Affine extends Layer{
    private Matrix W;
    private Matrix b;
    private Matrix dW;
    private Matrix db;
    
    private Matrix X;
    private Matrix A;
    
    /**
     * レイヤの初期化
     * @param n
     * @param W
     * @param b 
     */
    public Affine(int n, Matrix W, Matrix b){
        name = "Affine["+(n+1)+"]";
        this.W = W;
        this.b = b;
    }
    
    /**
     *順伝播処理
     * @param X
     * @return 
     */
    public Matrix forward(Matrix X){
        //System.out.println(name+"の順伝播");
        this.X = X;
        A = Matrix.add(Matrix.mult(X, W), b.createBatchVerBias(X.getNumRow()));
        return A;
    }
    
    /**
     * 逆伝播処理
     * @param dA
     * @return 
     */
    public Matrix backward(Matrix dA){
        //System.out.println(name+"の逆伝播");
        dW = Matrix.mult(X.transpose(), dA);
        db = dA.colAxisSum();
        Matrix dZ = Matrix.mult(dA, W.transpose());
        return dZ;
    }
    
    /**
     * 学習パラメータを更新する(確率的勾配降下法)
     * @param lr
     */
    public void SGD(double lr){
        W = Matrix.add(W, Matrix.scaler(-lr, dW));
        b = Matrix.add(b, Matrix.scaler(-lr, db));
    }
}
