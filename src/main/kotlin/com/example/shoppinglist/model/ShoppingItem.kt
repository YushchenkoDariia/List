package com.example.shoppinglist.model


import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import java.time.LocalDateTime


class ShoppingItem(
    name: String,
    isChecked: Boolean = false,
    quantity: Int = 1,
    category: String = ""
) {
    val id = System.currentTimeMillis()
    val nameProperty = SimpleStringProperty(name)
    val isCheckedProperty = SimpleBooleanProperty(isChecked)
    val quantityProperty = SimpleIntegerProperty(quantity)
    val categoryProperty = SimpleStringProperty(category)
    val createdAt: LocalDateTime = LocalDateTime.now()
    var updatedAt: LocalDateTime = LocalDateTime.now()
        private set


    var name: String
        get() = nameProperty.get()
        set(value) {
            nameProperty.set(value)
            updateTimestamp()
        }


    var isChecked: Boolean
        get() = isCheckedProperty.get()
        set(value) {
            isCheckedProperty.set(value)
            updateTimestamp()
        }


    var quantity: Int
        get() = quantityProperty.get()
        set(value) {
            quantityProperty.set(value)
            updateTimestamp()
        }


    var category: String
        get() = categoryProperty.get()
        set(value) {
            categoryProperty.set(value)
            updateTimestamp()
        }


    public fun updateTimestamp() {
        updatedAt = LocalDateTime.now()
    }


    override fun toString(): String {
        return "$name (${if(isChecked) "✓" else "○"}) - Qty: $quantity${if(category.isNotEmpty()) ", Category: $category" else ""}"
    }
}
