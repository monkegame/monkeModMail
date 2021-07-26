package online.monkegame.monkemodmail;

import java.io.File;
import java.io.IOException;

public class Database {

    public String path = System.getProperty("user.dir") + System.getProperty("file.separator") + "plugins" + System.getProperty("file.separator") + "monkeModMail" +  System.getProperty("file.separator");

    public String createDB() throws IOException {

        File db = new File(path + "files.db");
        boolean dbY = db.createNewFile();
        if (dbY) {
            return "Database created!";
        } else {
            return "Database already exists, using existing database!";
        }
    }


}
