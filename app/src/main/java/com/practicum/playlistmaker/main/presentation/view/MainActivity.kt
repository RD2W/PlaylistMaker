package com.practicum.playlistmaker.main.presentation.view

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.common.constants.LogTags
import com.practicum.playlistmaker.common.presentation.navigation.BackPressHandler
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Главная Activity приложения, содержащая навигацию и основной UI.
 * Реализует:
 * - Навигацию между фрагментами
 * - Управление Toolbar и BottomNavigation
 * - Обработку нажатия кнопки "Назад"
 * - Отображение Splash Screen
 */
class MainActivity : AppCompatActivity() {
    /**
     * ViewBinding для activity_main.xml. Инициализируется лениво при первом обращении.
     * Ленивая инициализация позволяет отложить создание до фактической необходимости.
     */
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    /**
     * Контроллер навигации. Инициализируется лениво при первом обращении.
     * Получает NavController из NavHostFragment.
     */
    private val navController by lazy {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        navHostFragment.navController
    }

    /**
     * Callback для кастомной обработки нажатия кнопки "Назад".
     * Удаляется при уничтожении Activity.
     */
    private var backPressedCallback: OnBackPressedCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        keepSplashScreen(splashScreen, true)

        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)

        setupNavigation()

        lifecycleScope.launch {
            delay(SPLASH_SCREEN_DURATION_MILLIS)
            keepSplashScreen(splashScreen, false)
        }
    }

    override fun onDestroy() {
        // Очистка callback для избежания утечек памяти
        backPressedCallback?.remove()
        super.onDestroy()
    }

    /**
     * Управляет отображением Splash Screen.
     * @param splashScreen Экземпляр SplashScreen
     * @param keep Если true - оставляет Splash Screen на экране
     */
    private fun keepSplashScreen(splashScreen: SplashScreen, keep: Boolean) {
        splashScreen.setKeepOnScreenCondition { keep }
    }

    /**
     * Основной метод настройки навигации в приложении:
     * - Связывает BottomNavigation с NavController
     * - Настраивает обработчик кнопки "Назад"
     * - Устанавливает слушатель изменений destination
     */
    private fun setupNavigation() {
        // Настройка BottomNavigation
        binding.bottomNavigation.setupWithNavController(navController)

        // Инициализация обработчика back press
        setupBackPressHandler()

        // Обновление заголовка при старте
        binding.root.post {
            updateToolbarTitle(navController.currentDestination)
        }

        // Обработчик изменений destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateToolbarTitle(destination)
            updateUIForDestination(destination.id)
        }
    }

    /**
     * Настраивает кастомную обработку кнопки "Назад":
     * - Для системной кнопки через OnBackPressedCallback
     * - Для кнопки в Toolbar через setNavigationOnClickListener
     */
    private fun setupBackPressHandler() {
        // Callback для системной кнопки "Назад"
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val currentFragment = getCurrentFragment()

                // Если фрагмент реализует BackPressHandler и обработал нажатие
                if (currentFragment is BackPressHandler && currentFragment.handleBackPressed()) {
                    return
                }

                // Стандартная обработка
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        }.also { callback ->
            onBackPressedDispatcher.addCallback(this, callback)
        }

        // Обработка кнопки назад в Toolbar
        binding.topAppBar.setNavigationOnClickListener {
            val currentFragment = getCurrentFragment()

            // Если фрагмент реализует BackPressHandler и обработал нажатие
            if (currentFragment is BackPressHandler && currentFragment.handleBackPressed()) {
                return@setNavigationOnClickListener
            }

            // Попытка навигации назад, если не удается - закрываем Activity
            if (!navController.popBackStack()) {
                finish()
            }
        }
    }

    /**
     * Получает текущий отображаемый фрагмент из NavHostFragment.
     * @return Текущий фрагмент или null, если не найден
     */
    private fun getCurrentFragment(): Fragment? {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as? NavHostFragment
        return navHostFragment?.childFragmentManager?.fragments?.firstOrNull()
    }

    /**
     * Обновляет заголовок Toolbar в зависимости от текущего destination.
     * @param destination Текущий пункт назначения навигации
     */
    private fun updateToolbarTitle(destination: NavDestination?) {
        Timber.tag(LogTags.NAVIGATION).d("Navigated to ${destination?.label}")
        binding.topAppBar.title = when (destination?.id) {
            R.id.playerFragment, R.id.playlistFragment -> NO_TOOLBAR_TITLE
            else -> destination?.label ?: getString(R.string.app_name)
        }
    }

    /**
     * Обновляет видимость UI элементов в зависимости от destination.
     * @param destinationId ID текущего пункта назначения
     */
    private fun updateUIForDestination(destinationId: Int) {
        when (destinationId) {
            R.id.searchFragment, R.id.mediaFragment, R.id.settingsFragment -> {
                showMainToolBar(true)
                showNavBarHideBackButton(true)
            }
            R.id.playlistFragment, R.id.playerFragment -> {
                showMainToolBar(false)
                showNavBarHideBackButton(false)
            }
            else -> {
                showMainToolBar(true)
                showNavBarHideBackButton(false)
            }
        }
    }

    /**
     * Управляет видимостью основного Toolbar.
     * @param isVisible Показывать или скрывать Toolbar
     */
    private fun showMainToolBar(isVisible: Boolean) {
        binding.topAppBar.isVisible = isVisible
    }

    /**
     * Управляет видимостью BottomNavigation и кнопки "Назад".
     * @param isVisible Показывать или скрывать BottomNavigation
     */
    private fun showNavBarHideBackButton(isVisible: Boolean) {
        binding.grNavigationView.isVisible = isVisible
        supportActionBar?.setDisplayHomeAsUpEnabled(!isVisible)
    }

    /**
     * Устанавливает кастомный заголовок для Toolbar.
     * @param titleRes String resource ID для заголовка
     */
    fun setToolbarTitle(@StringRes titleRes: Int) {
        binding.topAppBar.title = getString(titleRes)
    }

    companion object {
        /**
         * Пустой заголовок для Toolbar (используется для некоторых экранов)
         */
        const val NO_TOOLBAR_TITLE = ""

        /**
         * Длительность отображения Splash Screen в миллисекундах
         */
        const val SPLASH_SCREEN_DURATION_MILLIS = 250L
    }
}
