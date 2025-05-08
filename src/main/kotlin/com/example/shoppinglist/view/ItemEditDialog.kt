package com.example.shoppinglist.view


import com.example.shoppinglist.controller.ShoppingListController
import com.example.shoppinglist.model.ShoppingItem
import javafx.geometry.Insets
import javafx.scene.control.*
import javafx.scene.layout.GridPane
import javafx.stage.Modality


class ItemEditDialog(
    private val item: ShoppingItem,
    private val controller: ShoppingListController
) : Dialog<Boolean>() {


    init {
        title = controller.getString("dialog.edit.title")
        headerText = null
        initModality(Modality.APPLICATION_MODAL)


        // Кнопки - виправлено!
        dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)


        // Створюємо форму
        val grid = GridPane()
        grid.hgap = 10.0
        grid.vgap = 10.0
        grid.padding = Insets(20.0, 10.0, 10.0, 10.0)


        // Поле назви
        val nameField = TextField(item.name)
        grid.add(Label(controller.getString("item.name")), 0, 0)
        grid.add(nameField, 1, 0)


        // Поле кількості
        val quantitySpinner = Spinner<Int>(1, 100, item.quantity)
        quantitySpinner.isEditable = true
        grid.add(Label(controller.getString("item.quantity")), 0, 1)
        grid.add(quantitySpinner, 1, 1)


        // Поле категорії
        val categoryField = TextField(item.category)
        grid.add(Label(controller.getString("item.category")), 0, 2)
        grid.add(categoryField, 1, 2)


        // Встановлюємо вміст діалогу
        dialogPane.content = grid


        // Конвертуємо результат натискання кнопки в boolean (true, якщо OK)
        setResultConverter { dialogButton ->
            if (dialogButton == ButtonType.OK) {
                val name = nameField.text.trim()


                // Перевіряємо валідність даних
                if (name.isEmpty()) {
                    controller.showErrorDialog("Error", controller.getString("error.nameRequired"))
                    return@setResultConverter false
                }


                val quantity = quantitySpinner.value
                val category = categoryField.text.trim()


                // Оновлюємо елемент
                item.name = name
                item.quantity = quantity
                item.category = category


                return@setResultConverter true
            }
            false
        }
    }
}

