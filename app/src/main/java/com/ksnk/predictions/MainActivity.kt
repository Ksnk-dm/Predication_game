package com.ksnk.predictions


import android.app.Notification
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.ksnk.predictions.dao.PredicationDao
import org.w3c.dom.Text
import com.ksnk.predictions.entity.Predication as Predication1


class MainActivity : AppCompatActivity(), OnUserEarnedRewardListener {
    private var predicationImageView1: ImageView? = null
    private var predicationImageView2: ImageView? = null
    private var predicationImageView3: ImageView? = null
    private var countTextView: TextView? = null
    private var timerTextView: TextView? = null
    private var count = 0
    private var countTemp = 0


    var statusFirstRun = true
    private var receiver: TimerStatusReceiver? = null


    private val predicationList = listOf(
        "Не жди чуда – чуди сам.",
        "Мир принадлежит тому, кто ему рад.",
        "Поздравляем! Вы находитесь на верном пути.",
        "Покорив одну гору, начинай штурмовать другую...",
        "Прилив энергии поможет Вам справиться с большим объемом незапланированных работ.",
        "Примите то, что вы не можете изменить, и вы будете чувствовать себя лучше.",
        "Природа, время и терпение - три великих врача.", "Пришло время действовать!",
        "Пришло время закончить старое и начать новое.",
        "Пусть мир наполнится спокойствием и доброжелательностью",
        "Работа с новыми партнерами будет очень выгодным.",
        "Работайте над дипломатическими способностями - они очень пригодятся для реализации идей.",
        "Размышляйте и не спешите с действиями.",
        "Разрешите состраданию направлять ваши решения.",
        "Результат Ваших действий может оказаться неожиданным.",
        "Романтика переместит вас в новом направлении.",
        "С этого момента ваша доброта приведет вас к успеху.",
        "Сегодня у вас будет красивый день.",
        "Семь раз отмерьте, один раз отрежьте!",
        "Слушайте каждого. Идеи приходят отовсюду.",
        "Соловья баснями не кормят.",
        "Сосредоточьтесь на семье и гармонии с окружающим миром.",
        "Счастливая жизнь прямо перед вами.",
        "Теперь настало время попробовать что-то новое.",
        "Терпение! Вы почти у цели.",
        "Тот, кто знает,  достаточно богат.",
        "Тот, кто не ждет благодарности, никогда не будет разочарован.",
        "Удача проводит Вас через все трудные времена.",
        "Пусть мир наполнится спокойствием и доброжелательностью",
        "Пусть мир наполнится спокойствием и доброжелательностью",
        "Пусть мир наполнится спокойствием и доброжелательностью"

    )

    private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    private fun initFirstStart(
        userDao: PredicationDao,
        statusFirstRun: Boolean,
        editor: SharedPreferences.Editor
    ) {
        if (statusFirstRun) {
            for (i in predicationList.indices) {
                val predication = Predication1(predicationList.get(i))
                userDao.insertAll(predication)
            }
            editor.putBoolean("firstStart", false).apply()
        }
    }

    private fun checkNullSizeList(userDao: PredicationDao, editor: SharedPreferences.Editor) {
        if (userDao.getAll().size < 3) {
            initFirstStart(userDao, true, editor)
        }
    }

    private fun checkNullCount(editor: SharedPreferences.Editor) {
        if (count == 0) {
            count = 1
            editor.putInt("count", count).apply()
            predicationImageView1?.visibility = View.VISIBLE
            countTextView?.text = count.toString()
        }
    }

    fun loadAd() {
        if (rewardedInterstitialAd == null) {
            val adRequest = AdManagerAdRequest.Builder().build()

            // Load an ad.
            RewardedInterstitialAd.load(
                this,
                "ca-app-pub-2981423664535117/9800122375",
                adRequest,
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        super.onAdFailedToLoad(adError)
                        rewardedInterstitialAd = null
                    }

                    override fun onAdLoaded(rewardedAd: RewardedInterstitialAd) {
                        super.onAdLoaded(rewardedAd)
                        rewardedInterstitialAd = rewardedAd
                    }
                })
        }
    }

    private fun showAdsDialog() {
        showAdNow()
    }

    private fun showAdNow() {
        rewardedInterstitialAd!!.show(this@MainActivity, this@MainActivity)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        MobileAds.initialize(this) { initializationStatus -> loadAd() }

        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        val db: AppDataBase? = App.instance?.getDatabase()
        val userDao: PredicationDao = db!!.predicationDao()
        statusFirstRun = sharedPreference.getBoolean("firstStart", true)
        count = sharedPreference.getInt("count", 3)


        initFirstStart(userDao, statusFirstRun, editor)



        countTextView = findViewById(R.id.countTextView)
        timerTextView = findViewById(R.id.timer_text_view)
        countTextView?.text = count.toString()
        countTextView?.setOnClickListener {
            rewardedInterstitialAd?.show(
                this
            ) { rewardItem ->
                checkNullCount(editor)
            }
        }
        receiver = TimerStatusReceiver(timerTextView!!)

        predicationImageView1 = findViewById(R.id.predictionImageView1)
        predicationImageView1?.setOnClickListener {
            createAlertDialog(userDao, editor)
            count--
            editor.putInt("count", count).apply()
            countTextView?.text = count.toString()
            predicationImageView1?.visibility = View.INVISIBLE
            if(count==0){
                startService()
            }

        }
        predicationImageView2 = findViewById(R.id.predictionImageView2)
        predicationImageView2?.setOnClickListener {
            createAlertDialog(userDao, editor)
            count--
            editor.putInt("count", count).apply()
            countTextView?.text = count.toString()
            predicationImageView2?.visibility = View.INVISIBLE
            if(count==0){
                startService()
            }

        }
        predicationImageView3 = findViewById(R.id.predictionImageView3)
        predicationImageView3?.setOnClickListener {
            createAlertDialog(userDao, editor)
            count--
            editor.putInt("count", count).apply()
            countTextView?.text = count.toString()
            predicationImageView3?.visibility = View.INVISIBLE
            if(count==0){
                startService()
            }

        }
        if (count == 2) {
            predicationImageView1?.visibility = View.INVISIBLE
            stopService()
        }
        if (count == 1) {
            predicationImageView1?.visibility = View.INVISIBLE
            predicationImageView2?.visibility = View.INVISIBLE
            predicationImageView3?.visibility = View.VISIBLE
            stopService()
        }
        if (count == 0) {
            predicationImageView1?.visibility = View.INVISIBLE
            predicationImageView2?.visibility = View.INVISIBLE
            predicationImageView3?.visibility = View.INVISIBLE

        }

    }

    override fun onResume() {
        super.onResume()
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(receiver!!, IntentFilter(CountdownTimerService.TIME_INFO))
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver!!)
    }

    private fun startService() {
        val intent = Intent(this, CountdownTimerService::class.java)
        startService(intent)

    }

    private fun stopService() {
        val intent = Intent(this, CountdownTimerService::class.java)
        stopService(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putInt("count", count).apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        editor.putInt("count", count).apply()
    }

    private fun createAlertDialog(userDao: PredicationDao, editor: SharedPreferences.Editor) {
        var size = userDao.getAll().size
        val rnds = (0 until size).random() // generated random from 0 to 10 included
        var id = 0
        checkNullSizeList(userDao, editor)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Предсказание на сегодня")
        builder.setMessage(userDao.getAll()[rnds].predicationText)
        id = userDao.getAll()[rnds].uid
        builder.setNegativeButton(android.R.string.no) { dialog, which ->
        }
        userDao.deleteById(id)
        builder.show()
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        checkAdsReward()
    }

    private fun checkAdsReward() {
        if (count == 0) {
            count = 1
            predicationImageView1?.visibility = View.VISIBLE
            stopService()
        }
    }
}