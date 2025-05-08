package com.example.shoppinglist


import com.example.shoppinglist.controller.ShoppingListController
import com.example.shoppinglist.model.ShoppingItem
import com.example.shoppinglist.util.LanguageManager
import com.example.shoppinglist.view.ItemEditDialog
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.Stage
import javafx.scene.input.KeyCode
import javafx.animation.FadeTransition
import javafx.util.Duration




public class ShoppingListApp : Application() {


    public  val controller = ShoppingListController()


    override fun start(primaryStage: Stage) {
        // Налаштовуємо вікно
        primaryStage.title = controller.getString("app.title")


        // Створюємо головну панель
        val root = BorderPane()


        // Створюємо форму додавання нового елементу
        val addItemForm = createAddItemForm()
        root.top = addItemForm


        // Створюємо список елементів з можливістю позначення
        val itemsList = createItemsList()
        root.center = itemsList






        // Створюємо сцену
        val scene = Scene(root, 600.0, 500.0)


        // Налаштування меню для зміни мови
        val menuBar = createMenuBar()
        (root.top as VBox).children.add(0, menuBar)


        // Відображаємо сцену
        primaryStage.scene = scene
        primaryStage.show()
    }


    public  fun createMenuBar(): MenuBar {
        val menuBar = MenuBar()


        // Меню мови
        val languageMenu = Menu("Language")


        val englishMenuItem = MenuItem("English")
        englishMenuItem.setOnAction {
            LanguageManager.switchLanguage("en")
            restartApplication()
        }


        val ukrainianMenuItem = MenuItem("Українська")
        ukrainianMenuItem.setOnAction {
            LanguageManager.switchLanguage("uk")
            restartApplication()
        }


        languageMenu.items.addAll(englishMenuItem, ukrainianMenuItem)


        menuBar.menus.add(languageMenu)
        return menuBar
    }


    public  fun createAddItemForm(): VBox {
        val addItemForm = VBox(10.0)
        addItemForm.padding = Insets(10.0)


        // Входи для форми
        val formGrid = GridPane()
        formGrid.hgap = 10.0
        formGrid.vgap = 10.0
        formGrid.alignment = Pos.CENTER_LEFT


        // Поле назви товару
        val nameLabel = Label(controller.getString("item.name"))
        val nameField = TextField()
        formGrid.add(nameLabel, 0, 0)
        formGrid.add(nameField, 1, 0)


        // Поле кількості
        val quantityLabel = Label(controller.getString("item.quantity"))
        val quantitySpinner = Spinner<Int>(1, 100, 1)
        quantitySpinner.isEditable = true
        formGrid.add(quantityLabel, 0, 1)
        formGrid.add(quantitySpinner, 1, 1)


        // Поле категорії
        val categoryLabel = Label(controller.getString("item.category"))
        val categoryField = TextField()
        formGrid.add(categoryLabel, 0, 2)
        formGrid.add(categoryField, 1, 2)


        // Кнопка додавання
        val addButton = Button(controller.getString("item.add"))
        addButton.setOnAction {
            // Отримуємо дані з форми
            val name = nameField.text.trim()
            val quantity = quantitySpinner.value
            val category = categoryField.text.trim()


            // Перевіряємо валідність даних
            if (name.isEmpty()) {
                controller.showErrorDialog("Error", controller.getString("error.nameRequired"))
                return@setOnAction
            }


            // Додаємо новий елемент
            val newItem = controller.addItem(name, quantity, category)


            // Анімуємо додавання
            animateItemAdded()


            // Очищаємо форму
            nameField.clear()
            quantitySpinner.valueFactory.setValue(1)
            categoryField.clear()
            nameField.requestFocus()
        }


        // Додаємо обробку клавіші Enter
        nameField.setOnKeyPressed { event ->
            if (event.code == KeyCode.ENTER) {
                addButton.fire()
            }
        }


        formGrid.add(addButton, 1, 3)


        // Налаштування розтягування колонок
        val col1 = ColumnConstraints()
        col1.percentWidth = 30.0
        val col2 = ColumnConstraints()
        col2.percentWidth = 70.0
        formGrid.columnConstraints.addAll(col1, col2)


        addItemForm.children.add(formGrid)
        return addItemForm
    }


    public  fun createItemsList(): VBox {
        val container = VBox(10.0)
        container.padding = Insets(10.0)


        // Заголовок списку
        val listLabel = Label(controller.getString("app.title"))
        listLabel.style = "-fx-font-size: 16px; -fx-font-weight: bold;"


        // Кнопки дій над списком
        val buttonsBox = HBox(10.0)
        buttonsBox.alignment = Pos.CENTER_RIGHT


        val deleteCheckedButton = Button(controller.getString("item.deleteChecked"))
        deleteCheckedButton.setOnAction {
            if (controller.getCheckedItemsCount() > 0) {
                val confirmed = controller.showConfirmationDialog(
                    controller.getString("item.delete"),
                    "Are you sure you want to delete all checked items?"
                )


                if (confirmed) {
                    val count = controller.deleteCheckedItems()
                    animateItemsRemoved()
                }
            }
        }


        // Прив'язка активності кнопки до кількості відмічених елементів
        deleteCheckedButton.disableProperty().bind(
            Bindings.createBooleanBinding(
                { controller.getCheckedItemsCount() == 0 },
                controller.filteredItems
            )
        )


        buttonsBox.children.add(deleteCheckedButton)


        // Список товарів
        val listView = ListView<ShoppingItem>()
        listView.items = controller.filteredItems
        listView.setCellFactory {
            object : ListCell<ShoppingItem>() {
                public  val checkBox = CheckBox()
                public  val deleteButton = Button(controller.getString("item.delete"))
                public  val editButton = Button(controller.getString("item.edit"))
                public  val hbox = HBox(10.0)


                init {
                    checkBox.setOnAction {
                        val item = item
                        if (item != null) {
                            controller.toggleItemStatus(item)
                        }
                    }


                    deleteButton.setOnAction {
                        val item = item
                        if (item != null) {
                            val confirmed = controller.showConfirmationDialog(
                                controller.getString("item.delete"),
                                "Are you sure you want to delete '${item.name}'?"
                            )


                            if (confirmed) {
                                // Анімація видалення
                                val fadeOut = FadeTransition(Duration.millis(300.0), this)
                                fadeOut.fromValue = 1.0
                                fadeOut.toValue = 0.0
                                fadeOut.setOnFinished {
                                    controller.deleteItem(item)
                                }
                                fadeOut.play()
                            }
                        }
                    }


                    editButton.setOnAction {
                        val item = item
                        if (item != null) {
                            val dialog = ItemEditDialog(item, controller)
                            dialog.showAndWait().ifPresent { result ->
                                if (result) {
                                    // Елемент оновлено в діалозі
                                    // Оновлюємо відображення
                                    updateItem(item, false)
                                }
                            }
                        }
                    }


                    hbox.alignment = Pos.CENTER_LEFT
                }


                override fun updateItem(item: ShoppingItem?, empty: Boolean) {
                    super.updateItem(item, empty)


                    if (empty || item == null) {
                        text = null
                        graphic = null
                    } else {
                        // Налаштування чекбоксу
                        checkBox.isSelected = item.isChecked


                        // Налаштування тексту елементу
                        val nameLabel = Label(item.name)
                        nameLabel.maxWidth = Double.MAX_VALUE
                        HBox.setHgrow(nameLabel, Priority.ALWAYS)


                        if (item.isChecked) {
                            nameLabel.style = "-fx-strikethrough: true; -fx-text-fill: gray;"
                        } else {
                            nameLabel.style = ""
                        }


                        // Додаткова інформація
                        val detailsLabel = Label("Qty: ${item.quantity}" +
                                (if (item.category.isNotEmpty()) ", Category: ${item.category}" else ""))
                        detailsLabel.style = "-fx-font-size: 10px; -fx-text-fill: #666;"


                        val vbox = VBox(2.0, nameLabel, detailsLabel)
                        vbox.maxWidth = Double.MAX_VALUE
                        HBox.setHgrow(vbox, Priority.ALWAYS)


                        // Компонуємо елемент
                        hbox.children.clear()
                        hbox.children.addAll(checkBox, vbox, editButton, deleteButton)


                        graphic = hbox
                    }
                }
            }
        }


        // Додаємо компоненти до контейнера
        container.children.addAll(listLabel, buttonsBox, listView)
        VBox.setVgrow(listView, Priority.ALWAYS)


        return container
    }






    public  fun animateItemAdded() {
        // Ця функція реалізує анімацію для нових елементів
        // В реальній програмі потрібно було б зв'язати це з ListView
    }


    public  fun animateItemsRemoved() {
        // Ця функція реалізує анімацію для видалених елементів
        // В реальній програмі потрібно було б зв'язати це з ListView
    }


    public  fun restartApplication() {
        // У реальній програмі тут можна було б перезапустити додаток або перезавантажити сцену
        // Але для цього прикладу просто показуємо повідомлення
        val alert = Alert(Alert.AlertType.INFORMATION)
        alert.title = "Language Changed"
        alert.headerText = null
        alert.contentText = "Please restart the application to apply language changes."
        alert.showAndWait()
    }


    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ShoppingListApp::class.java, *args)
        }
    }
}
