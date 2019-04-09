package comonent;

import java.util.ArrayList;
import main.ImageRecognizer;

/**
 *ニューラルネットワークの構成・計算処理についてのクラス
 * @author MasatoKondo
 */
public class NeuralNetwork {
    private ArrayList<Matrix> weightList;    //各層間の重み
    private ArrayList<Matrix> biasList;     //各層間の閾値
    private ArrayList<Matrix> weightedSumList;    //各層の重み付き和
    private ArrayList<Matrix> activatedList;   //各層の活性化関数による変換後の重み付き和
    private ArrayList<Matrix> gradLossWeightList;      //各層の重みに関する誤差の勾配
    private ArrayList<Matrix> gradLossBiasList;      //各層のバイアスに関する誤差の勾配
    private int numLayers;                  //層の数
    private static final double LEARNING_RATE = 0.01;//学習率
    
    private ArrayList<Matrix> varForWeightList;     //重みのパラメータ更新式で用いる変数を格納するリスト
    private ArrayList<Matrix> varForBiasList;       //バイアスのパラメータ更新式で用いる変数を格納するリスト
    
    //private long dropOutSeed; //ドロップアウトするニューロンを決定するためのシード
    
    /**
     * ニューラルネットワークを初期化する
     * @param numLayer 
     */
    public NeuralNetwork(int... numLayer){
        weightList = new ArrayList<>();
        biasList = new ArrayList<>();
        weightedSumList = new ArrayList<>();
        activatedList = new ArrayList<>();
        gradLossWeightList = new ArrayList<>();
        gradLossBiasList = new ArrayList<>();
        varForWeightList = new ArrayList<>();
        varForBiasList = new ArrayList<>();
        //可変長引数の数から層数を計算（0から数える）
        numLayers = numLayer.length-1;
        InitParameters(numLayer); 
        
        
        /*dropOutSeed = 0;
        for(int n : numLayer){
            dropOutSeed+=n;
        }*/
    }
    
    /**
     * 学習パラメータを初期化する
     * @param numLayer 
     */
    private void InitParameters(int...numLayer){
        //重みと閾値を初期化
        for(int n=0; n<numLayers; n++){
            //重みWnのサイズは「入力側の層のニューロンの数×出力側の層のニューロンの数」
            Matrix W = new Matrix(numLayer[n],numLayer[n+1]);
            weightList.add(Matrix.scaler(Math.sqrt(2.0/numLayer[n]), W));// Heの初期値
            //weightList.add(Matrix.scaler(0.01, W));//N(0, 0.01)に従う乱数で初期化
            
            //閾値bnのサイズは「出力側の層のニューロンの数」
            Matrix b = new Matrix(1, numLayer[n+1]);
            biasList.add(Matrix.scaler(0, b));//バイアスは0で初期化
            
            //更新式で利用する変数のリストにn行目の重み・バイアスのサイズと一致する零行列・零ベクトルを追加
            varForWeightList.add(Matrix.scaler(0, W));
            varForBiasList.add(Matrix.scaler(0, b));
            
            weightedSumList.add(null);
            activatedList.add(null);
            gradLossWeightList.add(null);
            gradLossBiasList.add(null);
        }
    }
    
    /**
     * 順伝播処理で入力データについて正解の予測値を計算
     * @param X
     * @return 
     */
    public Matrix feedForward(Matrix X){
        Matrix Z=X; //入力値を取得
        weightedSumList.set(0, X);
        activatedList.set(0, X);
        
        //層の先頭から末尾まで、以下の処理を繰り返す
        for(int n=0; n<numLayers; n++){
            Matrix W = weightList.get(n);//重みを取得
            Matrix b = biasList.get(n);    //閾値を取得（入力データの行数に合わせてデータ生成）
            Matrix B  =b.createBatchVerBias(Z.getNumRow());
            
            //重み付き和を計算し、リストに格納
            Matrix A =Matrix.add(Matrix.mult(Z, W), B);
            
            if(n<numLayers-1){//隠れ層ではReLU関数で変換する
                Z = A.relu();
                
                //Z = Z.dropOut(dropOutSeed);
                //dropOutList.add(Z);
                //dropOutSeed++;
                //System.out.println("dropOutSeed"+dropOutSeed);
            }
            else{//出力層ではソフトマックス関数で変換する
                Z = A.softmax();
            }
            
            if(n<numLayers-1){//現在の層における重み付き和・変換後の値をリストに格納
                weightedSumList.set(n+1, A);
                activatedList.set(n+1, Z);
            }
        }
        return Z;
    }
    
    /**
     * 交差エントロピー誤差を計算する
     * @param Y
     * @param T
     * @return 
     */
    public double crossEntropyError(Matrix Y, Matrix T){
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
     * 出力層における重み付き和に関する誤差の勾配を計算する
     * @param Y
     * @param T
     * @return 
     */
    private Matrix calcOutputLayersGrad(Matrix Y, Matrix T){
       if(Y.getNumRow()!=T.getNumRow() || Y.getNumCol()!=T.getNumCol()){
            System.err.println("NeuralNetworkクラスのcalcOutputLayersGradメソッドでエラー：予測値Yと正解Tのサイズの不一致");
            System.exit(0);
        }
        ArrayList<Double> deltaList = new ArrayList<>();
        
        for(int i=0; i<Y.getNumRow(); i++){
            for(int j=0; j<Y.getNumCol(); j++){
                double value = (Y.getValue(i, j)-T.getValue(i, j))/Y.getNumRow();
                deltaList.add(value);
            }
        }
        return new Matrix(Y.getNumRow(), Y.getNumCol(), deltaList);
    }
    
    /**
     * 隠れ層における重み付き和に関する誤差の勾配を計算する
     * @param A
     * @param gradLZ
     * @return 
     */
    private Matrix calcHiddenLayersGrad(Matrix A, Matrix gradLZ){
        if(A.getNumRow()!=gradLZ.getNumRow() || A.getNumCol()!=gradLZ.getNumCol()){
            System.err.println("NeuralNetworkクラスのcalcHiddenLayersGradメソッドでエラー：重み付き和Aと勾配gradLZのサイズの不一致");
            System.exit(0);
        }
        ArrayList<Double> deltaList = new ArrayList<>();
        
        for(int i=0; i<A.getNumRow(); i++){
            for(int j=0; j<A.getNumCol(); j++){
                double value;
                
                //ReLUの場合の勾配を計算
                if(A.getValue(i, j)<0){
                    value = 0;
                }
                else{
                    value = gradLZ.getValue(i, j);
                }
                deltaList.add(value);
            }
        }
        return new Matrix(A.getNumRow(), A.getNumCol(), deltaList);
    }
    
    /**
     * 誤差逆伝播法で学習パラメータに関する誤差の勾配を計算
     * @param Y
     * @param T 
     */
    public void backPropagation(Matrix Y, Matrix T){
        
        //出力層における重み付き和に関する誤差の勾配を計算
        Matrix gradLA = this.calcOutputLayersGrad(Y, T);
        
        //層の末尾から先頭に向けて、計算処理を行う
        for(int n=numLayers-1; n>=0; n--){
            Matrix W = weightList.get(n);       //重みを取得
            Matrix Z = activatedList.get(n);    //閾値を取得
            
            //重み・バイアスに関する誤差の勾配を計算
            Matrix gradLW = Matrix.mult(Z.transpose(), gradLA);
            Matrix gradLB = gradLA.colAxisSum();
            
            gradLossWeightList.set(n, gradLW);
            gradLossBiasList.set(n, gradLB);
            
            //次の層における重み付き和に関する誤差の勾配を計算
            Matrix gradLZ = Matrix.mult(gradLA, W.transpose());
            gradLA = this.calcHiddenLayersGrad(weightedSumList.get(n), gradLZ);
        }
    }
    
    
    /**
     * 最適化アルゴリズム（確率的勾配降下法）
     */
    public void SGD(){
        for(int n=0; n<numLayers; n++){
            Matrix W = Matrix.add(weightList.get(n), Matrix.scaler(-LEARNING_RATE, gradLossWeightList.get(n)));
            Matrix b = Matrix.add(biasList.get(n), Matrix.scaler(-LEARNING_RATE, gradLossBiasList.get(n)));
            
            weightList.set(n, W);
            biasList.set(n, b);
        }
    }
    
    /**
     * 最適化アルゴリズム（AdaGrad）
     */
    public void AdaGrad(){
        for(int n=0; n<numLayers; n++){
            Matrix gW = gradLossWeightList.get(n);
            Matrix gB = gradLossBiasList.get(n);
            
            //勾配のアダマール積（要素ごとの掛け算）を計算
            Matrix gW2 = Matrix.multEntrywise(gW, gW);
            Matrix gB2 = Matrix.multEntrywise(gB, gB);
            
            //h+∂L/∂W, h+∂L/∂B を計算
            Matrix Hw = Matrix.add(varForWeightList.get(n), gW2);
            Matrix Hb = Matrix.add(varForBiasList.get(n), gB2);
            
            //hを更新
            varForWeightList.set(n, Hw);
            varForBiasList.set(n, Hb);
            
            //(-η/√h)∂L/∂W, (-η/√h)∂L/∂B を計算
            Matrix lrW = Matrix.scaler(-LEARNING_RATE, Matrix.divEntrywise(gW, Matrix.sqrt(Hw)));
            Matrix lrB = Matrix.scaler(-LEARNING_RATE, Matrix.divEntrywise(gB, Matrix.sqrt(Hb)));
            
            //重み・バイアスを更新
            Matrix W = Matrix.add(weightList.get(n), lrW);
            Matrix b = Matrix.add(biasList.get(n), lrB);
            
            weightList.set(n, W);
            biasList.set(n, b);
        }
    }
    
    /**
     * 最適化アルゴリズム（RMSprop）
     */
    public void RMSprop(){
        for(int n=0; n<numLayers; n++){
            Matrix gW = gradLossWeightList.get(n);
            Matrix gB = gradLossBiasList.get(n);
            
            //勾配のアダマール積（要素ごとの掛け算）を計算
            Matrix gW2 = Matrix.multEntrywise(gW, gW);
            Matrix gB2 = Matrix.multEntrywise(gB, gB);
            
            double decacyRate = 0.99;//減衰率α
            Matrix ahw = Matrix.scaler(decacyRate, varForWeightList.get(n));
            Matrix ahb = Matrix.scaler(decacyRate, varForBiasList.get(n));
            
            //αh+(1-α)∂L/∂W, αh+(1-α)∂L/∂B を計算
            Matrix Hw = Matrix.add(ahw, Matrix.scaler(1.0-decacyRate, gW2));
            Matrix Hb = Matrix.add(ahb, Matrix.scaler(1.0-decacyRate, gB2));
            
            //hを更新
            varForWeightList.set(n, Hw);
            varForBiasList.set(n, Hb);
            
            //(-η/√h)∂L/∂W, (-η/√h)∂L/∂B を計算
            Matrix lrW = Matrix.scaler(-LEARNING_RATE, Matrix.divEntrywise(gW, Matrix.sqrt(Hw)));
            Matrix lrB = Matrix.scaler(-LEARNING_RATE, Matrix.divEntrywise(gB, Matrix.sqrt(Hb)));
            
            //重み・バイアスを更新
            Matrix W = Matrix.add(weightList.get(n), lrW);
            Matrix b = Matrix.add(biasList.get(n), lrB);
            
            weightList.set(n, W);
            biasList.set(n, b);
        }
    }
    
    /**
     * ニューラルネットワークの学習パラメータを表示する
     */
    public void showParameters(){
        int n=0;
        /*for(Matrix W: weightList){
            System.out.println("W_"+n+":\n"+W.toString());
            n++;
        }*/
        n=0;
        for(Matrix b: biasList){
            System.out.println("b_"+n+":\n"+b.toString());
            n++;
        }
        /*n=0;
        for(Matrix gW: gradLossWeightList){
            System.out.println("gW_"+n+":\n"+gW.toString());
            n++;
        }
        n=0;
        for(Matrix gb: gradLossBiasList){
            System.out.println("b_"+n+":\n"+gb.toString());
            n++;
        } */  
    }
    
    /**
     * 数値微分で勾配を計算する
     * @param X
     * @param T 
     */
    public void numricalGrad(Matrix X, Matrix T){
         //元の勾配データを退避
        ArrayList<Matrix> savedWList = new ArrayList<>(weightList);
        ArrayList<Matrix> savedBList = new ArrayList<>(biasList);
        double h = 1e-4;
        
        //重みの勾配を計算
        ArrayList<Double> gradW = new ArrayList<>();
        for(int n=0; n<numLayers; n++){
            Matrix W = weightList.get(n);
            
            for(int i=0; i<W.getNumRow(); i++){
                for(int j=0; j<W.getNumCol(); j++){
                    
                    //n番目の重みWnの(i,j)成分に微小量を足し、誤差を計算
                    Matrix Wplus = W.addElement(i, j, h);
                    weightList.set(n, Wplus);
                    Matrix Yplus = this.feedForward(X);
                    double Lplus = this.crossEntropyError(Yplus, T);
                    weightList = new ArrayList<>(savedWList);//重みを元に戻す
                                        
                    //n番目の重みWnの(i,j)成分に微小量を引き、誤差を計算
                    Matrix Wminus = W.addElement(i, j, -h);
                    weightList.set(n, Wminus);
                    Matrix Yminus = this.feedForward(X);
                    double Lminus = this.crossEntropyError(Yminus, T);
                    weightList = new ArrayList<>(savedWList);//重みを元に戻す
                    
                    //重みについての誤差の偏微分を計算
                    double gw = (Lplus-Lminus)/(2*h);
                    gradW.add(gw);
                }
            }
            Matrix gW = new Matrix(W.getNumRow(), W.getNumCol(), gradW);
            gradLossWeightList.set(n, gW);
            gradW.clear();
        }
        
        //閾値の勾配を計算
        ArrayList<Double> gradB = new ArrayList<>();
        for(int n=0; n<numLayers; n++){
            Matrix b = biasList.get(n);
            for(int i=0; i<b.getNumRow(); i++){
                for(int j=0; j<b.getNumCol(); j++){
                    
                    //n番目の閾値bnの(i,j)成分に微小量を足し、誤差を計算
                    Matrix Bplus = b.addElement(i, j, h);
                    biasList.set(n, Bplus);
                    Matrix Yplus = this.feedForward(X);
                    double Lplus = this.crossEntropyError(Yplus, T);
                    biasList = new ArrayList<>(savedBList);//閾値を元に戻す
                    
                    
                    //n番目の閾値bnの(i,j)成分に微小量を引き、誤差を計算
                    Matrix Bminus = b.addElement(i, j, -h);
                    biasList.set(n, Bminus);
                    //System.out.println("b:n"+biasList.get(n));
                    Matrix Yminus = this.feedForward(X);
                    double Lminus = this.crossEntropyError(Yminus, T);
                    biasList = new ArrayList<>(savedBList);//閾値を元に戻す
                   
                    //閾値についての誤差の偏微分を計算
                    double gb = (Lplus-Lminus)/(2*h);
                    gradB.add(gb);
                }
            }
            Matrix gB = new Matrix(b.getNumRow(), b.getNumCol(), gradB);
            gradLossBiasList.set(n, gB);
            gradB.clear();
        }
    }
    
    public static void main(String argas[]){
        //Mnist trainData = new Mnist("TRAIN");
        //Matrix X = trainData.getImagesDataAsMatrix(0);
        //Matrix T = trainData.getLabelsDataAsOneHot(0);
        NeuralNetwork net = new NeuralNetwork(3,4,2);
        
        double[][] x = {{0.5, 0.2, 0.1},{0.1, 0.3, 0.8},{0.3, 0.5, 0.4}, {0.2, 0.3, 0.8}};
        Matrix X = new Matrix(x);
        
        
        double[][] t = {{1,0}, {0,1}, {0,1},{1,0}};
        Matrix T = new Matrix(t);
        net.showParameters();
        
        System.out.println(X);
        System.out.println(T);
        Matrix Y = net.feedForward(X);
        System.out.println(Y);
        double loss = net.crossEntropyError(Y, T);
        System.out.println("loss:"+loss);
        
        /*
        System.out.println("numuricalGrad");
        net.numricalGrad(X, T);
        net.showParameters();
        */
        for(int k=0; k<1000; k++){
            System.out.println("backPropargation");
            net.backPropagation(Y, T);
            net.SGD();
            net.showParameters();
            Y = net.feedForward(X);
            loss = net.crossEntropyError(Y, T);
            System.out.println("loss:"+loss);
        }
    }
}
