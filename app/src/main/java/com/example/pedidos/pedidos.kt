package com.example.pedidos

import Pedido
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.pedidos.databinding.ActivityPedidosBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class pedidos : AppCompatActivity() {

    // Variables de clase
    private lateinit var binding: ActivityPedidosBinding
    private lateinit var database: DatabaseReference
    private lateinit var contadorRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPedidosBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Deshabilitar el campo para que no se pueda editar
        binding.etNombreCliente.isEnabled = false

        configurarFirebase()
        cargarNombreUsuario()
        configurarBotones()
    }

    private fun configurarFirebase() {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser ?: run {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val firebaseDatabase = FirebaseDatabase.getInstance()
        database = firebaseDatabase.getReference("pedidos/${currentUser.uid}/pedidos")
        contadorRef = firebaseDatabase.getReference("pedidos/${currentUser.uid}/contador_pedidos")
    }

    private fun cargarNombreUsuario() {
        val currentUser = auth.currentUser ?: return
        val userRef = FirebaseDatabase.getInstance()
            .getReference("usuario")
            .child(currentUser.uid)

        userRef.child("nombre").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val nombreUsuario = snapshot.getValue(String::class.java)
                binding.etNombreCliente.setText(nombreUsuario ?: "")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@pedidos,
                    "Error al obtener el nombre del usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun configurarBotones() {
        binding.btnGuardarPedido.setOnClickListener {
            validarYGuardarPedido()
        }
    }

    private fun validarYGuardarPedido() {
        val currentUser = auth.currentUser ?: return

        // Obtener valores del formulario
        val nombre = binding.etNombreCliente.text.toString().trim()
        val direccion = binding.etDireccion.text.toString().trim()
        val descripcion = binding.etDescripcion.text.toString().trim()
        val monto = binding.etMonto.text.toString().trim()

        // Validar campos
        if (!validarCampos(nombre, direccion, descripcion, monto)) return

        // Obtener nuevo folio antes de guardar
        obtenerYAsignarNuevoFolio { nuevoFolio ->
            val nuevoPedido = Pedido(
                folio = nuevoFolio.toString(),
                usuarioId = currentUser.uid,
                nombreCliente = nombre,
                direccion = direccion,
                descripcion = descripcion,
                monto = monto.toDouble(),
                activo = true
            )

            guardarEnFirebase(nuevoPedido)
        }
    }

    private fun validarCampos(
        nombre: String,
        direccion: String,
        descripcion: String,
        monto: String
    ): Boolean {
        var valido = true

        with(binding) {
            if (nombre.isEmpty()) {
                etNombreCliente.error = "Ingrese el nombre del cliente"
                valido = false
            }
            if (direccion.isEmpty()) {
                etDireccion.error = "Ingrese la dirección de entrega"
                valido = false
            }
            if (descripcion.isEmpty()) {
                etDescripcion.error = "Ingrese una descripción"
                valido = false
            }
            if (monto.isEmpty()) {
                etMonto.error = "Ingrese el monto"
                valido = false
            } else {
                try {
                    if (monto.toDouble() <= 0) {
                        etMonto.error = "Monto debe ser mayor a 0"
                        valido = false
                    }
                } catch (e: NumberFormatException) {
                    etMonto.error = "Formato inválido"
                    valido = false
                }
            }
        }
        return valido
    }

    private fun obtenerYAsignarNuevoFolio(callback: (Int) -> Unit) {
        contadorRef.runTransaction(object : Transaction.Handler {
            override fun doTransaction(currentData: MutableData): Transaction.Result {
                var folioActual = currentData.getValue(Int::class.java) ?: 0
                folioActual++ // Incrementamos el contador
                currentData.value = folioActual
                return Transaction.success(currentData)
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                currentData: DataSnapshot?
            ) {
                if (error != null) {
                    mostrarError("Error al actualizar folio: ${error.message}")
                } else if (committed) {
                    val nuevoFolio = currentData?.getValue(Int::class.java) ?: 0
                    callback(nuevoFolio) // Devolvemos el nuevo folio
                }
            }
        })
    }

    private fun guardarEnFirebase(pedido: Pedido) {
        val pedidoRef = database.child(pedido.folio)

        pedidoRef.setValue(pedido)
            .addOnSuccessListener {
                mostrarExito("Pedido #${pedido.folio} guardado")

                // Ir a Home y cerrar esta actividad
                val intent = Intent(this, Home::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish() // Cierra la actividad actual
            }
            .addOnFailureListener { e ->
                mostrarError("Error al guardar: ${e.message}")
            }
    }

    private fun mostrarExito(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun mostrarError(mensaje: String) {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    }

    private fun limpiarFormulario() {
        with(binding) {
            // Si deseas mantener el nombre, no lo limpies
            etDireccion.text?.clear()
            etDescripcion.text?.clear()
            etMonto.text?.clear()
            etDireccion.requestFocus()
        }
    }
}
