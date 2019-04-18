package ca;

/**
 *セルオートマトン
 * @author MasatoKondo
 */
public class CellAutomaton {
    private int n;              //セルのサイズ
    private int[] cells;        //現在のセルの状態を保持する配列
    private int[] ruluMap;      //次のセルの状態を決める規則(ビット列)
    
    /**
     * 初期設定
     * @param n
     * @param rule 
     */
    public CellAutomaton(int n, int rule){
        this.n = n;
        cells = new int[n];
        ruluMap = new int[8];
        cells[n/2]=1;
        setRule(rule);   
    }
    
    /**
     * ルールをセットする
     * @param rule 
     */
    public void setRule(int rule){
        
        //引数を8で割ったあまりを計算し、計算した結果をビット列に変換
        int num = rule%256;       
        for(int i=0; num>0; i++){
            ruluMap[i] = num%2;
            num /= 2;
        }
    }
    
    /**
     * セルの状態を更新する
     */
    public void updataCell(){
        int[] dummy = new int[n];
        
        for(int i=0; i<n; i++){
            
            int left = (i-1+n)%n;   //左側のセル
            int right = (i+1+n)%n;  //右側のセル
            
            //ルール番号のインデックスを求める（対象となる3つのセルに格納された3ビット列を10進数に変換）
            int k = 4*cells[left]+2*cells[i]+cells[right];
            
            //ダミー配列に次の状態
            dummy[i] = ruluMap[k];
        }
        
        //ダミー配列の値を、セルにコピー（状態を更新）
        cells = dummy.clone();
    }
    
    /**
     * 現在のセルの状態を返す
     * @return 
     */
    public int[] getCells(){
        return cells;
    }
}
