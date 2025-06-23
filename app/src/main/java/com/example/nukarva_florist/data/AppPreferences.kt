package com.example.nukarva_florist.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.nukarva_florist.utils.Constants

class AppPreferences(private val mContext: Context) {
    private val sharedPref: SharedPreferences =
        mContext.getSharedPreferences(Constants.Preferences.MAIN_CONFIG, Context.MODE_PRIVATE)
    private val spEditor: SharedPreferences.Editor = sharedPref.edit()

    fun setToken(token: String?) {
        spEditor.putString(Constants.Login.TOKEN, token)
        spEditor.commit()
    }

    fun getToken(): String? {
        return sharedPref.getString(Constants.Login.TOKEN, "token")
    }

    fun setEmail(email: String?) {
        spEditor.putString(Constants.Login.EMAIL, email)
        spEditor.commit()
    }

    fun getEmail(): String? {
        return sharedPref.getString(Constants.Login.EMAIL, "email")
    }

    fun addFavorite(productId: Int) {
        val favorites = getFavorites().toMutableSet()
        favorites.add(productId)
        saveFavorites(favorites)

        val timestampKey = "${Constants.Preferences.FAVORITE_TIMESTAMP}_$productId"
        sharedPref.edit().putLong(timestampKey, System.currentTimeMillis()).apply()
    }

    fun removeFavorite(productId: Int) {
        val favorites = getFavorites().toMutableSet()
        favorites.remove(productId)
        saveFavorites(favorites)
    }

    fun isFavorite(productId: Int): Boolean {
        return getFavorites().contains(productId)
    }

    fun getFavorites(): Set<Int> {
        return sharedPref.getStringSet(Constants.Preferences.FAVORITE, emptySet())
            ?.mapNotNull { it.toIntOrNull() }
            ?.toSet() ?: emptySet()
    }

    fun saveFavorites(favorites: Set<Int>) {
        val stringSet = favorites.map { it.toString() }.toSet()
        spEditor.putStringSet(Constants.Preferences.FAVORITE, stringSet)
        spEditor.commit()
    }

    fun getFavoriteSavedTimestamp(productId: Int): Long {
        val key = "${Constants.Preferences.FAVORITE_TIMESTAMP}_$productId"
        return sharedPref.getLong(key, 0L)
    }

    fun setIsLoggedIn(isLoggedIn: String?){
        spEditor.putString(Constants.Preferences.LOGGEDIN, isLoggedIn)
        spEditor.commit()
    }

    fun getIsLoggedIn(): String? {
        return sharedPref.getString(Constants.Preferences.LOGGEDIN, "false")
    }

    companion object {
        fun clearAll(context: Context) {
            val pref: SharedPreferences = context.getSharedPreferences(
                Constants.Preferences.MAIN_CONFIG,
                Context.MODE_PRIVATE
            )
            val editor: SharedPreferences.Editor = pref.edit()
            editor.clear()
            editor.commit()
        }
    }
}
