package game;

/**
 *
 * @author Masato
 */
public class ActionKey {
    
    //押されている間、キー入力は有効（移動に用いる）
    public static final int NORMAL = 0;//通常
    
    //一度の入力のみ受け付ける（ジャンプに用いる）
    public static final int DETECT_INITIAL_PRESS_ONLY = 1;
    
    //キーの状態
    private static final int STATE_RELEASED = 0;    //キーが離された状態
    private static final int STATE_PRESSED = 1;     //キーが押された状態
    private static final int STATE_WAITING_FOR_RELEASE = 2; //キーが離されるのを待っている状態
    
    private int mode;   //キーのモード
    private int amount; //キーが押された回数
    private int count;  //キーのカウント
    private int state;  //キーの状態
    
    /**
     * モードを指定してキーを初期化
     * @param mode 
     */
    public ActionKey(int mode){
        this.mode = mode;
        reset();
    }
    
    /**
     * キーの状態をリセット（押されていない状態）
     */
    public void reset(){
        state = STATE_RELEASED;
        amount = 0;
        count = amount;
    }
    
    /**
     * キーが押された時の処理
     */
    public void press(){
        //「STATE_WAITING_FOR_RELEASE」状態では、キー入力は無効
        if(state != STATE_WAITING_FOR_RELEASE){
            state = STATE_PRESSED;
        }
    }
    
    private void incAmount(){
        if(state==STATE_PRESSED){
            amount++;
            count++;
        }
    }
    
    /**
     * キーが離された時の処理
     */
    public void release(){
        state = STATE_RELEASED;
    }
    
    /**
     * キーが押されているかどうかの判定
     * @return 
     */
    public boolean isPressed(){
        incAmount();
        if(amount!=0){
            if(state==STATE_RELEASED){//キーが離されたら、キーカウントをリセット
                amount=0;
            }
            else if(mode==DETECT_INITIAL_PRESS_ONLY){//一階の入力のみ有効の場合
                state = STATE_WAITING_FOR_RELEASE;//キー状態を「離し待ち状態」に（この状態のとき、キーは押されていることにならない）
                amount = 0;
            }
            return true;
        }
        return false;
    }
    
    public int getCount(){
        return count;
    }
    
    public String getState(){
        switch(state){
            case STATE_PRESSED:
                return "STATE_PRESSED";
                
            case STATE_WAITING_FOR_RELEASE:
                return "STATE_WAITING_FOR_RELEASE";
                
            default:
                return "STATE_RELEASED";
        }
    }
}
