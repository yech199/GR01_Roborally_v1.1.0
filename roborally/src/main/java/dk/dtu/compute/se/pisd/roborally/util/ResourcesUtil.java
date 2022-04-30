package dk.dtu.compute.se.pisd.roborally.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResourcesUtil {

    private static File[] getResourceFolderFiles (String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();
    }

    public static List<String> getBoardFileNames() {
        File[] boardnames = getResourceFolderFiles("boards");

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < boardnames.length; i++) {
            String name = boardnames[i].getName();
            result.add(name.substring(0, name.lastIndexOf(".")));
        }
        return result;
    }
}
