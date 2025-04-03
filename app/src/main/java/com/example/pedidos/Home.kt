package com.example.pedidos

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class Home : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_pedidos -> {
                    startActivity(Intent(this, pedidos::class.java))
                    true
                }
                R.id.nav_historial -> {
                    startActivity(Intent(this, historial::class.java))
                    true
                }
                R.id.nav_clientes -> {
                    startActivity(Intent(this, clientes::class.java))
                    true
                }
                R.id.nav_configuracion -> {
                    startActivity(Intent(this, clientes::class.java))
                    true
                }
                else -> false
            }
        }
    }
}