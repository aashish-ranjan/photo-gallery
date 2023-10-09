package com.example.photogallery

import android.content.Context
import androidx.work.WorkManager
import java.lang.IllegalStateException

class WorkManagerSingleton private constructor() {
    companion object {
        private var INSTANCE: WorkManager? = null

        fun initialise(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = WorkManager.getInstance(context)
            }
        }

        fun getInstance(): WorkManager {
            return INSTANCE ?: throw IllegalStateException("WorkManagerSingleton must be initialised")
        }
    }

}