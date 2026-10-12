package com.example;

import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SongListCell extends ListCell<Song> {
    private final Button button = new Button();
    private final ImageView imageView = new ImageView();
    
    private final Consumer<Song> onPlayRequested; 

    public SongListCell(Consumer<Song> onPlayRequested) {
        this.onPlayRequested = onPlayRequested;

        imageView.setFitHeight(40);
        imageView.setFitWidth(40);
        imageView.setPreserveRatio(true);

        button.setGraphic(imageView);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setMaxWidth(Double.MAX_VALUE);
        
        button.setOnAction(event -> {
            Song item = getItem();
            if (item != null) {
                System.out.println("Playing song: " + item.getTitle());
                
                this.onPlayRequested.accept(item); 
            }
        });
    }
    

    @Override
    protected void updateItem(Song item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setGraphic(null); 
        } else {
            button.setText(item.getTitle());
            
            try {
                String path = item.getImagePath();
                var resource = getClass().getResource(path);
                
                if (resource != null) {
                    imageView.setImage(new Image(resource.toExternalForm()));
                } else {
                    System.err.println("Resource not found: " + path);
                    
                    var fallback = getClass().getResource("/com/example/images/basecover.png");
                    if (fallback != null) {
                        imageView.setImage(new Image(fallback.toExternalForm()));
                    }
                }
            } catch (Exception e) {
                System.err.println("Error loading image for: " + item.getTitle());
                e.printStackTrace();
            }
            
            setGraphic(button);
        }
    }
}