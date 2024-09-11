package com.example.e_commerceapp.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.e_commerceapp.model.Category
import com.example.e_commerceapp.viewmodel.CategoryViewModel
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class BaseCategoryViewModelFactory @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val category: Category
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoryViewModel(firestore, category) as T
    }
}