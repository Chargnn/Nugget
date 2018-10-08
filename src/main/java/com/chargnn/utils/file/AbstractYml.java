package com.chargnn.utils.file;

import com.chargnn.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class AbstractYml {

    public static Main main;
    private String fileName;
    private File file;
    protected FileConfiguration fileConfiguration;

    public AbstractYml(Main main, String fileName) throws IOException {
        this.main = main;
        this.fileName = fileName;

        if(!main.getDataFolder().exists()){
            main.getDataFolder().mkdirs();
        }

        file = new File(main.getDataFolder(), this.fileName);

        if (!file.exists())
            file.createNewFile();

        fileConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    public void save() throws IOException {
        fileConfiguration.save(file);
    }
}
