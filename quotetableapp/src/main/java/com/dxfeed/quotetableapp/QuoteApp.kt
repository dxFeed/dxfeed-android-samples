package com.dxfeed.quotetableapp

import android.app.Application
import android.content.Context
import com.dxfeed.quotetableapp.adapters.SymbolsDataProvider

class QuoteApp : Application() {
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        dataProvider = SymbolsDataProvider(applicationContext)
    }

    companion object {
        var context: Context? = null
            private set

        lateinit var dataProvider: SymbolsDataProvider
            private set
    }
}