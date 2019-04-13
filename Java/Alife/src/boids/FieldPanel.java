package boids;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 *人工生命のフィールド
 * @author MasatoKondo
 */
public class FieldPanel extends Application {
    public static final int WIDTH = 640;
    public static final int HEIGHT = 480;
    public static final int NUM_BOIDS = 150;
    static final int INIT_APEAR_RANGE = 100;
    
    Pane pane = new Pane();
    ObservableList<Node> shapes = pane.getChildren();
    Boid[] boids = new Boid[NUM_BOIDS];
    boolean active = true;
    
    
    @Override
    public void init(){
        for(int i=0; i<NUM_BOIDS; i++){
            boids[i] = new Boid(this);
        }
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setScene(new Scene(pane, WIDTH, HEIGHT));
        primaryStage.show();
        
        //人工生命を動かす
        new Thread(){
            @Override
            public void run(){
                while(active){
                    for(Boid b : boids){
                        b.moveDecide();
                        b.move();
                    }
                    try{
                        Thread.sleep(20);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(FieldPanel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }.start();
        
        //描画タイマー
        new AnimationTimer(){
            @Override
            public void handle(long now) {
                for(Boid b : boids){
                    b.draw();
                }
            }
            
        }.start();
    }
    
    @Override
    public void stop(){
        active = false;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}