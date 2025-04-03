package com.example.pedidos

import Pedido
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.button.MaterialButton

class Home : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnNuevoPedido = findViewById<MaterialButton>(R.id.btnNuevoPedido)
        val btnMas = findViewById<ImageButton>(R.id.btnMas)

        btnNuevoPedido.setOnClickListener {
            startActivity(Intent(this, pedidos::class.java))
        }

        btnMas.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.bottom_nav_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    // Navegar a Configuración
                    startActivity(Intent(this, configuracion::class.java))
                    true
                }
                R.id.action_about -> {
                    // Mostrar diálogo Acerca de
                    showAboutDialog()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Acerca de")
            .setMessage("Versión 1.0\nDesarrollado por Mi Empresa")
            .setPositiveButton("Aceptar", null)
            .show()
    }
}