package com.example.bibliuniforav2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope // <--- ADICIONE ESTA LINHA
import kotlinx.coroutines.launch

class AdminEventsViewModel(private val repository: EventosRepository) : ViewModel() {

    private val _eventosAtivos = MutableLiveData<List<Evento>>()
    val eventosAtivos: LiveData<List<Evento>> = _eventosAtivos

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    init {
        loadEventosAtivos()
    }

    fun loadEventosAtivos() {
        viewModelScope.launch {
            try {
                _errorMessage.value = "" // Limpa erros anteriores
                val eventos = repository.getAllEventos()
                _eventosAtivos.value = eventos
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao carregar eventos: ${e.message}"
                _eventosAtivos.value = emptyList() // Garante que a lista não seja nula em caso de erro
            }
        }
    }

    fun deleteEvento(id: String) {
        viewModelScope.launch {
            try {
                _errorMessage.value = ""
                val sucesso = repository.deleteEvento(id)
                if (sucesso) {
                    // Após deletar, recarrega a lista para refletir a mudança
                    loadEventosAtivos()
                } else {
                    _errorMessage.value = "Falha ao deletar o evento."
                }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao deletar evento: ${e.message}"
            }
        }
    }

    class Factory(private val repository: EventosRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AdminEventsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AdminEventsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}