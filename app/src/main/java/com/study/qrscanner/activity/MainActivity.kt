package com.study.qrscanner.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.study.qrscanner.utils.AppEvent
import com.study.qrscanner.R
import com.study.qrscanner.databinding.ActivityMainBinding
import com.study.qrscanner.utils.Constants.AD_UNIT_ID
import com.study.qrscanner.utils.hideView
import com.study.qrscanner.utils.showView
import com.study.qrscanner.utils.toastMsg
import com.study.qrscanner.viewModel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private lateinit var appBarConfiguration: AppBarConfiguration

    private val viewModel by viewModels<MainViewModel>()


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initializeAds()

        checkAndAskPermission()

        initializeNavController()

        fragmentDestinationHandle()

        observables()

    }

    private fun initializeAds() {
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }

        val adView = AdView(this)
        adView.adUnitId = AD_UNIT_ID
        adView.setAdSize(AdSize.BANNER)
        binding.adView.removeAllViews()
        binding.adView.addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        adView.adListener = object : AdListener() {
            override fun onAdClicked() {
                toastMsg("onAdClicked")
                // Code to be executed when the user clicks on an ad.
            }

            override fun onAdClosed() {
                toastMsg("onAdClosed")
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e("adMob", adError.responseInfo.toString())
                toastMsg("onAdFailedToLoad")
                // Code to be executed when an ad request fails.
            }

            override fun onAdImpression() {
                toastMsg("onAdImpression")
                // Code to be executed when an impression is recorded
                // for an ad.
            }

            override fun onAdLoaded() {
                toastMsg("onAdLoaded")
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdOpened() {
                toastMsg("onAdOpened")
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
            }
        }
    }

    private fun observables() {
        viewModel.appEvent.observe(this) {
            when (it) {
                is AppEvent.Other -> {
                    openDrawer()
                }

                else -> {}
            }
            viewModel._appEvent.postValue(null)
        }
    }

    private fun openDrawer() {
        if (navController.currentDestination?.id == R.id.menu_favorites)
            navController.navigateUp()
        else if (navController.currentDestination?.id == R.id.fragment_generate_q_r)
            navController.navigateUp()
        else if (navController.currentDestination?.id != R.id.qrDetailsFragment)
            binding.drawerLayout.open()
        else
            navController.navigateUp()
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun fragmentDestinationHandle() {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.topBar.showView()
                    binding.bottomNavigation.showView()
                }

                R.id.historyFragment -> {
                    binding.topBar.showView()
                    binding.bottomNavigation.showView()
                }
                R.id.settingsFragment -> {
                    binding.topBar.showView()
                    binding.bottomNavigation.showView()
                }

                R.id.qrDetailsFragment -> {
                    binding.topBar.showView()
                    binding.bottomNavigation.hideView()
                }
                R.id.fragment_generate_q_r -> {
                    binding.topBar.showView()
                    binding.bottomNavigation.hideView()
                }

                R.id.menu_favorites -> {
                    binding.topBar.showView()
                    binding.bottomNavigation.hideView()
                }
            }
        }
    }

    private fun initializeNavController() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.menu_scan,
                R.id.menu_history,
                R.id.menu_setting
            ), binding.drawerLayout
        )
        setSupportActionBar(binding.topBar)
        binding.topBar.setupWithNavController(navController, appBarConfiguration)
        binding.navView.setupWithNavController(navController)
    }

    private fun checkAndAskPermission() {
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || checkSelfPermission(
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            val permission = arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.CALL_PHONE
            )
            requestPermissions(permission, 112)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}