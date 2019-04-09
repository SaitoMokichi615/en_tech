package comonent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 *行列の定義や生成を行うクラス
 * @author MasatoKondo
 */
public class Matrix {
    private int row;    //行数
    private int col;    //列数
    private ArrayList<Double> valuesList;   //要素
    
    /**
     * 行列の要素を標準正規分布に従う乱数で初期化
     * @param row
     * @param col 
     */
    public Matrix(int row, int col){
        this.row = row;
        this.col = col;
        valuesList = new ArrayList<>();
        
        for(int n=0; n<row*col; n++){
            long seed = n;
            valuesList.add(new Random(seed).nextGaussian());
        }
    }
    
    /**
     * 指定したリストを要素に持つ行列を生成する
     * @param row
     * @param col
     * @param valuesList 
     */
    public Matrix(int row, int col, ArrayList<Double> valuesList){
        this.row = row;
        this.col = col;
        this.valuesList = new ArrayList<>(valuesList);
    }
    
    /**
     * 指定した二次元配列から行列を生成する
     * @param matrix 
     */
    public Matrix(double[][] matrix){
        row = matrix.length;
        col = matrix[0].length;
        valuesList = new ArrayList<>();
        
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                valuesList.add(matrix[i][j]);
            }
        }
    }
    
    /**
     * 行列の行数を取得
     * @return 
     */
    public int getNumRow(){
        return row;
    }
    
    /**
     * 行列の列数を取得
     * @return 
     */
    public int getNumCol(){
        return col;
    }
    
    /**
     * 行列の形状を文字列で返す
     * @return 
     */
    public String getShape(){
        return "("+row+","+col+")";
    }
    
    /**
     * 指定した位置（行・列）の要素を返す
     * @param i
     * @param j
     * @return 
     */
    public double getValue(int i, int j){
        if(i<0 || j<0 || i>row || j>col){
            System.err.println("MatrixクラスのgetValueメソッドでエラー：不正な行番号または列番号の指定");
            System.exit(0);
        }
        return valuesList.get(i*col+j);
    }
      
    /**
     * 2つの行列の和を計算する
     * @param A
     * @param B
     * @return 
     */
    public static Matrix add(Matrix A, Matrix B){
        //2つの行列のサイズが一致していたら以下の処理を実行
        if(A.row!=B.row || A.col!=B.col){
            System.err.println("Matrixクラスのaddメソッドでエラー：行列のサイズの不一致");
            System.exit(0);   
        }
        ArrayList addValuesList = new ArrayList<>();//行列の和の要素を格納するリスト
        for(int n=0; n<A.row*A.col; n++){
            addValuesList.add(A.valuesList.get(n)+B.valuesList.get(n));
        }
        return new Matrix(A.row, A.col, addValuesList); 
        
    }
    
    /**
     * 2つの行列の要素同士の積を計算する
     * @param A
     * @param B
     * @return 
     */
    public static Matrix multEntrywise(Matrix A, Matrix B){
        //2つの行列のサイズが一致していたら以下の処理を実行
        if(A.row!=B.row || A.col!=B.col){
            System.err.println("Matrixクラスのaddメソッドでエラー：行列のサイズの不一致");
            System.exit(0);   
        }
        ArrayList<Double> tmp = new ArrayList<>();
        
        for(int n=0; n<A.row*A.col; n++){
           double product = A.valuesList.get(n)*B.valuesList.get(n);
           tmp.add(product);
        }
        return new Matrix(A.row, A.col, tmp);    
    }
    
    /**
     * 2つの行列の要素同士の商を計算する
     * @param A
     * @param B
     * @return 
     */
    public static Matrix divEntrywise(Matrix A, Matrix B){
        //2つの行列のサイズが一致していたら以下の処理を実行
        if(A.row!=B.row || A.col!=B.col){
            System.err.println("Matrixクラスのaddメソッドでエラー：行列のサイズの不一致");
            System.exit(0);   
        }
        ArrayList<Double> tmp = new ArrayList<>();
        double h = 1e-7;
        
        for(int n=0; n<A.row*A.col; n++){
           double product = A.valuesList.get(n)/(B.valuesList.get(n)+h);
           tmp.add(product);
        }
        return new Matrix(A.row, A.col, tmp);
    }
    
    /**
     * 行列のスカラー倍を計算する
     * @param k
     * @param A
     * @return 
     */
    public static Matrix scaler(double k, Matrix A){
        ArrayList<Double> scalerValuesList = new ArrayList<>();
        
        for(int n=0; n<A.row*A.col; n++){
            scalerValuesList.add(A.valuesList.get(n)*k);
        }                        
        return new Matrix(A.row, A.col, scalerValuesList);
    }
    
    /**
     * 行列の要素の平方根を計算する
     * @param A
     * @return 
     */
    public static Matrix sqrt(Matrix A){
        ArrayList<Double> sqrtValueList = new ArrayList<>();
        
        for(int n=0; n<A.row*A.col; n++){
            sqrtValueList.add(Math.sqrt(A.valuesList.get(n)));
        }
        return new Matrix(A.row, A.col, sqrtValueList);
    }
       
    /**
     * 2つの行列の積を計算する
     * @param A
     * @param B
     * @return 
     */
    public static Matrix mult(Matrix A, Matrix B){
        ArrayList<Double> multValuesList = new ArrayList<>();
        
        if(A.col!=B.row){
            System.err.println("Matrixクラスのmultメソッドでエラー：行数または列数の不一致");
            System.exit(0);
        }
        
        for(int i=0; i<A.row; i++){
            for(int j=0; j<B.col; j++){
                double sum = 0;
                for(int k=0; k<B.row; k++){
                    sum += A.getValue(i, k)*B.getValue(k, j);
                }
                multValuesList.add(sum);
            }
        }
        return new Matrix(A.row, B.col, multValuesList);    
    }
    
    /**
    転置行列を返す
     * @return 
     */
    public Matrix transpose(){
        ArrayList<Double> transposeList = new ArrayList<>();
        
        for(int j=0; j<col; j++){
            for(int i=0; i<row; i++){
                transposeList.add(this.getValue(i, j));
            }
        }
        return new Matrix(col, row, transposeList);
    }
    
    /**
     * ミニバッチ処理に対応した閾値行列を生成
     * @param batchSize
     * @return 
     */
    public Matrix createBatchVerBias(int batchSize){
        
        if(this.row>1){
            System.err.println("MatrixクラスのcreateBatchVerBiasメソッドでエラー:不正なベクトルを入力");
        }
        ArrayList<Double> batchVerBiasValues = new ArrayList<>();//バッチ対応のバイアスBの要素
        
        for(int n=0; n<batchSize; n++){
            for(int k=0; k<col; k++){
                batchVerBiasValues.add(valuesList.get(k));
            }
        }
        return new Matrix(batchSize, col, batchVerBiasValues);
    }
    
    /**
     * 指定した行の要素（行ベクトル）を返す
     * @param i
     * @return 
     */
    public Matrix getRowVec(int i){
        if(i<0 || i>row*col){
            System.err.println("MatrixクラスのgetRowVecメソッドでエラー：行指定が不正");
            System.exit(0);
        }
        ArrayList<Double> rowVec = new ArrayList<>();
        
        for(int n = i*col; n<(i+1)*col; n++){
            rowVec.add(valuesList.get(n));
        }
        return new Matrix(1, col, rowVec);
    }
    
    /**
     * 行列の列ベクトルの成分の総和を計算し、計算結果を要素とする行列を生成
     * @return 
     */
    public Matrix colAxisSum(){
        ArrayList<Double> axisSumList = new ArrayList<>();
                
        for(int j=0; j<col; j++){
            double sum = 0;
            
            for(int i=0; i<row; i++){
                sum+=this.getValue(i, j);
            }
            axisSumList.add(sum);
        }
        return new Matrix(1, col, axisSumList);
    }
    
    /**
     * 行列の要素をReLU関数で変換する
     * @return 
     */
    public Matrix relu(){
        ArrayList<Double> activatedValue = new ArrayList<>();
        
        for(int n=0; n<row*col; n++){
            double weightedSum = valuesList.get(n);   //重み付き和を取得
            
            if(weightedSum<0){//変換前の値が0より小さいならば、0を出力
                activatedValue.add(0.0);
            }
            else{//そうでないならば、変換前の値を出力
                activatedValue.add(weightedSum);
            }
        }
        return new Matrix(row, col, activatedValue);
    }
    
    /**
     * 行列の要素をソフトマックス関数で変換する
     * @return 
     */
    public Matrix softmax(){
        ArrayList<Double> output = new ArrayList<>();
        
        for(int i=0; i<row; i++){
            Matrix a = this.getRowVec(i);
            double max = Collections.max(a.valuesList);
            double sum = 0;
            
            for(int j=0; j<col; j++){//i番目の行の各要素をexpで変換し、変換後の要素の総和を求める
                sum+=Math.exp(a.valuesList.get(j)-max);
            }
            
            for(int k=0; k<col; k++){//ソフトマックス関数の式yを計算し、リストに格納する
                output.add(Math.exp(a.valuesList.get(k)-max)/sum);
            }
        }
        return new Matrix(row, col, output);
    }
    
    /**
     * 行列の最大要素の位置を返す
     * @return 
     */
    public int getIndexOfMaxEement(){
        return valuesList.indexOf(Collections.max(valuesList));
    }
    
    /**
     * 指定した位置に指定した要素を足す
     * @param i
     * @param j
     * @param h
     * @return 
     */
    public Matrix addElement(int i, int j, double h){
        if(i<0 || j<0 || i>=row || j>=col){
            System.err.println("MatrixクラスのaddElementメソッドでエラー：不正な指定位置");
            System.exit(0);
        }
        ArrayList<Double> temp = new ArrayList<>();
        
        for(int p=0; p<row; p++){
            for(int q=0; q<col; q++){
                double value = this.getValue(p, q);
                
                if(p==i && j==q){
                    value+= h;
                }
                temp.add(value);
            }
        }
        return new Matrix(row, col, temp);
    }
    
   /* public Matrix dropOut(long seed){
        double dropOutRatio = 0.5;
        ArrayList<Double> dropOutData = new ArrayList<>();
        
        for(int n=0; n<row*col; n++){
            double p = new Random(seed).nextDouble();
            
            if(dropOutRatio<p){
                dropOutData.add(0.0);
            }
            else{
                dropOutData.add(valuesList.get(n));
            }
        }
        return new Matrix(row, col, dropOutData);
    }*/
    
    /**
     * 指定した行列に指定した幅でパディングする
     * @param X
     * @param padding
     * @return 
     */
    public static Matrix padding(Matrix X, int padding){
        
        if(padding<0){
            System.err.println("Matrixクラスのpaddingメソッドでエラー：指定幅が不正");
            System.exit(0);
        }
        ArrayList<Double> paddingMatrixList = new ArrayList<>();
        int row = X.row+2*padding;
        int col = X.col+2*padding;
        
        int k=0;//パディング前の行列の要素にアクセスするための添字
        for(int i=0; i<row; i++){
            for(int j=0; j<col; j++){
                
                if(i>padding-1 && j>padding-1 && i<row-padding && j<col-padding){
                    paddingMatrixList.add(X.valuesList.get(k));
                    k++;
                }
                else{
                    paddingMatrixList.add(0.0);
                }
            }
        }
        return new Matrix(row, col, paddingMatrixList);
    }
    
    /**
     * 畳み込み演算
     * @param A
     * @param F
     * @param padding
     * @param stride
     * @return 
     */
    public static Matrix convolute(Matrix A, Matrix F, int padding, int stride){
        //入力データにパディング処理を施す
        Matrix X = Matrix.padding(A, padding);
        
        //出力する特徴マップの行数と列数
        int mapRow = (A.row-F.row+2*padding)/stride +1;
        int mapCol = (A.col-F.col+2*padding)/stride +1;
        
        if((A.row-F.row+2*padding)%stride!=0 || (A.col-F.col+2*padding)%stride!=0 ){
            System.err.println("Matrixクラスのconvoluteメソッドでエラー：計算不能なパラメータを入力");
        }
        ArrayList<Double> mapList = new ArrayList<>();  //特徴マップの値を格納するリスト
        int mapCount = 0;                               //入力データに渡すパラメータ内の係数その1
        
        //特徴マップに格納する各要素の値を計算する
        for(int n=0; n<mapRow*mapCol; n++){
            if(n>0 && n%mapCol==0){
               mapCount++; 
            }
            
            int filterCount = 0;//入力データに渡すパラメータ内の係数その2
            double sum = 0;
            
            for(int k=0; k<F.row*F.col; k++){
                if(k>0 && k%F.row==0){
                    filterCount++;
                }
                //フィルタ範囲で要素同士の積を計算し、その総和をとる
                sum += F.valuesList.get(k)*X.valuesList.get(k + mapCount*X.col + filterCount*(X.col-F.col)+n%mapCol*stride);
            }
            mapList.add(sum);
        }
        return new Matrix(mapRow, mapCol, mapList);
    }
    
    /**
     * 行列を表示する
     * @return 
     */
    @Override
    public String toString(){
        String elements="";
        
        for(int n=0; n<row*col; n++){
            if(n%col==0){
                elements+="[";
            }
            elements+=valuesList.get(n)+", ";
            
            if(n%col==col-1){
                elements+="]\n";
            }
        }
        return elements;
    }
    
    public static void main(String args[]){
        double[][] x = {{1,2,3,0},
                        {0,1,2,3},
                        {3,0,1,2},
                        {2,3,0,1},
                     };
        
        double[][] f = {{2,0,1},
                        {0,1,2},
                        {1,0,2},
                    };
        
        Matrix X = new Matrix(x);
        Matrix F = new Matrix(f);
        Matrix Y = Matrix.convolute(X, F, 0, 1);
        Matrix Z = Matrix.convolute(X, F, 1, 1);
        System.out.println("入力データ\n"+X);
        System.out.println("フィルタ\n"+F);
        System.out.println("特徴マップ1\n"+Y);
        System.out.println("特徴マップ2\n"+Z);
        
        Matrix[] Batch = new Matrix[100];
        for(int n=0; n<100; n++){
            Batch[n] =  new Matrix(28, 28); 
        }
        
        int count = 0;
        for(Matrix I : Batch){
            System.out.println(count+"番目の特徴マップ");
            System.out.println(Matrix.convolute(I, F, 0, 1));
            count++;
        }
    }
}
