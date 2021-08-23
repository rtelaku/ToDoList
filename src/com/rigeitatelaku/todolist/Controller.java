package com.rigeitatelaku.todolist;

import com.rigeitatelaku.todolist.datamodel.ToDoData;
import com.rigeitatelaku.todolist.datamodel.ToDoItem;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class Controller {

    @FXML
    private ListView<ToDoItem> todoListView;
    @FXML
    private TextArea itemDetails;
    @FXML
    private Label deadlineLabel;
    private List<ToDoItem> toDoItems;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ContextMenu listContextMenu;

    @FXML
    public void showNewItemDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add new to do items");
        dialog.setHeaderText("Use this dialog to create a new to do item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("toDoItemDialog.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException ioException){
            ioException.getMessage();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK){
            DialogController controller = fxmlLoader.getController();
            ToDoItem toDoItem = controller.processResults();
            todoListView.getSelectionModel().select(toDoItem);
        } else if(result.isPresent() && result.get() == ButtonType.CANCEL){
            System.out.println("CANCEL");
        } else {
            System.out.println("None");
        }
    }

    public void deleteItem(ToDoItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Item");
        alert.setHeaderText("Delete item : " + item.getShortDescription());
        alert.setContentText("Are you sure you want to delete item?");
        Optional<ButtonType> result = alert.showAndWait();
        if((result.isPresent()) && (result.get() == ButtonType.OK)){
            ToDoData.getInstance().deleteToDoItem(item);
        }
    }

    public void initialize(){
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });

        listContextMenu.getItems().addAll(deleteMenuItem);
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoItem>() {
            @Override
            public void changed(ObservableValue<? extends ToDoItem> observableValue, ToDoItem toDoItem, ToDoItem t1) {
                if(t1 != null){
                    ToDoItem item = todoListView.getSelectionModel().getSelectedItem();
                    itemDetails.setText(item.getDetails());
                    DateTimeFormatter df = DateTimeFormatter.ofPattern("MMMM d, yyyy");
                    deadlineLabel.setText(df.format(item.getDeadline()));
                }
            }
        });
        todoListView.setItems(ToDoData.getInstance().getToDoItems());
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<ToDoItem>, ListCell<ToDoItem>>() {
            @Override
            public ListCell<ToDoItem> call(ListView<ToDoItem> toDoItemListView) {
               ListCell<ToDoItem> cell = new ListCell<>(){
                   @Override
                   protected void updateItem(ToDoItem toDoItem, boolean empty) {
                       super.updateItem(toDoItem, empty);
                       if(empty){
                           setText(null);
                       } else {
                           setText(toDoItem.getShortDescription());
                           if(toDoItem.getDeadline().equals(LocalDate.now())){
                               setTextFill(Color.RED);
                           } else if(toDoItem.getDeadline().equals(LocalDate.now().plusDays(1))){
                               setTextFill(Color.BROWN);
                           } else if(toDoItem.getDeadline().isBefore(LocalDate.now())){
                               setTextFill(Color.GREEN);
                           }
                       }
                   }
               };

               cell.emptyProperty().addListener(
                       (obs, wasEmpty, isNowEmpty) -> {
                           if(isNowEmpty){
                               cell.setContextMenu(null);
                       } else {
                               cell.setContextMenu(listContextMenu);
                           }
                });

               return cell;
            }
        });
    }
    public void handleKeyPressed(KeyEvent event){
        ToDoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            if(event.getCode().equals(KeyCode.DELETE)){
                deleteItem(selectedItem);
            }
        }
    }
}
