package com.example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Optional;
import java.util.Scanner;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;



public class PrimaryController {

    private MediaPlayer mediaPlayer;
    public ObservableList<Song> allPlayLists = FXCollections.observableArrayList();
    int currentSong = 0;
    int currentPlaylist = 0;
    boolean looping = false;
    @FXML
    private Button playButton;
    @FXML
    private ListView<playList> playListList;
    @FXML
    private ListView<Song> songList;
    @FXML
    private Slider volumeSlider;
    @FXML
    private Slider musicSlider;
    @FXML
    private VBox dropArea;
    @FXML
    private AnchorPane rootPane;
    private ObservableList<playList> allPlayList = FXCollections.observableArrayList();
    private FilteredList<Song> filteredSongs;
    private final String IMAGE_BASE_PATH = "/com/example/images/";


    @FXML
    public void initialize() {
        loadData();
        filteredSongs = new FilteredList<>(allPlayList.get(0).getTracks(), p -> true);
        songList.setItems(filteredSongs);
        playListList.setItems(allPlayList);
        setupDragAndDrop();

        songList.setCellFactory(lv -> new SongListCell(this::resetMediaPlayer));
        playListList.setCellFactory(lv -> new playListListCell(this::changeMainPlayList));

        Platform.runLater(() -> {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setOnCloseRequest(event -> {
            System.out.println("Application is closing... saving data.");
            saveToDatabase();
        });
    });

    }

    @FXML
    private void switchToSecondary() throws IOException {
        App.setRoot("secondary");
    }

    @FXML
    private void playSong() throws IOException {
        if (mediaPlayer != null){
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
                playButton.setText("Play");
            } else {
                mediaPlayer.play();
                double sliderTimeMillis = musicSlider.getValue();
                Duration seekTime = Duration.millis(sliderTimeMillis);
                mediaPlayer.seek(seekTime);
                playButton.setText("Pause"); 
            }
        } else {
            System.out.println("Media player is empty");
        }
    }

    @FXML
    private void searchSong() {
        TextAreaInputDialog dialog = new TextAreaInputDialog(null, "Search");
        dialog.setHeaderText("Search for a song title:");
        dialog.setGraphic(null);
        Optional<String> result = dialog.showAndWait();

        if (filteredSongs != null) {
            if (result.isPresent() && !result.get().isEmpty()) {
                String searchText = result.get().toLowerCase();
                filteredSongs.setPredicate(song -> {
                    return song.getTitle().toLowerCase().contains(searchText);
                });
            } else {
                filteredSongs.setPredicate(song -> true);
            }
        }
    }

    @FXML
    private void addSong() throws IOException {
        TextAreaInputDialog dialog = new TextAreaInputDialog(null, "Song");
        dialog.setHeaderText("Choose a song (Use file name).");
        dialog.setGraphic(null);
        Optional<String> chooseSong = dialog.showAndWait();

        dialog = new TextAreaInputDialog(null, "name");
        dialog.setHeaderText("Choose a name for the song.");
        dialog.setGraphic(null);
        Optional<String> chooseName = dialog.showAndWait();

        if (chooseSong.isPresent() && chooseName.isPresent()) {
            try {
                String defaultIcon = IMAGE_BASE_PATH + "basecover.png";
                String songPath = "/" + chooseSong.get(); 
                
                allPlayList.get(currentPlaylist).getTracks().add(new Song(chooseName.get(), defaultIcon, songPath));
            } catch (Exception f) {
                System.out.println("Error adding song. Ensure file is in resources.");
            }
        }
    }

    @FXML
    private void deleteSong() throws IOException{
        TextAreaInputDialog dialog = new TextAreaInputDialog(null,"Song");
        dialog.setHeaderText("Choose a song (Use file name).");
        dialog.setGraphic(null);
        Optional<String> chooseSong = dialog.showAndWait();

        if (chooseSong.isPresent()) {
            try {
                String deleteSong = "/" + chooseSong.get();
                for (Song s: allPlayList.get(currentPlaylist).getTracks()){
                    if (s.getMediaPath().equals(deleteSong)){
                        int deleteSongIndex = allPlayList.get(currentPlaylist).getTracks().indexOf(s);
                        allPlayList.get(currentPlaylist).getTracks().remove(deleteSongIndex);
                    }
                }
            } catch (Exception f) {
                System.out.println("The file you want to delete does not exist.");
            }
        }
    }

    @FXML
    private void addPlayList() throws IOException{
        TextAreaInputDialog dialog = new TextAreaInputDialog(null,"Playlist");
        dialog.setHeaderText("Choose a name for the new playlist.");
        dialog.setGraphic(null);
        Optional<String> chooseSong = dialog.showAndWait();

        if (chooseSong.isPresent()) {
            try {
                String newPlayListName = chooseSong.get();
                playList newPlayList = new playList(newPlayListName);
                allPlayList.add(newPlayList);
            } catch (Exception f) {
                System.out.println("Error creating new playlist.");
            }
        }
    }

    @FXML
    private void deletePlayList() throws IOException{
        TextAreaInputDialog dialog = new TextAreaInputDialog(null,"Playlist");
        dialog.setHeaderText("What playlist do you want to delete.(use index)");
        dialog.setGraphic(null);
        Optional<String> chooseSong = dialog.showAndWait();

        if (chooseSong.isPresent()) {
            try {
                int deletePlayListName = Integer.parseInt(chooseSong.get());
                allPlayList.remove(deletePlayListName);
            } catch (Exception f) {
                System.out.println("Error deleting playlist.");
            }
        }
    }

    @FXML
    private void changeSongIcon() throws IOException {
        TextAreaInputDialog dialog = new TextAreaInputDialog(null, "change song Icon");
        dialog.setHeaderText("New Icon file name (e.g., icon.png):");
        dialog.setGraphic(null);
        Optional<String> chooseSong = dialog.showAndWait();

        if (chooseSong.isPresent()) {
            try {
                String newIcon = IMAGE_BASE_PATH + chooseSong.get();
                allPlayList.get(currentPlaylist).getTracks().get(currentSong).setImagePath(newIcon);
                songList.refresh();
            } catch (Exception f) {
                System.out.println("Error loading image.");
            }
        }
    }

    @FXML
    private void changePlayListIcon() throws IOException {
        TextAreaInputDialog dialog = new TextAreaInputDialog(null, "change playlist Icon");
        dialog.setHeaderText("New Icon file name (e.g., icon.png):");
        dialog.setGraphic(null);
        Optional<String> chooseSong = dialog.showAndWait();

        if (chooseSong.isPresent()) {
            try {
                String newIcon = IMAGE_BASE_PATH + chooseSong.get();
                allPlayList.get(currentPlaylist).setIcon(newIcon);
                playListList.refresh();
            } catch (Exception f) {
                System.out.println("Error loading image.");
            }
        }
    }

    @FXML
    private void loopSong() throws IOException{
        looping = !looping;
        System.out.println(looping);
    }

    @FXML
    private void scramblePlayList() throws IOException{
        Song currentlyPlaying = null;
        if (currentSong >= 0 && currentSong < allPlayList.get(currentPlaylist).getTracks().size()) {
            currentlyPlaying = allPlayList.get(currentPlaylist).getTracks().get(currentSong);
        }

        ObservableList<Song> tracks = allPlayList.get(currentPlaylist).getTracks();
        FXCollections.shuffle(tracks);
        
        if (currentlyPlaying != null) {
            currentSong = tracks.indexOf(currentlyPlaying);
        }

        songList.setItems(null);
        songList.setItems(tracks);
        songList.refresh();
    }

    @FXML
    private void skipSongForwards() throws IOException{
        currentSong++;
        if (currentSong < allPlayList.get(currentPlaylist).getTracks().size()){
            resetMediaPlayer(allPlayList.get(currentPlaylist).getTracks().get(currentSong));
        } else {
            currentSong = 0;
            resetMediaPlayer(allPlayList.get(currentPlaylist).getTracks().get(currentSong));
        }
    }

    @FXML
    private void skipSongBackwards() throws IOException{
        currentSong--;
        if (currentSong < allPlayList.get(currentPlaylist).getTracks().size() && currentSong >= 0){
            resetMediaPlayer(allPlayList.get(currentPlaylist).getTracks().get(currentSong));
        } else {
            currentSong = 0;
            resetMediaPlayer(allPlayList.get(currentPlaylist).getTracks().get(currentSong));
        }
    }

    private void nextSong() throws IOException{
        currentSong++;
        if (currentSong < allPlayList.get(currentPlaylist).getTracks().size()){
            resetMediaPlayer(allPlayList.get(currentPlaylist).getTracks().get(currentSong));
        } else {
            currentSong = 0;
            resetMediaPlayer(allPlayList.get(currentPlaylist).getTracks().get(currentSong));
        }
    }

    public void resetSong(){
        mediaPlayer.seek(Duration.ZERO);
        mediaPlayer.play();
    }

    @FXML
    public void sortSongs(){
        Song currentlyPlaying = null;
        if (currentSong >= 0 && currentSong < allPlayList.get(currentPlaylist).getTracks().size()) {
            currentlyPlaying = allPlayList.get(currentPlaylist).getTracks().get(currentSong);
        }

        ObservableList<Song> tracks = allPlayList.get(currentPlaylist).getTracks();
        FXCollections.sort(tracks, Comparator.comparing(Song::getTitle, String.CASE_INSENSITIVE_ORDER));

        if (currentlyPlaying != null) {
            currentSong = tracks.indexOf(currentlyPlaying);
        }

        songList.setItems(null);
        songList.setItems(tracks);
        songList.refresh();
    }

    @FXML
    public void reverseSortSongs(){
        Song currentlyPlaying = null;
        if (currentSong >= 0 && currentSong < allPlayList.get(currentPlaylist).getTracks().size()) {
            currentlyPlaying = allPlayList.get(currentPlaylist).getTracks().get(currentSong);
        }

        ObservableList<Song> tracks = allPlayList.get(currentPlaylist).getTracks();
        FXCollections.reverse(tracks);

        if (currentlyPlaying != null) {
            currentSong = tracks.indexOf(currentlyPlaying);
        }

        songList.setItems(null);
        songList.setItems(tracks);
        songList.refresh();
    }

    @FXML
    public void setupDragAndDrop() {
        dropArea.setOnDragOver(event -> {
            if (event.getGestureSource() != dropArea && event.getDragboard().hasFiles()) {
                boolean hasMp3 = event.getDragboard().getFiles().stream()
                        .anyMatch(file -> file.getName().toLowerCase().endsWith(".mp3"));
                
                if (hasMp3) {
                    event.acceptTransferModes(TransferMode.COPY);
                }
            }
            event.consume();
        });

        dropArea.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    for (File file : db.getFiles()) {
                        if (file.getName().toLowerCase().endsWith(".mp3")) {
                            copyFileToResources(file);
                        }
                    }
                    success = true;
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }

        private void copyFileToResources(File sourceFile) {
            try {
                File destDir = new File("src/main/resources/");
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, sourceFile.getName());
                
                java.nio.file.Files.copy(sourceFile.toPath(), destFile.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                System.out.println("Imported: " + sourceFile.getName());
            } catch (IOException e) {
                System.err.println("Failed to copy file: " + e.getMessage());
            }
        }

    public void resetMediaPlayer(Song selectedSong) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
        }

        try {
            currentSong = allPlayList.get(currentPlaylist).getTracks().indexOf(selectedSong);

            String path = getClass().getResource(selectedSong.getMediaPath()).toExternalForm();
            Media media = new Media(path);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.volumeProperty().bindBidirectional(volumeSlider.valueProperty());
            mediaPlayer.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
                if (musicSlider != null && !musicSlider.isValueChanging()) {
                    musicSlider.setValue(newValue.toMillis());
                    }
            });

            mediaPlayer.setOnEndOfMedia(() -> {
                if (looping) {
                    mediaPlayer.seek(Duration.ZERO);
                    mediaPlayer.play();
                } else {
                    try {
                        nextSong();
                    } catch (IOException e) {
                        System.err.println("Failed to skip to next song: " + e.getMessage());
                    }
                }
            });

            mediaPlayer.setOnReady(() -> {
                musicSlider.setMax(media.getDuration().toMillis());
            });
                
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Error loading: " + selectedSong.getMediaPath());
        }
    }


    public void changeMainPlayList(playList selected){
        if (selected != null){
            filteredSongs = new FilteredList<>(selected.getTracks(), p -> true);
            songList.setItems(filteredSongs);
            currentPlaylist = allPlayList.indexOf(selected);
            currentSong = 0; 
        }
    }

    private void loadData() {
        File dbFile = new File("playlist_save.txt");
        if (!dbFile.exists()) return;

        try (Scanner fileScanner = new Scanner(dbFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.isEmpty()) continue;

                Scanner lineScanner = new Scanner(line);
                lineScanner.useDelimiter(";");

                if (lineScanner.hasNext()) {
                    String name = lineScanner.next();
                    String icon = lineScanner.next();
                    
                    playList newPlayList = new playList(name);
                    newPlayList.setIcon(icon);
                    allPlayList.add(newPlayList);

                    while (lineScanner.hasNext()) {
                        String songChunk = lineScanner.next(); 
                        
                        String[] songParts = songChunk.split("\\|"); 
                        
                        if (songParts.length >= 3) {
                            String title = songParts[0];
                            String img = songParts[1]; 
                            String path = songParts[2];
                            
                            newPlayList.getTracks().add(new Song(title, img, path));
                        }
                    }
                }
                lineScanner.close();
            }
        } catch (FileNotFoundException e) {
            System.out.println("No save file found.");
        }
    }

    private void saveToDatabase() {
        String path = System.getProperty("user.dir") + File.separator + "playlist_save.txt";
        File dbFile = new File(path);
        try (PrintWriter writer = new PrintWriter(new FileWriter(dbFile, false))) { 
            for (playList pl : allPlayList) {
                writer.println(serializePlaylist(pl));
            }
        } catch (IOException e) {
            System.err.println("Could not update database: " + e.getMessage());
        }
    }

    private String serializePlaylist(playList pl) {
        StringBuilder sb = new StringBuilder();
        sb.append(pl.getName()).append(";").append(pl.getIcon()).append(";");

        for (Song s : pl.getTracks()) {
            sb.append(s.getTitle()).append("|")
            .append(s.getImagePath()).append("|") 
            .append(s.getMediaPath()).append(";"); 
        }
        
        return sb.toString();
    }
}
