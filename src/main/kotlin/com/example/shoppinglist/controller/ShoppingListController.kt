package com.example.shoppinglist.controller


import com.example.shoppinglist.model.ShoppingItem
import com.example.shoppinglist.util.LanguageManager
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.collections.transformation.FilteredList
import javafx.scene.control.Alert
import javafx.scene.control.ButtonType
import java.util.Optional


class ShoppingListController {


    // Колекція для зберігання елементів
    public val items: ObservableList<ShoppingItem> = FXCollections.observableArrayList()


    // Фільтрований список для відображення
    val filteredItems: FilteredList<ShoppingItem> = FilteredList(items) { true }


    // Додавання нового елементу
    fun addItem(name: String, quantity: Int, category: String): ShoppingItem {
        val newItem = ShoppingItem(name, false, quantity, category)
        items.add(newItem)
        return newItem
    }


    // Отримання елементу за індексом
    fun getItem(index: Int): ShoppingItem? {
        return if (index >= 0 && index < items.size) items[index] else null
    }


    // Оновлення елементу
    fun updateItem(itemId: Long, name: String, quantity: Int, category: String): Boolean {
        val item = items.find { it.id == itemId } ?: return false


        item.name = name
        item.quantity = quantity
        item.category = category


        return true
    }


    // Видалення елементу
    fun deleteItem(item: ShoppingItem): Boolean {
        return items.remove(item)
    }


    // Переключення статусу елементу (відмічено/не відмічено)
    fun toggleItemStatus(item: ShoppingItem) {
        item.isChecked = !item.isChecked
    }


    // Видалення всіх відмічених елементів
    fun deleteCheckedItems(): Int {
        val checkedItems = items.filter { it.isChecked }.toList()
        items.removeAll(checkedItems)
        return checkedItems.size
    }


    // Отримання кількості відмічених елементів
    fun getCheckedItemsCount(): Int {
        return items.count { it.isChecked }
    }


    // Показ діалогу підтвердження
    fun showConfirmationDialog(title: String, content: String): Boolean {
        val alert = Alert(Alert.AlertType.CONFIRMATION)
        alert.title = title
        alert.headerText = null
        alert.contentText = content


        val result: Optional<ButtonType> = alert.showAndWait()
        return result.isPresent && result.get() == ButtonType.OK
    }


    // Показ діалогу помилки
    fun showErrorDialog(title: String, content: String) {
        val alert = Alert(Alert.AlertType.ERROR)
        alert.title = title
        alert.headerText = null
        alert.contentText = content
        alert.showAndWait()
    }


    // Методи для локалізації
    fun getString(key: String): String {
        return LanguageManager.getString(key)
    }


    fun getStringFormatted(key: String, vararg args: Any): String {
        return LanguageManager.getStringFormatted(key, *args)
    }
}
