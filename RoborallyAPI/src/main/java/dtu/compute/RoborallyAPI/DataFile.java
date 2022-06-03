package dtu.compute.RoborallyAPI;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DataFile {
    private String fileName;

    DataFile(String jsonFileName) {
        fileName = jsonFileName;
    }

    String load() throws IOException {
        return new String(Files.readAllBytes(Paths.get(fileName)));
    }
}
