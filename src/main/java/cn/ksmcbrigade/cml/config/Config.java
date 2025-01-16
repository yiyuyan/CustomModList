package cn.ksmcbrigade.cml.config;

import cn.ksmcbrigade.cml.utils.ModListUtils;
import com.google.gson.*;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Config {

    private final File config;

    public final ArrayList<ModListUtils.modInfo> adds = new ArrayList<>();
    public final ArrayList<String> removes = new ArrayList<>();

    public Config(File file) throws IOException {
        this.config = file;
        this.save(false);
        this.load();
    }

    public void save(boolean e) throws IOException{
        if(!config.exists() || e){
            removes.add("wurst");
            removes.add("meteor");
            /*removes.add("cml");
            adds.add(new modInfo("inertiaanticheat","InertiaAntiCheat","0.0.7.2+1.20.1","Stop people from using unwanted mods on your server!",ModEnvironment.UNIVERSAL,"GPL-3.0", List.of("DiffuseHyperion")));*/
            FileUtils.writeStringToFile(this.config,new GsonBuilder().setPrettyPrinting().create().toJson(get()));
        }
    }

    public void load() throws IOException{
        this.adds.clear();
        this.removes.clear();

        JsonObject object = JsonParser.parseString(FileUtils.readFileToString(this.config)).getAsJsonObject();
        for (JsonElement element : object.getAsJsonArray("removes")) {
            this.removes.add(element.getAsString());
        }
        for (JsonElement element : object.getAsJsonArray("adds")) {
            if(element instanceof JsonObject object1){
                adds.add(ModListUtils.modInfo.parse(object1));
            }
        }
    }

    public JsonObject get(){
        JsonObject object = new JsonObject();
        JsonArray addArray = new JsonArray(),removeArray = new JsonArray();
        for (ModListUtils.modInfo add : this.adds) {
            addArray.add(add.get());
        }
        for (String remove : removes) {
            removeArray.add(remove);
        }
        object.add("adds",addArray);
        object.add("removes",removeArray);
        return object;
    }
}
