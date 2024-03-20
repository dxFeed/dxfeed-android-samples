package com.dxfeed.quotetableapp.adapters

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson


data class InstrumentInfo(val symbol: String, val description: String) {

}

class SymbolsDataProvider(val context: Context) {
    val kSelectedSymbolsKey = "kSelectedSymbolsKey"
    val kSymbolsKey = "kSymbolsKey"
    val arrayInfoClass = Array<InstrumentInfo>::class.java
    val arrayStringClass = Array<String>::class.java

    val gson = Gson()

    var allSymbols: Array<InstrumentInfo>
        get() {
            var preferences: SharedPreferences =
                context.getSharedPreferences("SymbolsDataProviderPreferences", Context.MODE_PRIVATE)
            val editor = preferences.edit()

            val allSymbols = preferences.getString(kSymbolsKey, null)
            val symbols = gson.fromJson(
                allSymbols,
                arrayInfoClass
            )

            return symbols
        }
        set(value) {
            var preferences: SharedPreferences =
                context.getSharedPreferences("SymbolsDataProviderPreferences", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            val string = gson.toJson(value, arrayInfoClass)
            editor.putString(kSymbolsKey, string)
            editor.apply();
        }

    var selectedSymbols: Array<String>
        get() {
            var preferences: SharedPreferences =
                context.getSharedPreferences("SymbolsDataProviderPreferences", Context.MODE_PRIVATE)
            val editor = preferences.edit()

            val allSymbols = preferences.getString(kSelectedSymbolsKey, null)

            if (allSymbols == null) {
                return arrayOf("AAPL", "IBM", "ETH/USD:GDAX")
            }

            val symbols = gson.fromJson(
                allSymbols,
                arrayStringClass
            )

            return symbols
        }
        set(value) {
            var preferences: SharedPreferences =
                context.getSharedPreferences("SymbolsDataProviderPreferences", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            val string = gson.toJson(value, arrayStringClass)
            editor.putString(kSelectedSymbolsKey, string)
            editor.apply();
        }
}