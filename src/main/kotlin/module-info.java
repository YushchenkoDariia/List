module com.example.shoppinglist {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;
    requires jdk.javadoc;


    opens com.example.shoppinglist to javafx.graphics;
    exports com.example.shoppinglist;
}