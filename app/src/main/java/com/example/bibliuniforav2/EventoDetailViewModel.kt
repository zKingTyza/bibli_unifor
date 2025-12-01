package com.example.bibliuniforav2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class EventoDetailViewModel(private val eventosRepository: EventosRepository) : ViewModel() {

    private val _evento = MutableLiveData<Evento?>()
    val evento: LiveData<Evento?> get() = _evento

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> get() = _errorMessage

    fun loadEvento(eventoId: String) {
        viewModelScope.launch {
            try {
                val loadedEvento: Evento? = eventosRepository.getEventoById(eventoId)
                _evento.value = loadedEvento

                if (loadedEvento == null) {
                    _errorMessage.value = "Evento não encontrado."
                } else {
                    _errorMessage.value = ""
                }

            } catch (e: Exception) {
                _evento.value = null
                _errorMessage.value = "Erro ao carregar evento: ${e.message}"
            }
        }
    }

    class Factory(private val eventosRepository: EventosRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EventoDetailViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EventoDetailViewModel(eventosRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}