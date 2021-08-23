package com.rigeitatelaku.todolist;

import com.rigeitatelaku.todolist.datamodel.ToDoData;
import com.rigeitatelaku.todolist.datamodel.ToDoItem;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class DialogController {
    @FXML
    private TextField shortDescriptionField;
    @FXML
    private TextArea detailsField;
    @FXML
    private DatePicker deadlineField;

    public ToDoItem processResults(){
        String shortDescription = shortDescriptionField.getText().trim();
        String details = detailsField.getText().trim();
        LocalDate deadline = deadlineField.getValue();

        ToDoItem toDoItem = new ToDoItem(shortDescription, details, deadline);
        ToDoData.getInstance().addToDoItem(toDoItem);
        return toDoItem;
    }
}
