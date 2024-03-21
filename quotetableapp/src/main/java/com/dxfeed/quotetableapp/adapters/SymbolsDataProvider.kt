package com.dxfeed.quotetableapp.adapters

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson


data class InstrumentInfo(val symbol: String, val description: String)

interface ISaveSymbols {
    fun save(symbols: List<String>)
}
class SymbolsDataProvider(val context: Context): ISaveSymbols, ViewModel() {
    private val kSelectedSymbolsKey = "kSelectedSymbolsKey"
    private val kSymbolsKey = "kSymbolsKey"
    private val arrayInfoClass = Array<InstrumentInfo>::class.java
    private val arrayStringClass = Array<String>::class.java

    private val _data: MutableLiveData<List<String>>
    val data: LiveData<List<String>>

    val gson = Gson()

    init {
        _data = MutableLiveData<List<String>>(selectedSymbols.toList())
        data = _data
    }
    var allSymbols: Array<InstrumentInfo>
        get() {
            var preferences: SharedPreferences =
                context.getSharedPreferences("SymbolsDataProviderPreferences", Context.MODE_PRIVATE)
            val editor = preferences.edit()

            val allSymbols = preferences.getString(kSymbolsKey, null)
            if (allSymbols == null) {
                return arrayOf()
            }
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
            val newValue = value.toList()
            println("Set symbols $newValue ${_data.value}")
            if (_data.value == newValue) {
                return
            }
            _data.value = newValue

            var preferences: SharedPreferences =
                context.getSharedPreferences("SymbolsDataProviderPreferences", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            val string = gson.toJson(value, arrayStringClass)
            editor.putString(kSelectedSymbolsKey, string)
            editor.apply();
        }

    override fun save(symbols: List<String>) {
        selectedSymbols = symbols.toTypedArray()
    }
}