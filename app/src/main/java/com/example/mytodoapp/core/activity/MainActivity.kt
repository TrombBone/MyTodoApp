package com.example.mytodoapp.core.activity

import android.os.Bundle
import com.example.mytodoapp.R
import com.example.mytodoapp.abstracts.BaseActivity

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}