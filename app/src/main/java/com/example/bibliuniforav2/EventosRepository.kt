package com.example.bibliuniforav2

import android.util.Log // ✅ IMPORTAR LOG
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class EventosRepository(val db: FirebaseFirestore) {

    private val TAG = "EventosRepository" // ✅ TAG para logs
    private val eventosCollection = db.collection("eventos")

    // -------------------------------------------------------------------------
    // 1. READ ALL (Leitura de todos os eventos com IDs)
    // -------------------------------------------------------------------------

    suspend fun getAllEventos(): List<Evento> {
        return try {
            val snapshot = eventosCollection
                // ✅ REMOVIDO: .orderBy("dataHora") - este campo não existe mais no Firestore como um Date
                .get()
                .await()

            return snapshot.documents.mapNotNull { document ->
                document.toObject(Evento::class.java)?.apply {
                    id = document.id
                }
            }

        } catch (e: Exception) {
            // ✅ Log de erro detalhado
            Log.e(TAG, "Erro ao buscar todos os eventos: ${e.message}", e)
            emptyList()
        }
    }

    // -------------------------------------------------------------------------
    // 2. READ ONE (Buscar um único evento pelo ID)
    // -------------------------------------------------------------------------

    suspend fun getEventoById(id: String): Evento? {
        return try {
            val document = eventosCollection.document(id).get().await()
            document.toObject(Evento::class.java)?.apply {
                this.id = document.id
            }
        } catch (e: Exception) {
            // ✅ Log de erro detalhado
            Log.e(TAG, "Erro ao buscar evento pelo ID '$id': ${e.message}", e)
            null
        }
    }

    // -------------------------------------------------------------------------
    // 3. CREATE (Criação)
    // -------------------------------------------------------------------------

    suspend fun createEvento(evento: Evento): String? {
        return try {
            val documentReference = eventosCollection.add(evento).await()
            documentReference.id
        } catch (e: Exception) {
            // ✅ Log de erro detalhado. Evento.titulo pode ser nulo ou vazio ao criar
            val tituloEvento = evento.titulo.ifEmpty { "Evento sem título" }
            Log.e(TAG, "Erro ao criar evento '$tituloEvento': ${e.message}", e)
            null
        }
    }

    // -------------------------------------------------------------------------
    // 4. UPDATE (Atualização)
    // -------------------------------------------------------------------------

    suspend fun updateEvento(evento: Evento): Boolean {
        return try {
            evento.id.let { id -> // ✅ 'id' é String, não String?, então .let é mais seguro
                if (id.isNotEmpty()) { // ✅ Verificar se o ID não é vazio também
                    eventosCollection.document(id).set(evento).await()
                    true
                } else {
                    Log.w(TAG, "Falha ao atualizar evento: ID do evento é nulo ou vazio.")
                    false
                }
            }
        } catch (e: Exception) {
            val tituloEvento = evento.titulo.ifEmpty { "Evento sem título" }
            Log.e(TAG, "Erro ao atualizar evento '$tituloEvento' (ID: ${evento.id}): ${e.message}", e)
            false
        }
    }

    // -------------------------------------------------------------------------
    // 5. DELETE (Exclusão)
    // -------------------------------------------------------------------------

    suspend fun deleteEvento(id: String): Boolean {
        return try {
            eventosCollection.document(id).delete().await()
            true
        } catch (e: Exception) {
            // ✅ Log de erro detalhado
            Log.e(TAG, "Erro ao excluir evento com ID '$id': ${e.message}", e)
            false
        }
    }
}