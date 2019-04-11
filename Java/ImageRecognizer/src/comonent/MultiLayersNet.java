package comonent;

import java.util.ArrayList;
import layer.*;

/**
 * ニューラルネットワークを構成する
 * @author MasatoKondo
 */
public class MultiLayersNet {
    int numLayers;      //各層の数
    int[] numNeurons;   //各層のニューロンの数
    
    //各層の各種レイヤ(Affine, ReLU, SoftmaxWithLoss)
    private ArrayList<Affine>AffineList = new ArrayList<>();
    private ArrayList<Relu>ReluList = new ArrayList<>();
    private SoftmaxWithLoss swl;
    
    //入力データX、正解ラベルT、ネットワークの予測値Y
    private Matrix X;
    private Matrix T;
    private Matrix Y;
    
    //誤差
    private double loss;
    
    //ハイパーパラメータ（学習率）
    private static final double LEARNING_RATE = 0.01;
    
    /**
     * ニューラルネットワークの初期化
     * @param numNeurons 
     */
    public MultiLayersNet(int... numNeurons){
        this.numNeurons = numNeurons;
        numLayers = numNeurons.length-1;    //層の数を可変引数（各層のニューロンの数）から求める 
        initLayers();
    }
    
    /**
     * レイヤを構成する
     */
    private void initLayers(){
         for(int n=0; n<numLayers; n++){
             
            //重みはHeの初期値、bは零ベクトルで初期化
            Matrix W = Matrix.scaler(Math.sqrt(2.0/numNeurons[n]), new Matrix(numNeurons[n], numNeurons[n+1]));
            Matrix b = Matrix.scaler(0, new Matrix(1, numNeurons[n+1]));
            
             //Affineレイヤを追加
             Affine af = new Affine(n, W, b);
             AffineList.add(af);
             
             if(n!=numLayers-1){//隠れ層にはReLUレイヤを追加 
                Relu rl = new Relu(n);
                ReluList.add(rl);
             }
             else{//出力層にはSoftmaxWithLossレイヤを追加
                 swl = new SoftmaxWithLoss(n);
             }
         }
    }
    
    /**
     * 入力データとその正解ラベルを取得する
     * @param iteration 
     */
    public void setInputBatchData(int iteration){
        Mnist trainData = new Mnist("TRAIN", iteration);
        X = trainData.createImagesBatchData();
        T = trainData.createLabelsBatchData();
            
        /*テスト用入力データ
        double[][] x = {{0.5, 0.2, 0.1},{0.1, 0.3, 0.8},{0.3, 0.5, 0.4}, {0.2, 0.3, 0.8}};
        X = new Matrix(x);
        
        double[][] t = {{1,0}, {0,1}, {0,1},{1,0}};
        T = new Matrix(t);*/
    }
    
    /**
     * ネットワーク全体の順伝播処理 
     */
    public void feedForward(){
        Matrix A = null;
        Matrix Z = null;
          
        //入力層側から順伝播処理を実行
        for(int n=0; n<numLayers; n++){
            if(n==0){//第0層（入力層）～第1層における順伝播
                A = AffineList.get(n).forward(X);
                Z = ReluList.get(n).forward(A);
            }
            else if(n!=numLayers-1){//隠れ層における順伝播
                A = AffineList.get(n).forward(Z);
                Z = ReluList.get(n).forward(A);
            }
            else{//第n-1層～第n層(出力層)における順伝播
                A = AffineList.get(n).forward(Z);
            }
        }
        Y = swl.prediction(A);
        loss = swl.forward(A, T);
    }
    
    /**
     * ネットワーク全体の逆伝播
     */
    public void backPropagation(){
        //出力層のdA（誤差Lの重み付き和A_outに関する勾配）を求める
        Matrix dA = swl.backward();
        Matrix dZ;
        
        //出力層側から逆伝播処理
        for(int n=numLayers-1; n>0; n--){
            dZ = AffineList.get(n).backward(dA);
            dA = ReluList.get(n-1).backward(dZ);
            
            if(n==1){
                AffineList.get(n-1).backward(dA);
            }
        }
    }
    
    /**
     * 学習パラメータを更新する
     */
    public void updateParameter(){
        for(Affine af : AffineList){
            af.SGD(LEARNING_RATE);
        }
    } 
}
