package com.ksnk.predictions


import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.ksnk.predictions.dao.PredicationDao
import com.ksnk.predictions.entity.Predication as Predication1


class MainActivity : AppCompatActivity(), OnUserEarnedRewardListener {
    private var predicationImageView1: ImageView? = null
    private var predicationImageView2: ImageView? = null
    private var predicationImageView3: ImageView? = null
    private var countTextView: TextView? = null
    private var plusTextView: TextView? = null
    private var count = 3
    private var countTemp = 0
    private var mInterstitialAd: InterstitialAd? = null
    private final var TAG = "MainActivity"
    lateinit var mAdView : AdView
    var statusFirstRun = true


    private var rewardedInterstitialAd: RewardedInterstitialAd? = null

    private fun initFirstStart(
        userDao: PredicationDao,
        statusFirstRun: Boolean,
        editor: SharedPreferences.Editor
    ) {
        if (statusFirstRun) {
            for (i in Contains.predicationList.indices) {
                val predication = Predication1(Contains.predicationList[i])
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

    private fun checkNullCount() {
        if (count == 0) {
            count = 1
            predicationImageView1?.visibility = View.VISIBLE
            countTextView?.text = count.toString()
            plusTextView?.visibility=View.INVISIBLE
        }
    }

    fun loadAd() {
        if (rewardedInterstitialAd == null) {
            val adRequest = AdManagerAdRequest.Builder().build()

            // Load an ad.
            RewardedInterstitialAd.load(
                this,
                "ca-app-pub-3940256099942544/5354046379",
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

    override fun onPause() {
        super.onPause()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main)




        MobileAds.initialize(this) { initializationStatus -> loadAd() }
        mAdView = findViewById(R.id.adView)
        val adRequestBanner = AdRequest.Builder().build()
        mAdView.loadAd(adRequestBanner)
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        var editor = sharedPreference.edit()
        val db: AppDataBase? = App.instance?.getDatabase()
        val userDao: PredicationDao = db!!.predicationDao()
        statusFirstRun = sharedPreference.getBoolean("firstStart", true)
      //  count = sharedPreference.getInt("count", 3)
Log.d("counter", count.toString())

        initFirstStart(userDao, statusFirstRun, editor)
        var adRequest = AdRequest.Builder().build()

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712", adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError?.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")
                mInterstitialAd = interstitialAd
            }
        })


        countTextView = findViewById(R.id.countTextView)
        plusTextView = findViewById(R.id.plus_text_view)
        countTextView?.text = count.toString()
        plusTextView ?.setOnClickListener {
            rewardedInterstitialAd?.show(this) {
                checkNullCount()
            }
        }


        predicationImageView1 = findViewById(R.id.predictionImageView1)
        predicationImageView1?.setOnClickListener {
            createAlertDialog(userDao, editor)
            count--
            countTextView?.text = count.toString()
            predicationImageView1?.visibility = View.INVISIBLE
            if(count==0){
                plusTextView?.visibility = View.VISIBLE
            }

        }
        predicationImageView2 = findViewById(R.id.predictionImageView2)
        predicationImageView2?.setOnClickListener {
            createAlertDialog(userDao, editor)
            count--
            countTextView?.text = count.toString()
            predicationImageView2?.visibility = View.INVISIBLE
            if(count==0){
                plusTextView?.visibility = View.VISIBLE
            }

        }
        predicationImageView3 = findViewById(R.id.predictionImageView3)
        predicationImageView3?.setOnClickListener {
            createAlertDialog(userDao, editor)
            count--
            countTextView?.text = count.toString()
            predicationImageView3?.visibility = View.INVISIBLE
            if(count==0){
                plusTextView?.visibility = View.VISIBLE
            }

        }
        if (count == 2) {
            predicationImageView1?.visibility = View.INVISIBLE
        }
        if (count == 1) {
            predicationImageView1?.visibility = View.INVISIBLE
            predicationImageView2?.visibility = View.INVISIBLE
            predicationImageView3?.visibility = View.VISIBLE
        }
        if (count == 0) {
            predicationImageView1?.visibility = View.INVISIBLE
            predicationImageView2?.visibility = View.INVISIBLE
            predicationImageView3?.visibility = View.INVISIBLE
            plusTextView?.visibility = View.VISIBLE
        }
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
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
        }
        builder.setOnCancelListener {
            if (mInterstitialAd != null) {
                mInterstitialAd?.show(this)
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.")
            }
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
            plusTextView?.visibility=View.INVISIBLE
        }
    }
}