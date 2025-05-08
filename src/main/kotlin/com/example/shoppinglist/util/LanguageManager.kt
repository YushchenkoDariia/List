package com.example.shoppinglist.util


import java.util.*


object LanguageManager {
    public const val BUNDLE_BASE_NAME = "lang/strings"


    public var locale = Locale("en")
    private var resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale)


    fun switchLanguage(languageCode: String) {
        locale = Locale(languageCode)
        resourceBundle = ResourceBundle.getBundle(BUNDLE_BASE_NAME, locale)
    }


    fun getString(key: String): String {
        return try {
            resourceBundle.getString(key)
        } catch (e: MissingResourceException) {
            "⚠️ $key"
        }
    }


    fun getStringFormatted(key: String, vararg args: Any): String {
        val pattern = getString(key)
        return String.format(pattern, *args)
    }


    fun getCurrentLanguage(): String {
        return locale.language
    }
}

