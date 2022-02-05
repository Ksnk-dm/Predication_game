package com.ksnk.predictions.activity


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
import com.ksnk.predictions.Contains
import com.ksnk.predictions.R
import com.ksnk.predictions.base.App
import com.ksnk.predictions.base.AppDataBase
import com.ksnk.predictions.dao.PredicationDao
import com.ksnk.predictions.entity.Predication as Predication1


class MainActivity : AppCompatActivity(), OnUserEarnedRewardListener {
    private val firstStart = "firstStart"
    private val preferenceName = "preference_base"
    private val rewardAdsId = "ca-app-pub-3940256099942544/5354046379"
    private val interestingAdsId = "ca-app-pub-3940256099942544/1033173712"
    private var predicationImageView1: ImageView? = null
    private var predicationImageView2: ImageView? = null
    private var predicationImageView3: ImageView? = null
    private var countTextView: TextView? = null
    private var plusTextView: TextView? = null
    private var count = 3
    private var mInterstitialAd: InterstitialAd? = null
    private var sharedPreferences: SharedPreferences? = null
    private var editorPreferences: SharedPreferences.Editor? = null
    lateinit var mAdView: AdView
    private var statusFirstRun = true
    private var predicationDao: PredicationDao? = null
    private var rewardedInterstitialAd: RewardedInterstitialAd? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFullScreen()
        setContentView(R.layout.activity_main)
        init()
        initSharedPrefs()
        initAds()
        loadHomeBanner()
        loadDb()
        checkFirstStartFlag()
        setListeners()
        interestingLoadAds()
    }

    private fun initFirstStart() {
        if (statusFirstRun) {
            for (i in Contains.predicationList.indices) {
                val predication = Predication1(Contains.predicationList[i])
                predicationDao?.insertAll(predication)
            }
            editorPreferences?.putBoolean(firstStart, false)?.apply()
        }
    }

    private fun checkNullSizeList() {
        if (predicationDao?.getAll()?.size!! < 3) {
            statusFirstRun = true
            initFirstStart()

        }
    }

    private fun checkNullCount() {
        if (count == 0) {
            count = 1
            predicationImageView1?.visibility = View.VISIBLE
            countTextView?.text = count.toString()
            plusTextView?.visibility = View.INVISIBLE
        }
    }

    private fun loadAd() {
        if (rewardedInterstitialAd == null) {
            val adRequest = AdManagerAdRequest.Builder().build()
            RewardedInterstitialAd.load(
                this,
                rewardAdsId,
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

    private fun init() {
        countTextView = findViewById(R.id.countTextView)
        countTextView?.text = count.toString()
        plusTextView = findViewById(R.id.plus_text_view)
        predicationImageView1 = findViewById(R.id.predictionImageView1)
        predicationImageView2 = findViewById(R.id.predictionImageView2)
        predicationImageView3 = findViewById(R.id.predictionImageView3)
        mAdView = findViewById(R.id.adView)
    }

    private fun initSharedPrefs() {
        sharedPreferences = getSharedPreferences(preferenceName, Context.MODE_PRIVATE)
        editorPreferences = sharedPreferences?.edit()
    }

    private fun initAds() {
        MobileAds.initialize(this) { loadAd() }
    }

    private fun setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
    }

    private fun loadHomeBanner() {
        val adRequestBanner = AdRequest.Builder().build()
        mAdView.loadAd(adRequestBanner)
    }

    private fun loadDb() {
        val db: AppDataBase? = App.instance?.getDatabase()
        predicationDao = db!!.predicationDao()
    }

    private fun checkFirstStartFlag() {
        statusFirstRun = sharedPreferences!!.getBoolean(firstStart, true)
        initFirstStart()
    }

    private fun setListeners() {
        plusTextView?.setOnClickListener {
            rewardedInterstitialAd?.show(this) {
                checkNullCount()
            }
        }
        predicationImageView1?.setOnClickListener {
            createAlertDialog()
            count--
            countTextView?.text = count.toString()
            predicationImageView1?.visibility = View.INVISIBLE
            showPlusTextView()

        }

        predicationImageView2?.setOnClickListener {
            createAlertDialog()
            count--
            countTextView?.text = count.toString()
            predicationImageView2?.visibility = View.INVISIBLE
            showPlusTextView()

        }

        predicationImageView3?.setOnClickListener {
            createAlertDialog()
            count--
            countTextView?.text = count.toString()
            predicationImageView3?.visibility = View.INVISIBLE
            showPlusTextView()
        }
    }

    private fun interestingLoadAds() {
        var adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            this,
            interestingAdsId,
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    mInterstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    mInterstitialAd = interstitialAd
                }
            })
    }

    private fun showPlusTextView() {
        if (count == 0) {
            plusTextView?.visibility = View.VISIBLE
        }
    }

    private fun showAdsInteresting() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.show(this)
        } else {
            Log.d("TAG", "The interstitial ad wasn't ready yet.")
        }
    }

    private fun createAlertDialog() {
        var size = predicationDao?.getAll()?.size
        val random = (0 until size!!).random()
        var id = 0
        checkNullSizeList()
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.home_text))
        builder.setMessage(predicationDao!!.getAll()[random].predicationText)
        id = predicationDao!!.getAll()[random].uid
        builder.setNegativeButton(getString(R.string.close)) { dialog, which ->
            showAdsInteresting()
        }
        builder.setOnCancelListener {
            showAdsInteresting()
        }
        predicationDao!!.deleteById(id)
        builder.show()
    }

    override fun onUserEarnedReward(p0: RewardItem) {
        checkAdsReward()
    }

    private fun checkAdsReward() {
        if (count == 0) {
            count = 1
            predicationImageView1?.visibility = View.VISIBLE
            plusTextView?.visibility = View.INVISIBLE
        }
    }
}