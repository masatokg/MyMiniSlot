package jp.ac.asojuku.st.myminislot

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    // 持ち金と掛け金の初期値を定数宣言
    val MY_MONEY_DEF:Int = 1000;
    val BET_MONEY_DEF:Int = 10;

    // 持ち金と掛け金の値を保持するクラス内変数を用意
    var m_myMoney:Int = MY_MONEY_DEF;
    var m_betMoney:Int = BET_MONEY_DEF;

    // クラスインスタンス生成のライフサイクルイベント反応メソッド
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    // 画面表示・再表示のライフサイクルイベント反応メソッド
    override fun onResume() {
        super.onResume()

        // 共有プリファレンスのインスタンス取得
        val pref = PreferenceManager.getDefaultSharedPreferences(this);
        // 共有プリファレンスから手持金額取得
        m_myMoney = pref.getInt("MY_MONEY",MY_MONEY_DEF);

        // 手持ち金額を画面表示
        txvMoneyValue.setText(m_myMoney.toString());
        // 掛け金額を画面表示
        if(m_betMoney>m_myMoney){
            // 掛け金額が手持ち金額より少ない場合、掛け金は初期値を表示
            m_betMoney = BET_MONEY_DEF;
        }
        txvBetValue.setText(m_betMoney.toString());

        // 各ボタンにイベントリスナーを設定してゆく

        // スタートボタンを押されたら、onStartBtnClick()メソッドを実行（引数itはボタンviewインスタンス）
        btnStart.setOnClickListener { this.onStartBtnClick(it); }

        // UPボタンを押されたら、{}ブロック内の処理を実行（掛け金を更新して画面表示を変更）
        btnUp.setOnClickListener{
            if(m_betMoney+10<=m_myMoney) { m_betMoney+=10;txvBetValue.setText(m_betMoney.toString()); }
        }
        // DOWNボタンを押されたら、{}ブロック内の処理を実行（掛け金を更新して画面表示を変更）
        btnDown.setOnClickListener{
            if(m_betMoney-10>0) { m_betMoney-=10;txvBetValue.setText(m_betMoney.toString()); }
        }

        // リセットボタンを押されたら、onResetBtnClick()メソッドを実行（引数itはボタンviewインスタンス）
        btnReset.setOnClickListener { this.onResetBtnClick(); }
    }

    /**
     * スタートボタンのクリックイベントに反応させるリスナーコールバックメソッド
     */
    private fun onStartBtnClick(view: View?){
        // 持ち金が0以下だったら何もしない
        if(m_myMoney<=0){
            // メッセージを表示
            Toast.makeText(this, "お金がなくなりました", Toast.LENGTH_SHORT).show();
            return;
        }
        // インテントを生成、Extra（おまけ）情報として"BET_MONEY"をキーに値を追加登録する
        val intent = Intent(this, GameActivity::class.java);
        intent.putExtra("BET_MONEY", m_betMoney);
        // Intentで指定した行き先画面を起動
        startActivity(intent);
    }

    /**
     * リセットボタンのクリックイベントに反応させるリスナーコールバックメソッド
     */
    private fun onResetBtnClick(){
        // メモリ上のお金を初期化
        m_myMoney = MY_MONEY_DEF;
        m_betMoney = BET_MONEY_DEF;

        // 共有プリファレンス（ファイル）上のお金も初期化
        val pref = PreferenceManager.getDefaultSharedPreferences(this);
        // 共有プリファレンスから手持金額取得
        val editor = pref.edit();
        editor.putInt("MY_MONEY", m_myMoney).apply();

        // 画面表示の金額も初期化する
        txvMoneyValue.setText(m_myMoney.toString());
        txvBetValue.setText(m_betMoney.toString());
    }
}
