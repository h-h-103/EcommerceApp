package com.example.e_commerceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.e_commerceapp.firebase.FirebaseCommon
import com.example.e_commerceapp.model.CartProduct
import com.example.e_commerceapp.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
): ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateProductInCart(cartProduct: CartProduct) {
        viewModelScope.launch { _addToCart.emit(Resource.Loading()) }
        firestore.collection("User").document(auth.uid!!).collection("Cart")
            .whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()) { // Add new product
                        addNewProduct(cartProduct)
                    } else {
                        val product = it.first().toObject(CartProduct::class.java)
                        if (product == cartProduct) { // Increase the quantity
                            val documentId = it.first().id
                            increaseQuantity(documentId, cartProduct)
                        } else { // Add new product
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct) { addCartProduct, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(addCartProduct!!))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId) { _, e ->
            viewModelScope.launch {
                if (e == null)
                    _addToCart.emit(Resource.Success(cartProduct))
                else
                    _addToCart.emit(Resource.Error(e.message.toString()))
            }
        }
    }
}