package com.example;

import javafx.application.Platform;
import javafx.beans.NamedArg;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class TextAreaInputDialog extends Dialog<String> {
    private final TextArea textArea;
    private final String defaultValue;

    public TextAreaInputDialog() {
        this("", "");
    }

    public TextAreaInputDialog(@NamedArg("defaultValue") String defaultValue, String title) {
        this.defaultValue = defaultValue;
        
        setTitle(title);
        DialogPane dialogPane = getDialogPane();
        dialogPane.getStyleClass().add("text-input-dialog");
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        this.textArea = new TextArea(defaultValue);
        this.textArea.setWrapText(true); 
        VBox.setVgrow(textArea, Priority.ALWAYS); 

        VBox container = new VBox(textArea);
        container.setSpacing(10);
        dialogPane.setContent(container);

        setResultConverter(button -> {
            if (button != null && button.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                return textArea.getText();
            }
            return null;
        });

        Platform.runLater(textArea::requestFocus);
    }

    public final TextArea getEditor() {
        return textArea;
    }

    public final String getDefaultValue() {
        return defaultValue;
    }
}