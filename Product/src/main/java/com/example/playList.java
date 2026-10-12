package com.example;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class playList {
    private String name;
    private String icon; 
    private ObservableList<Song> songData;

    public playList(String n){
        this.name = n;
        this.icon = "/com/example/images/basecover.png";
        this.songData = FXCollections.observableArrayList();
    }

    public void addTrack(String title, String image, String media){
        this.songData.add(new Song(title, image, media));
    }

    public void setName(String n){
        this.name = n;
    }

    public void setIcon(String i){
        this.icon = i;
    }

    public void setTracks(ObservableList<Song> i){
        this.songData = i;
    }

    public void deleteSong(int i){
        songData.remove(i);
    }

    public String getName(){
        return this.name;
    }

    public String getIcon(){
        return this.icon;
    }

    public ObservableList<Song> getTracks(){
        return this.songData;
    }

}
