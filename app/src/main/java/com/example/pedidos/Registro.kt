package com.example.pedidos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registro : AppCompatActivity() {

    // Instancias de FirebaseAuth y Realtime Database
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        // Inicializamos FirebaseAuth y Realtime Database
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        // Referencias a los elementos del layout
        val etNombreCompleto = findViewById<EditText>(R.id.etNombreCompleto)
        val etCorreo = findViewById<EditText>(R.id.etCorreo)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)

        // Listener para el botón de registro
        btnRegistrar.setOnClickListener {
            val nombreCompleto = etNombreCompleto.text.toString().trim()
            val correo = etCorreo.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Validación simple de campos vacíos
            if (nombreCompleto.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear usuario en Firebase Auth
            auth.createUserWithEmailAndPassword(correo, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Si el registro es exitoso, guardamos en Realtime Database
                        val user = auth.currentUser
                        user?.let { usuario ->
                            val userId = usuario.uid
                            // Datos que se guardarán en la base de datos
                            val userData = hashMapOf(
                                "nombre" to nombreCompleto,
                                "correo" to correo
                            )

                            // Guardar en la rama "usuario" usando el UID como key
                            database.reference.child("usuario").child(userId)
                                .setValue(userData)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                                    // Navegar a la pantalla Home
                                    startActivity(Intent(this, Home::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al guardar: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                    } else {
                        // Error en el registro en Auth
                        Toast.makeText(this, "Error en el registro: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
