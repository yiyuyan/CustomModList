package cn.ksmcbrigade.cml;

import cn.ksmcbrigade.cml.config.Config;
import cn.ksmcbrigade.cml.utils.ModListUtils;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public class CustomModList implements PreLaunchEntrypoint {

    public static final Logger LOGGER = LogManager.getLogger();

    public static final Config config;

    static {
        try {
            config = new Config(new File("config/cml-config.json"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onPreLaunch() {
        LOGGER.info("Custom Mod List mod loaded.");
        for (Config.modInfo add : config.adds) {
            try {
                ModListUtils.add(add);
                LOGGER.info("Added a fake mod: {}", add.modName());
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | NoSuchFieldException e) {
                LOGGER.info("Failed to add a fake mod: {}",add.modId(),e);
            }
        }
        for (String remove : config.removes) {
            try {
                ModListUtils.remove(remove);
                LOGGER.info("Removed a mod in the mod list: {}", remove);
            } catch (IllegalAccessException | NoSuchFieldException e) {
                LOGGER.info("Failed to add a fake mod: {}",remove,e);
            }
        }
    }
}
