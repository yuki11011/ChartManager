package com.example.chartManager

import Repository
import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class ChartApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { ItemDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { Repository(database.itemDao()) }
}