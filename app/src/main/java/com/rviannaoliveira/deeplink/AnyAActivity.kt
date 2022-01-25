package com.rviannaoliveira.deeplink

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.rviannaoliveira.deeplink.router.requireLinkUri
import com.rviannaoliveira.deeplink.sample.R

class AnyAActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_any_a)
        val deeplinkUri = requireLinkUri()
        deeplinkUri?.getQueryParameter("name")?.let {
            Toast.makeText(this,it,Toast.LENGTH_SHORT).show()
        }
    }
}