package jp.ac.asojuku.st.myminislot

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
import java.util.*

class GameActivity : AppCompatActivity() {

    // 値の初期値用に値を定数宣言。通常あり得ない値を設定しておくと、初期化漏れに気付きやすい。
    val NONE_VALUE:Int = -1;
    // 3つのスロット用の変数を定義しておく。中身の数値がそれぞれどの役が選択されたかを表す
    var m_btnSlots:Array<Int> = arrayOf( NONE_VALUE, NONE_VALUE, NONE_VALUE);
    // 持ち金と掛け金のクラス内変数を初期化
    var m_myMoney = NONE_VALUE;
    var m_betMoney = NONE_VALUE;

    // クラス内部クラスとしてenum型を一つ定義する。このenumが役を表す。
    // 例えば「バナナ」は0番、画像idは「R.drawable.banana」ということを保持する
    private enum class Yaku(val yakuNum:Int, val imageId:Int){
        BANANA(0,R.drawable.banana),
        BAR(1,R.drawable.bar),
        BIG_WIN(2, R.drawable.bigwin),
        CHERRY(3, R.drawable.cherry),
        GRAPE(4, R.drawable.grape),
        LEMON(5, R.drawable.lemon),
        ORANGE(6, R.drawable.orange),
        SEVEN(7, R.drawable.seven),
        WALTER_MELON(8,R.drawable.waltermelon);
    }

    // クラスインスタンス生成のライフサイクルイベント反応メソッド
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
    }

    // 画面表示・再表示のライフサイクルイベント反応メソッド
    override fun onResume() {
        super.onResume();

        // 共有プリファレンスのインスタンス取得
        val pref = PreferenceManager.getDefaultSharedPreferences(this);
        // 共有プリファレンスから手持金額取得して画面表示テキストビューに設定
        m_myMoney = pref.getInt("MY_MONEY",1000);
        txvMyValueGame.setText(m_myMoney.toString());

        // Intentから掛け金額取得して画面表示テキストビューに設定
        m_betMoney = intent.getIntExtra("BET_MONEY", -1);
        txvBetValueGame.setText(m_betMoney.toString());

        // ボタンクリックに反応するコールバックリスナーメソッドを設定。
        // 第一引数はImageButtonのインスタンス、第二引数は配列 m_btnSlots で使うインデックス
        btnSlot1.setOnClickListener { onSlotClicked( btnSlot1, 0); }
        btnSlot2.setOnClickListener { onSlotClicked( btnSlot2, 1); }
        btnSlot3.setOnClickListener { onSlotClicked( btnSlot3, 2); }

        // 戻るボタン (この画面を破棄する)
        btnBack.setOnClickListener { finish() }

        // 応援人物画像を初期表示で設定
        img_mainImage.setImageResource(R.drawable.face_genki);
    }

    /**
     * スロットがクリックされて反応するコールバックリスナーメソッド
     * 第一引数はクリックされたボタンのViewインスタンス、第二引数は m_btnSlots で使うインデックス
     */
    private fun onSlotClicked(imageButton: ImageButton, idx:Int){

        // 該当ボタンを表すm_btnSlots 配列の設定値が未選択値 NONE_VALUE（-1）ではない場合何もしない
        if(m_btnSlots[idx] != NONE_VALUE){
            return;
        }
        // 役に対応する乱数値を取得（例： 0ならバナナ）
        val randIdx = Random().nextInt(Yaku.values().size);
        // 取得した乱数値に対応する役の画像IDをenum（Yaku）を基に取得して設定
        val resourceId:Int = Yaku.values()[randIdx].imageId;
        imageButton.setImageResource(resourceId);

        // m_btnSlots 配列の指定した場所に乱数値を役番号として設定
        m_btnSlots[idx] = randIdx;

        // m_btnSlots 配列に未選択値 NONE_VALUE（-1）が残っていない（全部選択した）場合、
        // ゲーム結果を判定、算出、結果表示
        if( !m_btnSlots.contains(NONE_VALUE) ){
            this.calcPoints()
        }
    }

    /**
     * ゲーム結果を判定し、スコアを算出、共有プリファレンスと画面表示に反映する
     */
    private fun calcPoints() {

        // 結果人物画像IDの変数を準備
        var faceId:Int = NONE_VALUE;
        // 倍率用の変数を準備
        var magnification:Int = NONE_VALUE;

        // 掛け金を引く
        m_myMoney -= m_betMoney;

        // 役番号確認表示
        // Toast.makeText(this, m_btnSlots[0].toString() + m_btnSlots[1].toString() + m_btnSlots[2].toString(), Toast.LENGTH_SHORT).show();

        // 条件ごとに結果の倍率を設定する
        // 3つ揃いの場合
        if( (m_btnSlots[0] == m_btnSlots[1])
            && (m_btnSlots[0] == m_btnSlots[2])) {

            if (m_btnSlots[0] == Yaku.SEVEN.yakuNum) {
                // 7の役(20倍)
                magnification = 20;
            }
            else if(m_btnSlots[0] == Yaku.BIG_WIN.yakuNum){
                // BIG_WINの役(10倍)
                magnification = 10;
            }
            else if(m_btnSlots[0] == Yaku.BAR.yakuNum){
                // BARの役(5倍)
                magnification = 5;
            }
            else{
                // その他の役(1倍)
                magnification = 1;
            }
        }
        else if(m_btnSlots[0] == m_btnSlots[1]
            || m_btnSlots[0] == m_btnSlots[2]
            || m_btnSlots[1] == m_btnSlots[2]){
            // 2つ揃いの場合

            if( m_btnSlots.count { it==Yaku.SEVEN.yakuNum } == 2) {
                // ７が２つあった(3倍)
                magnification = 3;
            }else{
                // その他が２つあった(1倍)
                magnification = 1;
            }
        }
        else if(m_btnSlots.contains(Yaku.WALTER_MELON.yakuNum)) {
            // スイカを１つでも含んでいた場合(1倍)
            magnification = 1;
        }
        else if(m_btnSlots[0] == Yaku.ORANGE.yakuNum
            && m_btnSlots[1] == Yaku.CHERRY.yakuNum
            && m_btnSlots[2] == Yaku.LEMON.yakuNum){
            // 左からオレンジ、チェリー、レモンの場合(30倍)
            magnification = 30;

        }
        else if(m_btnSlots[0] == Yaku.WALTER_MELON.yakuNum
            && m_btnSlots[1] == Yaku.BANANA.yakuNum
            && m_btnSlots[2] == Yaku.GRAPE.yakuNum){
            // 左からスイカ、バナナ、グレープの場合(10倍)
            magnification = 10;
        }

        // 設定した倍率を換算した結果持ち金にプラス
        if(magnification!=NONE_VALUE) { m_myMoney += (m_betMoney*magnification); }

        // 勝ち具合によって人物の表情とメッセージを変える
        when(magnification){
            in 11..100 -> { img_mainImage.setImageResource(R.drawable.face_yorokobi); txvMainMsg.setText(R.string.strHyaha); }
            in 3..10 -> { img_mainImage.setImageResource(R.drawable.face_smile); txvMainMsg.setText(R.string.strMaamaa); }
            1, 2 -> { img_mainImage.setImageResource(R.drawable.face_genki); txvMainMsg.setText(R.string.strAbuna); }
            else -> { img_mainImage.setImageResource(R.drawable.face_ouchi); txvMainMsg.setText(R.string.strYarareta); }
        }

        // 結果を反映
        this.applyResult();
    }

    /**
     * 結果金額を画面表示と共有プリファレンスに保存する
     */
    private fun applyResult(){
        // 持ち金表示を更新
        txvMyValueGame.text=m_myMoney.toString();
        // 共有プリファレンス取得
        val pref = PreferenceManager.getDefaultSharedPreferences(this);
        // 更新エディターを取得
        val editor = pref.edit();
        // 値を設定（put）して適用（apply）
        editor.putInt("MY_MONEY", m_myMoney).apply();
    }
}
