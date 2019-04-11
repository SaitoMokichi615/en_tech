package main;

import comonent.Matrix;
import comonent.Mnist;
import comonent.NeuralNetwork;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

/**
 *画像認識を行うためのクラス
 * @author MasatoKondo
 */
public class ImageRecognizer {
    private int iteration;
    public static final int END_ITERATION = 10000;
    public static final int NUM_HIDDEN = 100;
    
    private NeuralNetwork net;//ニューラルネットワーク
    private ArrayList<Point2D.Double> accuracyList;//認識率の推移
    private ArrayList<Point2D.Double> lossList;    //誤差の推移
    
    private static final String LOSS_OUTPUT_FILE = "lossTransition-100hiddenN-3Layers-He-AdaGrad.txt";
    private static final String ACCURACY_OUTPUT_FILE = "accuracyTransition-100hiddenN-3Layers-He-AdaGrad.txt";

    /**
     * 画像認識の初期設定
     */
    ImageRecognizer(){
        iteration = 0;
        net = new NeuralNetwork(Mnist.NUM_PIXELS, NUM_HIDDEN, NUM_HIDDEN, Mnist.NUM_CLASS);
        accuracyList = new ArrayList<>();
        lossList = new ArrayList<>();
    }
    
    /**
     * 認識率を計算する
     * @return 
     */
    public double accuracyCount(){
        Mnist testData = new Mnist("TEST", iteration);
        int count = 0;
        
        /*for(int k=0; k<Mnist.NUM_TEST_DATA; k+=Mnist.BATCH_SIZE){
            Mnist testData = new Mnist("TEST", k);
            Matrix X = testData.createImagesBatchData();
            Matrix Y = net.feedForward(X);
            Matrix T = testData.createLabelsBatchData();
            
            for(int n=0; n<Mnist.BATCH_SIZE; n++){
                if(Y.getRowVec(n).getIndexOfMaxEement()==T.getRowVec(n).getIndexOfMaxEement()){
                    count++;
                }
            }
        }*/
        
        //テストデータの数だけ以下の処理を実行
        for(int n=0; n<Mnist.NUM_TEST_DATA; n++){
            
            //テストデータから画像データと正解ラベルを取得
            Matrix X = testData.getImagesDataAsMatrix(n);
            Matrix T = testData.getLabelsDataAsOneHot(n);
            
            //予測値を計算する
            Matrix Y = net.feedForward(X);
            
            //正解ラベルと予測値の最大要素の位置が一致したら、正解数をインクリメント
            if(Y.getIndexOfMaxEement()==T.getIndexOfMaxEement()){
                count++;
            }
        }
        return (double)count/Mnist.NUM_TEST_DATA;
    }
    
    /**
     * ファイルに誤差・認識率の推移を出力する
     * @param fileName 
     */
    private void writeTransition(String fileName){
        ArrayList<Point2D.Double> dataList;
        
        switch(fileName){
            case LOSS_OUTPUT_FILE:
                dataList = lossList;
                break;
                
            case ACCURACY_OUTPUT_FILE:
                dataList = accuracyList;
                break;
                
            default:
                dataList = null;
                System.err.println("ImageRecognizerクラスのwriteTransitionメソッドでエラー：ファイル入出力エラー");
                System.exit(-1);
        }
        
        try(BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fileName)))){
            
            //調整回数と誤差の組から値を取得する
            for(Point2D p : dataList){
                StringBuilder sb = new StringBuilder(); //データ連結
                int iteration = (int) p.getX();         //調整回数を取得
                double loss = p.getY();                 //誤差を取得
                
                //データを連結して書き込む
                sb.append(iteration).append(" ").append(loss).append("\n");
                bw.write(new String(sb));
            }                
        }
        catch (IOException ex) {
            System.err.println("ImageRecognizerクラスのwriteTransitionメソッドでエラー：ファイル入出力エラー");
            System.exit(-1);
        }
    }
    
    /**
     * 学習パラメータを調整する
     */
    public void optimize(){
        int epoch = 0;
        while(iteration<END_ITERATION){
            //イテレーレションと時刻を表示
            Date date = new Date();
            System.out.println(iteration+"回目の学習");
            System.out.println(date);
            
            if(iteration%Mnist.EPOCH==0){//1エポックごとに以下の処理を実行
                
                //認識率を計算する
                double accuracy = accuracyCount();
                System.out.println("認識率:"+accuracy );
                accuracyList.add(new Point2D.Double(epoch, accuracy));
                epoch++;
            }
            
            //入力データ（画像データと正解ラベル）を取得
            Mnist trainData = new Mnist("TRAIN", iteration);
            Matrix X = trainData.createImagesBatchData();
            Matrix T = trainData.createLabelsBatchData();
            
            //順伝搬処理で入力データの予測値を計算
            Matrix Y = net.feedForward(X);
            
            //予測値と正解の誤差を計算
            double loss = net.crossEntropyError(Y, T);
            System.out.println("誤差："+loss+"\n");
            lossList.add(new Point2D.Double(iteration, loss));
            
            //誤差逆伝播法で学習パラメータに関する誤差の勾配を計算
            net.backPropagation(Y, T);
            
            //学習パラメータを更新
            net.AdaGrad();
            
            iteration++;
        }
    }
    
    /**
     * 画像の学習を行う
     * @param argas 
     */
    public static void main(String argas[]){
        ImageRecognizer ir = new ImageRecognizer();
        ir.optimize();
        ir.writeTransition(LOSS_OUTPUT_FILE);
        ir.writeTransition(ACCURACY_OUTPUT_FILE);
    }
}