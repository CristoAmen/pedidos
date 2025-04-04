package com.example.pedidos

import Pedido
import android.content.Intent
import android.os.Bundle
import android.view.MenuInflater
import android.view.View
import android.widget.EditText
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val btnNuevoPedido = findViewById<MaterialButton>(R.id.btnNuevoPedido)
        val btnMas = findViewById<ImageButton>(R.id.btnMas)
        recyclerView = findViewById(R.id.recyclerPedidos)

        tvPedidosCount = findViewById(R.id.tvPedidosCount)
        tvVentasCount = findViewById(R.id.tvVentasCount)

        recyclerView.layoutManager = LinearLayoutManager(this)
        pedidoAdapter = PedidoAdapter(
            listaPedidos,
            onClick = { pedido, position ->
                Toast.makeText(this, "Pedido de: ${pedido.nombreCliente}", Toast.LENGTH_SHORT).show()
            },
            onEdit = { pedido, position ->
                editarPedido(pedido, position)
            },
            onCancel = { pedido, position ->
                cancelarPedido(pedido, position)
            }
        )
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
                            if (pedido != null && pedido.activo && pedido.nombreCliente.isNotBlank()) {
                                listaPedidos.add(pedido)
                            }
                        } catch (e: IllegalArgumentException) {
                            e.printStackTrace()
                        }
                    }

                    pedidoAdapter.notifyDataSetChanged()
                    actualizarResumen()
                }

                override fun onCancelled(error: DatabaseError) {
                    // Manejar error
                }
            })
    }

    private fun actualizarResumen() {
        val totalPedidos = listaPedidos.size
        val totalVentas = listaPedidos.sumByDouble { it.monto }

        tvPedidosCount.text = totalPedidos.toString()
        tvVentasCount.text = "$$totalVentas"
    }

    private fun cancelarPedido(pedido: Pedido, position: Int) {
        pedido.activo = false

        val database = FirebaseDatabase.getInstance().reference
        val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        database.child("pedidos")
            .child(usuarioId)
            .child("pedidos")
            .child(pedido.folio)
            .setValue(pedido)
            .addOnSuccessListener {
                recreate()
                Toast.makeText(this, "Pedido cancelado", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cancelar el pedido", Toast.LENGTH_SHORT).show()
            }
    }

    private fun editarPedido(pedido: Pedido, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_pedido, null)

        val txtNombre = dialogView.findViewById<TextView>(R.id.txtNombre)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val etDireccion = dialogView.findViewById<EditText>(R.id.etDireccion)
        val etMonto = dialogView.findViewById<EditText>(R.id.etMonto)

        txtNombre.text = pedido.nombreCliente
        etDescripcion.setText(pedido.descripcion)
        etDireccion.setText(pedido.direccion)
        etMonto.setText(pedido.monto.toString())

        val builder = AlertDialog.Builder(this)
            .setTitle("Editar Pedido")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                pedido.descripcion = etDescripcion.text.toString()
                pedido.direccion = etDireccion.text.toString()
                pedido.monto = etMonto.text.toString().toDoubleOrNull() ?: pedido.monto

                val database = FirebaseDatabase.getInstance().reference
                val usuarioId = FirebaseAuth.getInstance().currentUser?.uid ?: return@setPositiveButton

                database.child("pedidos")
                    .child(usuarioId)
                    .child("pedidos")
                    .child(pedido.folio)
                    .setValue(pedido)
                    .addOnSuccessListener {
                        pedidoAdapter.notifyItemChanged(position)
                        actualizarResumen()
                        Toast.makeText(this, "Pedido actualizado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Error al actualizar el pedido", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
        builder.show()
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
