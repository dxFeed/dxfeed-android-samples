package com.dxfeed.quotetableapp.adapters

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dxfeed.quotetableapp.QuoteApp
import com.google.gson.Gson


data class InstrumentInfo(val symbol: String, val description: String)

interface ISaveSymbols {
    fun save(symbols: List<String>)
}


class SymbolsDataProvider: ISaveSymbols, ViewModel() {
    private val kSelectedSymbolsKey = "kSelectedSymbolsKey"
    private val kSymbolsKey = "kSymbolsKey"
    private val kPreferencesName = "SymbolsDataProviderPreferences"

    private val arrayInfoClass = Array<InstrumentInfo>::class.java
    private val arrayStringClass = Array<String>::class.java

    private val context: Context
        get() = QuoteApp.context!!

    private val _data: MutableLiveData<List<String>>
    val data: LiveData<List<String>>

    private val gson = Gson()
    private var cachedSymbols = arrayOf<InstrumentInfo>()
    private var cachedSelectedSymbols = arrayOf<String>()
    companion object {
        @Volatile private var instance: SymbolsDataProvider? = null
        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: SymbolsDataProvider().also { instance = it }
            }
    }

    init {
        _data = MutableLiveData<List<String>>(selectedSymbols.toList())
        data = _data
    }
    var allSymbols: Array<InstrumentInfo>
        get() {
            if (cachedSymbols.isNullOrEmpty()) {
                var preferences: SharedPreferences =
                    context.getSharedPreferences(kPreferencesName, Context.MODE_PRIVATE)

                val allSymbols = preferences.getString(kSymbolsKey, null)
                cachedSymbols = if (allSymbols == null) {
                    arrayOf()
                } else {
                    val symbols = gson.fromJson(
                        allSymbols,
                        arrayInfoClass
                    )
                    symbols.sortedBy { it.symbol }.toTypedArray()
                }
            }
            
            return cachedSymbols
        }

        set(value) {
            if (cachedSymbols.contentEquals(value)) {
                return
            }
            cachedSymbols = value

            var preferences: SharedPreferences =
                context.getSharedPreferences(kPreferencesName, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            val string = gson.toJson(value, arrayInfoClass)
            editor.putString(kSymbolsKey, string)
            editor.apply();
        }

    var selectedSymbols: Array<String>
        get() {
            if (cachedSelectedSymbols.isNullOrEmpty()) {
                var preferences: SharedPreferences =
                    context.getSharedPreferences(kPreferencesName, Context.MODE_PRIVATE)

                val allSymbolsStr = preferences.getString(kSelectedSymbolsKey, null)
                cachedSelectedSymbols = if (allSymbolsStr == null) {
                    arrayOf("AAPL", "IBM", "ETH/USD:GDAX")
                } else {
                    gson.fromJson(
                        allSymbolsStr,
                        arrayStringClass
                    )
                }
            }

            return cachedSelectedSymbols
        }
        set(value) {
            if (cachedSelectedSymbols.contentEquals(value)) {
                return
            }
            cachedSelectedSymbols = value

            val newValue = value.toList()
            _data.value = newValue

            var preferences: SharedPreferences =
                context.getSharedPreferences(kPreferencesName, Context.MODE_PRIVATE)
            val editor = preferences.edit()
            val string = gson.toJson(value, arrayStringClass)
            editor.putString(kSelectedSymbolsKey, string)
            editor.apply();
        }

    override fun save(symbols: List<String>) {
        selectedSymbols = symbols.toTypedArray()
    }
}