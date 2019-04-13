package boids;

import javafx.scene.shape.Rectangle;

/**
 * 人工生命
 * @author MasatoKondo
 */
class Boid{
    FieldPanel fp;
    int x, y;
    double dx = 1.0;
    double dy = 1.0;
    Rectangle shape = new Rectangle(3,3);
    
    static double COHESION_RATE = 0.01;
    static double SEPARATION_DIS = 10;
    static double ALIGNMENT_RATE = 0.5;
    static int SPEED_LIMIT = 8;
    
    /**
     * 初期化処理
     * @param fp 
     */
    public Boid(FieldPanel fp){
        this.fp = fp;
        fp.shapes.add(shape);
        x = (int)(FieldPanel.WIDTH/2 + (FieldPanel.INIT_APEAR_RANGE*Math.random()-0.5));
        y = (int)(FieldPanel.HEIGHT/2 + (FieldPanel.INIT_APEAR_RANGE*Math.random()-0.5));    
    }
    
    /**
     * 移動量決定処理
     */
    public void moveDecide(){
        cohesion();
        separation();
        alignment();
        double rate = Math.sqrt(dx*dx+dy*dy)/SPEED_LIMIT;
        
        if(rate > 1.0){
            dx /= rate;
            dy /= rate;
        }
        //if(Math.random()<0.01){dx = (int)(Math.random()*2.0+0.5)-1;}
        //if(Math.random()<0.01){dy = (int)(Math.random()*2.0+0.5)-1;}
    }
    
    /**
     * 群れの中心に向かう（集約）
     */
    void cohesion(){
        double cx = 0;
        double cy = 0;
        
        for(Boid b : fp.boids){
            cx += b.x;
            cy += b.y;
        }
        cx /= FieldPanel.NUM_BOIDS;
        cy /= FieldPanel.NUM_BOIDS;
        
        dx += (cx-x)*COHESION_RATE;
        dy += (cy-y)*COHESION_RATE;
    }
    
    /**
     * ぶつからないように距離をとる（分離）
     */
    void separation(){
        for(Boid b : fp.boids){
            if(b !=this){
                double ax = b.x-x;
                double ay = b.y-y;
                double dis = Math.sqrt(ax*ax + ay*ay);
                
                if(dis<SEPARATION_DIS){
                    dx -= ax;
                    dy -= ay;                   
                }
            }
        }
    }
    
    /**
     * 群れと同じ方向と速度に合わせる（整列）
     */
    void alignment(){
        double ax = 0;
        double ay = 0;
        
        for(Boid b : fp.boids){
            ax += b.dx;
            ay += b.dy;
        }
        ax /= FieldPanel.NUM_BOIDS;
        ay /= FieldPanel.NUM_BOIDS;
        
        dx += (ax-dx)*ALIGNMENT_RATE;
        dy += (ay-dy)*ALIGNMENT_RATE;
    }
    
    /**
     * 移動処理
     */
    public void move(){
        x += dx;
        y += dy;
        
        if(x<0 || x>=FieldPanel.WIDTH){
            dx = -dx;
            x += dx*2;
        }
        if(y<0 || y>=FieldPanel.HEIGHT){
            dy = -dy;
            y += dy*2;
        }
    }
    
    /**
     * 描画
     */
    public void draw(){
        shape.setX(x-5);
        shape.setY(y-5);
    }
}
