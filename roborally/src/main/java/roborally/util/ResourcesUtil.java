package roborally.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ResourcesUtil {

    private static File[] getResourceFolderFiles (String folder) throws NullPointerException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();
    }

    public static List<String> getBoardFileNames() throws NullPointerException {
        File[] boardnames = getResourceFolderFiles("boards");

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < boardnames.length; i++) {
            String name = boardnames[i].getName();
            result.add(name.substring(0, name.lastIndexOf(".")));
        }
        return result;
    }
    public static List<String> getSaveGameFiles() throws NullPointerException {
        File[] savegames = getResourceFolderFiles("savegames");

        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < savegames.length; i++) {
            String name = savegames[i].getName();
            result.add(name.substring(0, name.lastIndexOf(".")));
        }
        return result;
    }
}
