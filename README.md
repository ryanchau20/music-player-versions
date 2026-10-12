# JavaFX Music Player

A desktop music player for people who own their music as local MP3 files instead of renting it
from a streaming service. Built with **Java + JavaFX** and **Maven**.

Playlists are persisted to a plain text save file — there is no database server to install.

## Features

- Play / pause, skip forwards and backwards, seek bar, volume slider
- Loop the current song, or let playback auto-advance through the playlist
- Create and delete playlists, each with a custom icon
- Custom icons for individual songs
- Shuffle a playlist, sort it A→Z and Z→A
- Search / filter songs by title
- Drag and drop `.mp3` files to import them

## Project layout

```
Product/
├── pom.xml               # JavaFX 17.0.10, Java 11, javafx-maven-plugin, resource packaging
├── playlist_save.txt     # runtime database (ships seeded; rewritten on exit)
└── src/main/
    ├── java/com/example/
    │   ├── App.java                  # entry point, scene/FXML switching
    │   ├── PrimaryController.java    # all UI logic: playback, CRUD, sort, save/load
    │   ├── Song.java                 # song model (title, cover, media path)
    │   ├── playList.java             # playlist model (name, icon, tracks)
    │   ├── SongListCell.java         # custom song row (cover art + play button)
    │   ├── playListListCell.java     # custom playlist row (icon + select)
    │   ├── TextAreaInputDialog.java  # reusable text prompt dialog
    │   ├── module-info.java
    │   └── images/                   # cover art used by the UI
    └── resources/com/example/
        ├── primary.fxml               # main UI
        └── secondary.fxml             # secondary view
```

## Requirements

| Tool | Version |
| --- | --- |
| JDK | 11 or newer (built with `--release 11`) |
| Maven | 3.6 or newer |

JavaFX 17.0.10 is downloaded by Maven automatically — no separate JavaFX SDK install is needed.

## Running

```bash
cd Product
mvn javafx:run
```

Full rebuild:

```bash
mvn clean javafx:run
```

Cover art lives in `src/main/java/com/example/images/` and is shipped by a `<resources>` entry
in `pom.xml`, so the icons survive `mvn clean`.

## The save file

`Product/playlist_save.txt` ships seeded with one playlist so the app opens straight away:

```
My Music;/com/example/images/basecover.png;
```

Format: `<playlist name>;<playlist icon>;<title>|<cover>|<media file>;...` — one playlist per
line, `;` between records, `|` between a song's fields. The file is rewritten whenever the
window closes and must never be empty: startup reads the first playlist unconditionally, so an
empty file fails with `IndexOutOfBoundsException`.

## Notes

- Media files are loaded from the classpath, so restart the app after importing an MP3.
- The code contains no comments — `pom.xml`, sources and FXML are comment-free by design.
