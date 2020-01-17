package logic;

import java.io.*;
import java.nio.file.*;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileCreator {
    private StandardOpenOption onOpen, onExisting;

    public FileCreator() {
        onOpen = CREATE;
        onOpen = APPEND;
    }

    public FileCreator(StandardOpenOption onOpen, StandardOpenOption onExisting) {
        this.onOpen = onOpen;
        this.onExisting = onExisting;
    }

    public void write(String path,  String text) {
        byte[] data = text.getBytes();
        Path p = Paths.get(path);
        try (OutputStream out = new BufferedOutputStream(
                Files.newOutputStream(p, onOpen, onExisting))) {
            out.write(data, 0, data.length);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
