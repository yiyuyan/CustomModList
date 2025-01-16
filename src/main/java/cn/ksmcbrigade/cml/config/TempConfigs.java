package cn.ksmcbrigade.cml.config;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

import java.util.ArrayList;

public class TempConfigs {
    public static ArrayList<EntrypointContainer<ModInitializer>> mainPoints = new ArrayList<>();
    public static ArrayList<EntrypointContainer<ClientModInitializer>> clientPoints = new ArrayList<>();
}
