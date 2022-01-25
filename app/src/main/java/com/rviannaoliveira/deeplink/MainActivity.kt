package com.rviannaoliveira.deeplink

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.rviannaoliveira.deeplink.router.domain.DeeplinkRouter
import org.koin.android.ext.android.inject
import com.rviannaoliveira.deeplink.sample.R

class MainActivity : AppCompatActivity() {
    private val deeplinkRouter by inject<DeeplinkRouter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.actionA).setOnClickListener {
            deeplinkRouter.launch(this,"meuapp://anyA?name=ryuk")
        }
        findViewById<View>(R.id.actionB).setOnClickListener {
            deeplinkRouter.launchWithStack(this,Deeplink.AnyB)
        }
    }
}