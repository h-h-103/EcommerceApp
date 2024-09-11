package com.example.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerceapp.model.Product
import com.example.e_commerceapp.util.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
): ViewModel() {

    private val _search = MutableStateFlow<Resource<List<Product>>>(Resource.Unspecified())
    val search = _search.asStateFlow()

    fun searchProducts(searchQuery: String) {
        viewModelScope.launch {
            _search.emit(Resource.Loading()) // Emit loading state
            try {
                // Modify Firestore query to use startAt and endAt for search by prefix
                val result = firestore.collection("Products")
                    .orderBy("name")
                    .startAt(searchQuery)
                    .endAt(searchQuery + "\uf8ff") // "\uf8ff" is a high Unicode character to allow prefix search
                    .get()
                    .await() // Wait for the Firestore query to complete

                val products = result.toObjects(Product::class.java) // Map the result to Product objects
                _search.emit(Resource.Success(products)) // Emit the success state with products list
            } catch (e: Exception) {
                _search.emit(Resource.Error(e.message.toString())) // Emit error state if the query fails
            }
        }
    }
}