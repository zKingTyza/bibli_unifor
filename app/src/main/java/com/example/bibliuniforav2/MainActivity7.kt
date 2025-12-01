package com.example.bibliuniforav2

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Locale

class MainActivity7 :  BaseActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var notificationAdapter: NotificationAdapter
    private val notificationList = mutableListOf<Notification>()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy 'às' HH:mm", Locale.getDefault())

    // 🔑 UID do usuário logado
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main7)

        // 🔑 Obtém o UID do usuário logado
        currentUserId = auth.currentUser?.uid ?: ""

        if (currentUserId.isEmpty()) {
            Toast.makeText(this, "Usuário não logado!", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        setupBottomNavigation()
    }

    override fun onResume() {
        super.onResume()
        fetchNotificationsFromFirebase()
    }

    private fun setupRecyclerView() {
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_notifications)
        recyclerView.layoutManager = LinearLayoutManager(this)
        notificationAdapter = NotificationAdapter(notificationList)
        recyclerView.adapter = notificationAdapter
    }

    private fun fetchNotificationsFromFirebase() {
        // 🔑 FILTRA APENAS NOTIFICAÇÕES DO USUÁRIO LOGADO
        db.collection("notificacoes")
            .whereEqualTo("uidAuth", currentUserId) // ← FILTRO IMPORTANTE!
            .orderBy("data", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                notificationList.clear()

                for (document in documents) {
                    val notification = document.toObject(Notification::class.java)

                    // Preenche os campos locais
                    notification.id = document.id
                    notification.time = notification.data?.let { date -> dateFormat.format(date) } ?: "Agora"
                    notification.isRead = false

                    notificationList.add(notification)
                }
                notificationAdapter.notifyDataSetChanged()

                // Mostra quantas notificações foram encontradas
                Toast.makeText(this, "${notificationList.size} notificações", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e("Firestore", "Erro ao buscar notificações", exception)
                Toast.makeText(this, "Falha ao carregar notificações.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_notifications

        bottomNavigationView.setOnItemSelectedListener { item ->
            var intent: Intent? = null
            when (item.itemId) {
                R.id.nav_profile -> intent = Intent(this, MainActivity8::class.java)
                R.id.nav_library -> intent = Intent(this, MainActivity31::class.java)
                R.id.nav_home -> intent = Intent(this, MainActivity4::class.java)
                R.id.nav_donate -> intent = Intent(this, MainActivity32::class.java)
                R.id.nav_notifications -> { /* Não faz nada */ }
            }
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                startActivity(intent)
            }
            true
        }
        bottomNavigationView.setOnItemReselectedListener { /* Não faz nada */ }
    }
}