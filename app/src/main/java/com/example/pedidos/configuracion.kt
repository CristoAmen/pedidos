package com.example.pedidos

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.database.*
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import com.google.android.material.button.MaterialButton

class configuracion : AppCompatActivity() {

    // Declaramos la referencia a la base de datos y la instancia de FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    // ID de usuario (en producción se obtiene del usuario autenticado)
    private val userId = "Q4Ac3ij6laP33EVSQgUNqnohsvn1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuracion)

        // Obtén las referencias de los elementos de la UI
        val tvNombreUsuario = findViewById<TextView>(R.id.tvNombreUsuario)
        val tvCorreoElectronico = findViewById<TextView>(R.id.tvCorreoElectronico)
        val btnCerrarSesion = findViewById<MaterialButton>(R.id.btnCerrarSesion)

        // Inicializamos FirebaseAuth
        auth = FirebaseAuth.getInstance()

        // Inicializa la referencia a la base de datos apuntando al nodo "usuario"
        database = FirebaseDatabase.getInstance().getReference("usuario")

        // Consulta el nodo del usuario para mostrar la información del perfil
        database.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Recupera los valores del snapshot
                    val nombre = snapshot.child("nombre").getValue(String::class.java)
                    val correo = snapshot.child("correo").getValue(String::class.java)

                    // Asigna los valores a los TextViews
                    tvNombreUsuario.text = nombre ?: "Nombre no disponible"
                    tvCorreoElectronico.text = correo ?: "Correo no disponible"
                } else {
                    Log.w("configuracion", "No se encontraron datos para el usuario")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejo de errores
                Log.e("configuracion", "Error al leer los datos: ${error.message}")
            }
        })

        // Configura el listener para el botón de cerrar sesión
        btnCerrarSesion.setOnClickListener {
            auth.signOut() // Cierra la sesión del usuario actual

            // Opcional: Redirige al usuario a la pantalla de login (asegúrate de tener LoginActivity configurada)
            val intent = Intent(this, MainActivity::class.java)
            // Limpiamos la pila de actividades para que el usuario no regrese a esta pantalla
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}
