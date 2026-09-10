package com.abdellahshabat.fatora.util

import android.content.Context

/** أوضاع المظهر المتاحة للتطبيق. */
enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

/**
 * تخزين بسيط لبيانات المحل وتفضيلات التطبيق عبر SharedPreferences.
 * ما احتجنا Room هون لأنها إعدادات بسيطة (سطر واحد لكل قيمة)، مش جدول بيانات.
 */
class ShopPreferences(context: Context) {

    private val prefs = context.applicationContext
        .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getShopName(): String = prefs.getString(KEY_SHOP_NAME, "") ?: ""

    fun getOwnerPhone(): String = prefs.getString(KEY_OWNER_PHONE, "") ?: ""

    fun saveShopProfile(shopName: String, ownerPhone: String) {
        prefs.edit()
            .putString(KEY_SHOP_NAME, shopName)
            .putString(KEY_OWNER_PHONE, ownerPhone)
            .apply()
    }

    fun getThemeMode(): ThemeMode {
        val raw = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return runCatching { ThemeMode.valueOf(raw) }.getOrDefault(ThemeMode.SYSTEM)
    }

    fun saveThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }

    companion object {
        private const val PREFS_NAME = "fatora_shop_profile"
        private const val KEY_SHOP_NAME = "shop_name"
        private const val KEY_OWNER_PHONE = "owner_phone"
        private const val KEY_THEME_MODE = "theme_mode"
    }
}