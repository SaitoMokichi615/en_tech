package comonent;

import java.awt.image.BufferedImage;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;

/**
 *画像データを扱うためのクラス
 * @author MasatoKondo
 */
public class Mnist {
    private ArrayList<Double> imagesList;       //画像データのリスト
    private ArrayList<Double> labelsList;       //正解ラベルのリスト
    private int[] indexList;                    //画像番号のリスト
    private int numData;                        //データの総数
    private String pixelsFileName;              //画素値データのファイル名
    private String labelsFileName;              //正解ラベルのファイル名
    public static final int BATCH_SIZE = 100;   //バッチサイズ
    public static final int NUM_TRAIN_DATA = 60000; //訓練データの総数
    public static final int NUM_TEST_DATA = 10000; //テストデータの総数
    public static final int NUM_CLASS = 10;    //分類クラス数
    public static final int NUM_PIXELS = 784;   //画素数
    public static final int SIZE_IMAGE = 28;    //画像の大きさ
    public static final int EPOCH = NUM_TRAIN_DATA/BATCH_SIZE;  //エポック数
    private static final int MAX_PIXELS_VALUE = 255;   //画素値の最大値（正規化に使用）
    private long seed;              //画像データを取得する際のシード（学習のイテレーションを入力）
    private boolean readTestFlaf;   //テストデータの前半・後半いずれを読み込むか決定するフラグ
    
    /**
     * 画像データの初期化
     * @param type 
     */
    public Mnist(String type, long seed){
        imagesList = new ArrayList<>();
        labelsList = new ArrayList<>();
        indexList = new int[BATCH_SIZE];
        System.out.println(type+"データを読込中…");
        
        switch(type){
            case "TRAIN":
                pixelsFileName = "train-images-idx3-ubyte.gz";
                labelsFileName = "train-labels-idx1-ubyte.gz";
                
                this.seed = seed;//シードを取得
                getRandomlyIndex();//バッチサイズ分、ランダムに画像データのインデックスを取得
                getImageDataFromDataBase("train");
                //readPixelsData();//取得したインデックスに対応する画像データを読み込む
                //readLabelsData();//取得したインデックスに対応する正解ラベルを読み込む
                break;
                
            case "TEST":
                pixelsFileName = "t10k-images-idx3-ubyte.gz";
                labelsFileName = "t10k-labels-idx1-ubyte.gz";
                
                /*indexList = IntStream.range((int)seed, (int)seed+Mnist.BATCH_SIZE).toArray();
                getImageDataFromDataBase("test");*/
                indexList = IntStream.range(0, NUM_TEST_DATA).toArray();
                readPixelsData();//画像データを読み込む
                readLabelsData();//正解ラベルを読み込む
                break;
                
            default:
                System.err.println("Mnistクラスのコンストラクタでエラー：type指定のエラー");
                System.exit(0);
        }
    }
    
    /**
     * 画素値データを読み込む
     */
    private void readPixelsData(){
         try(DataInputStream is = new DataInputStream(new GZIPInputStream(new FileInputStream(pixelsFileName)))){
            
            //マジックナンバー、データの総数、画素数を読み込む
            is.readInt();
            numData =is.readInt();//データの総数は保持する
            is.readInt();
            is.readInt();
            
            int count = 0;//読み込んだ画像データの数 
            
            //画素値データを読み込む
            for(int n=0; n<numData; n++){
                for(int j=0; j<NUM_PIXELS; j++){
                    double value = (double)is.readUnsignedByte();
                    
                    //今読み込んでいる画像が、インデックス番目の画像ならば、画素値を読み込む
                    if(indexList[count]==n){
                        imagesList.add(value/MAX_PIXELS_VALUE);//正規化してリストに格納
                        
                        if(j==NUM_PIXELS-1){//現在の画像の画素値を読み込み終えたら、次のインデックスを取得
                            //System.out.println(indexList[count]+"番目の画像データを読込");
                            
                            if(count<indexList.length-1){
                                count++;
                            }
                            else{
                                break;
                            }
                        }
                    }
                    
                }
            }
        }
        catch (FileNotFoundException ex) {
            System.err.println("FileIOクラスのreadPixelsDataメソッドでエラー：ファイルの指定が不正");
            System.exit(0);
        }
        catch (IOException ex){
            System.err.println("FileIOクラスのreadPixelsDataメソッドでエラー：ファイルの読込失敗");
            System.exit(0);
        }
    }
    
    /**
     * 正解ラベルを読み込む
     */
    private void readLabelsData(){
        try(DataInputStream is = new DataInputStream(new GZIPInputStream(new FileInputStream(labelsFileName)))){
            
            //マジックナンバー、データの総数を読み込む
            is.readInt();
            numData = is.readInt();
            
            int count = 0;//読み込んだ画像データの数
            
            for(int n=0; n<numData; n++){
                int label = (int)is.readUnsignedByte();
                
                if(indexList[count]==n){//ファイルから読み込んでいる画像が、index番目のものであれば、正解ラベルの行列(one-hot表現)を生成
                    double[] oneHot = new double[NUM_CLASS];
                    oneHot[label] = 1.0;
                    for(double value : oneHot){
                        labelsList.add(value);
                    }
                    
                    //System.out.println(n+"番目の正解ラベルを読込");
                    if(count<indexList.length-1){
                        count++;
                    }
                }
                if(count>=indexList.length){//全ての画像番号を読み込んだら処理を終了
                    break;
                }
            }    
        }
        catch (FileNotFoundException ex) {
            System.err.println("FileIOクラスのreadLabelssDataメソッドでエラー：ファイルの指定が不正");
            System.exit(0);
        }
        catch (IOException ex){
            System.err.println("FileIOクラスのreadLabelsDataメソッドでエラー：ファイルの読込失敗");
            System.exit(0);
        }
    }
    
    /**
     * 画像データ（訓練データ）のミニバッチを生成する
     * @return 
     */
    public Matrix createImagesBatchData(){
        return new Matrix(BATCH_SIZE, NUM_PIXELS, imagesList);
    }
    
    /**
     * 正解ラベル（訓練データ）のミニバッチを作成する
     * @return 
     */
    public Matrix createLabelsBatchData(){
        return new Matrix(BATCH_SIZE, NUM_CLASS, labelsList);
    }
    
    /**
     * 文字列で画像データを表示
     * @param index 
     */
    public void showImageDataAsText(int index){
        
        //文字列を操作する部品
        StringBuilder sb = new StringBuilder();
        
        //指定した番号の画像データを行列として取得する
        
        //Matrix X = this.getImagesDataAsMatrix(index);
        Matrix data = this.createImagesBatchData();
        Matrix X = data.getRowVec(index);
        
        System.out.println(X.getShape());
        
        //データの値の大きさによって出力する文字を変える
        for(int i=0; i<SIZE_IMAGE; i++){
            for(int j=0; j<SIZE_IMAGE; j++){
                
                //正規化したデータを復元r
                double value = X.getValue(0, i*SIZE_IMAGE+j)*MAX_PIXELS_VALUE;
                
                //以下のルールに従って、ピクセル値を文字で表現する
                if(value==0.0){
                    sb.append("　");
                }
                else if(value>=1.0 && value<=30){
                    sb.append("□");
                }
                else if(value>=31 && value<=60){
                    sb.append("１");
                }
                else if(value>=61 && value<=90){
                    sb.append("７");
                }
                else if(value>=91 && value<=120){
                    sb.append("３");
                }
                else if(value>=121 && value<=150){
                    sb.append("８");
                }
                else if(value>=151 && value<=180){
                    sb.append("●");
                }
                else{
                    sb.append("■");
                }
            }
            sb.append("\n");
        }
        System.out.println(new String(sb));
        Matrix label = this.createLabelsBatchData();
        Matrix T = label.getRowVec(index);
        System.out.println("Lalbel:"+T);
        //System.out.println("Lalbel:"+this.getLabelsDataAsOneHot(index));
    }
    
    /**
     * 指定した番号の画像データを行列で返す
     * @param index
     * @return 
     */
    public Matrix getImagesDataAsMatrix(int index){
        if(index<0 || index>=numData){
            System.err.println("MnistクラスのgetImageDataAsMatrixメソッドでエラー：インデックスが不正");
            System.exit(0);
        }
        ArrayList<Double> temp = new ArrayList<>();
        for(int n=index*NUM_PIXELS; n<(index+1)*NUM_PIXELS; n++){
            temp.add(imagesList.get(n));
        }
        return new Matrix(1, NUM_PIXELS, temp);
    }
    
    /**
     * ラベルデータを数値で返す
     * @param index
     * @return 
     */
    public int getLabelsData(int index){
        Matrix labelData = getLabelsDataAsOneHot(index);
        return labelData.getIndexOfMaxEement();
    }
    
    /**
     * 正解ラベルをone-hot表現で返す
     * @param index
     * @return 
     */
    public Matrix getLabelsDataAsOneHot(int index){
        /*if(index<0 || index>=numData){
            System.out.println(index);
            System.err.println("MnistクラスのgetLabelsDataAsMatrixメソッドでエラー：インデックスが不正");
            System.exit(0);
        }*/
        ArrayList<Double> oneHot = new ArrayList<>();
        for(int n=index*NUM_CLASS; n<(index+1)*NUM_CLASS; n++){
            oneHot.add(labelsList.get(n));
        }
        return new Matrix(1, NUM_CLASS, oneHot);
        //return temp.indexOf(Collections.max(temp));
    }
    
    /**
     * 訓練データから読み込む画像のインデックスをランダムに取得
     */
    public void getRandomlyIndex(){
        ArrayList<Integer> trainSizeList = new ArrayList<>();
        for(int n=0; n<NUM_TRAIN_DATA; n++){
            trainSizeList.add(n);
        }
        //訓練データの数分のインデックスをシャッフル
        Collections.shuffle(trainSizeList, new Random(seed));
        
        ArrayList<Integer> batchSizeList = new ArrayList<>();
        for(int n=0; n<BATCH_SIZE; n++){
            batchSizeList.add(trainSizeList.get(n));
        }
        //100個のランダムに並んだインデックスをソート
        Collections.sort(batchSizeList);
        
        for(int i=0; i<indexList.length; i++){
            indexList[i] = batchSizeList.get(i);
        }
    }
    
    /**
     * 訓練データから指定したインデックスの画像データを選択するSQL文
     * @return 
     */
    private String createSelectSQL(String tableName){
        StringBuilder sb = new StringBuilder();
        Random rnd = new Random();
        
        sb.append("SELECT * FROM ").append(tableName).append(" WHERE ");
        for(int n=0; n<indexList.length; n++){
            sb.append("n=").append(indexList[n]);
            if(n!=indexList.length-1){
                sb.append(" or ");
            }
            else{
                sb.append(";");
            }
        }
        return new String(sb);
    }
    
    /**
     * データベースからランダムに画像データを読み込む
     */
    private void getImageDataFromDataBase(String tableName){
       Connection con = null;
    
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/mnist", "root", "gyro0615");
            //System.out.println("MySQLに接続しました");
            Statement stmt = con.createStatement();
                
            String sql = createSelectSQL(tableName);
            ResultSet rs = stmt.executeQuery(sql);
                
            while(rs.next()){//取得したレコードの末尾まで以下の処理を繰り返す
                int label = rs.getInt("t");     //ラベルデータを読み込む
                //System.out.println(rs.getInt("n")+"番目のデータを取得");
                double[] oneHot = new double[NUM_CLASS];    //ラベルをベクトル(one-hot)で表す
                oneHot[label] = 1.0;
                for(double value : oneHot){//リストに生成したラベルベクトルの要素を追加
                    labelsList.add(value);
                }
                
                for(int j=2; j<NUM_PIXELS+2; j++){//画素値データを読み込む
                    imagesList.add(rs.getDouble(j)/255);
                }
            }
            stmt.close();
        }
        catch (SQLException ex) {
            System.out.println("MySQLに接続出来ませんでした");
        }
        finally{
            if(con!=null){
                try{
                    con.close();
                    System.out.println("MySQLをクローズしました");
                }
                catch(SQLException ex){
                    System.out.println("MySQLのクローズに失敗しました");
                }
            }
        }
    } 
    
    /**
     * 指定したインデックスの画像データを取得する
     * @param index
     * @return 
     */
    public BufferedImage getImageData(int index){
        ArrayList<Double> temp = new ArrayList<>();
        
        //指定した番号の画像データの画素値をリストに格納する
        for(int i=index*NUM_PIXELS; i<(index+1)*NUM_PIXELS; i++){
            temp.add(imagesList.get(i)*255);
        }
        
        BufferedImage image = new BufferedImage(SIZE_IMAGE, SIZE_IMAGE, BufferedImage.TYPE_INT_RGB);
        
        for(int i=0; i<SIZE_IMAGE; i++){
            for(int j=0; j<SIZE_IMAGE; j++){
                double value = temp.get(i*SIZE_IMAGE + j); //画素値を読み込む
                
                //ビットのシフト演算で、グレースケールとなるように、R部分,G部分,B部分に、同じビットを格納
                int r = (int)value<<16;
                int g = (int)value<<8;
                int b = (int)value;
                
                //ビットのオア演算子で、RGB値を計算
                int rgb = 0xff000000|r|g|b;
                
                //画素値を設定
                image.setRGB(j, i, rgb);
            }
        }
        return image;
    }
    
    
    public static void main(String args[]){
        Mnist mnist = new Mnist("TEST", 0);
        //mnist.getImageDataFromDataBase("test");
        //System.out.println("画像:"+mnist.imagesList.size());
        //System.out.println("正解ラベル:"+mnist.labelsList.size());
        
        mnist.showImageDataAsText(4999);
    }
}
