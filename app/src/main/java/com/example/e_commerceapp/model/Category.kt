package com.example.e_commerceapp.model

sealed class Category(val categoryName: String) {
    data object Chair: Category("chair")
    data object Table: Category("table")
}