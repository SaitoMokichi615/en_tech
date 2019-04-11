package main;

import comonent.Mnist;
import comonent.MultiLayersNet;

/**
 *レイヤを利用した画像認識
 * @author MasatoKondo
 */
public class ImageRecognizerExtend {
    
    private MultiLayersNet mn;  //ニューラルネットワーク
    private int iteration;      //パラメータ調整回数
    
    private static final int MAX_ITERATION = 10000;
    
    /**
     * 初期設定
     */
    public ImageRecognizerExtend(){
        //ニューラルネットワークを構成する
        mn = new MultiLayersNet(Mnist.NUM_PIXELS, ImageRecognizer.NUM_HIDDEN, Mnist.NUM_CLASS);
        iteration = 0;
    }
    
    
    public void opitimize(){
        
        while(iteration<MAX_ITERATION){
            mn.setInputBatchData(iteration);
            mn.feedForward();
            mn.backPropagation();
            mn.updateParameter();
        }
    }

}
