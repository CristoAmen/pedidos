package com.example.pedidos

import Pedido
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Home : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var pedidoAdapter: PedidoAdapter
    private val listaPedidos = mutableListOf<Pedido>()

    private lateinit var tvPedidosCount: TextView
    private lateinit var tvVentasCount: TextView
    private lateinit var tvPendientesCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnNuevoPedido = findViewById<MaterialButton>(R.id.btnNuevoPedido)
        val btnMas = findViewById<ImageButton>(R.id.btnMas)
        recyclerView = findViewById(R.id.recyclerPedidos)

        tvPedidosCount = findViewById(R.id.tvPedidosCount)
        tvVentasCount = findViewById(R.id.tvVentasCount)
        tvPendientesCount = findViewById(R.id.tvPendientesCount)

        recyclerView.layoutManager = LinearLayoutManager(this)
        // Se pasa la función lambda para manejar el clic en cada pedido.
        pedidoAdapter = PedidoAdapter(listaPedidos) { pedido, position ->
            // Manejo de clic: muestra un Toast con el nombre del cliente.
            Toast.makeText(this, "Pedido de: ${pedido.nombreCliente}", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = pedidoAdapter

        cargarPedidosDesdeFirebase()

        btnNuevoPedido.setOnClickListener {
            startActivity(Intent(this, pedidos::class.java))
        }

        btnMas.setOnClickListener { view ->
            showPopupMenu(view)
        }
    }

    private fun cargarPedidosDesdeFirebase() {
        val database = FirebaseDatabase.getInstance().reference
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.child("pedidos").child(usuarioId).child("pedidos")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    listaPedidos.clear()

                    for (pedidoSnapshot in snapshot.children) {
                        if (pedidoSnapshot.value == null) continue

                        try {
                            val pedido = pedidoSnapshot.getValue(Pedido::class.java)

                            // Verificar si el pedido es válido antes de agregarlo a la lista
                            if (pedido != null && pedido.activo && pedido.nombreCliente.isNotBlank()) {
                                listaPedidos.add(pedido)
                            }
                        } catch (e: IllegalArgumentException) {
                            // Ignorar pedidos con datos inválidos
                            e.printStackTrace()
                        }
                    }

                    pedidoAdapter.notifyDataSetChanged()
                    actualizarResumen()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar error de lectura
                }
            })
    }

    private fun actualizarResumen() {
        val totalPedidos = listaPedidos.size
        val totalVentas = listaPedidos.sumByDouble { it.monto }
        val totalPendientes = listaPedidos.count { !it.activo }

        tvPedidosCount.text = totalPedidos.toString()
        tvVentasCount.text = "$$totalVentas"
        tvPendientesCount.text = totalPendientes.toString()
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(this, view)
        val inflater: MenuInflater = popup.menuInflater
        inflater.inflate(R.menu.bottom_nav_menu, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, configuracion::class.java))
                    true
                }
                R.id.action_about -> {
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
            .setMessage("Versión 1.0\nDesarrollado por paco")
            .setPositiveButton("Aceptar", null)
            .show()
    }
}
