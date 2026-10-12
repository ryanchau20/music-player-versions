package com.example;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class playListListCell extends ListCell<playList> {
    private final Button button = new Button();
    private final ImageView imageView = new ImageView();
    private final Consumer<playList> onPlaylistSelected;

    public playListListCell(Consumer<playList> onPlaylistSelected) {
        this.onPlaylistSelected = onPlaylistSelected;

        imageView.setFitHeight(40);
        imageView.setFitWidth(40);
        imageView.setPreserveRatio(true);

        button.setGraphic(imageView);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setMaxWidth(Double.MAX_VALUE);

        button.setOnAction(event -> {
            playList item = getItem();
            if (item != null) {
                this.onPlaylistSelected.accept(item);
            }
        });
    }

    @Override
    protected void updateItem(playList item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null);
        } else {
            button.setText(item.getName());

            try {
                String path = item.getIcon();
                var resource = getClass().getResource(path);

                if (resource != null) {
                    imageView.setImage(new Image(resource.toExternalForm()));
                } else {
                    System.err.println("Playlist icon not found: " + path);
                    
                    var fallback = getClass().getResource("/com/example/images/basecover.png");
                    if (fallback != null) {
                        imageView.setImage(new Image(fallback.toExternalForm()));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading image for playlist: " + item.getName());
                e.printStackTrace();
            }

            setGraphic(button);
        }
    }
}