package cn.ksmcbrigade.cml.config;

import com.google.gson.*;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {

    private final File config;

    public final ArrayList<modInfo> adds = new ArrayList<>();
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
            removes.add("cml");
            adds.add(new modInfo("inertiaanticheat","InertiaAntiCheat","0.0.7.2+1.20.1","Stop people from using unwanted mods on your server!",ModEnvironment.UNIVERSAL,"GPL-3.0", List.of("DiffuseHyperion")));
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
                adds.add(modInfo.parse(object1));
            }
        }
    }

    public JsonObject get(){
        JsonObject object = new JsonObject();
        JsonArray addArray = new JsonArray(),removeArray = new JsonArray();
        for (modInfo add : this.adds) {
            addArray.add(add.get());
        }
        for (String remove : removes) {
            removeArray.add(remove);
        }
        object.add("adds",addArray);
        object.add("removes",removeArray);
        return object;
    }

    public record modInfo(String modId, String modName, String modVersion, String describe, ModEnvironment environment, String license,List<String> modAuthors){
        public JsonObject get(){
            JsonObject object = new JsonObject();
            object.addProperty("id",modId);
            object.addProperty("name",modName);
            object.addProperty("version",modVersion);
            object.addProperty("describe",describe);
            object.addProperty("environment",environment.name());
            object.addProperty("license",license);
            object.add("authors",authors());
            return object;
        }

        private JsonArray authors(){
            JsonArray array = new JsonArray();
            for (String s : this.modAuthors) {
                array.add(s);
            }
            return array;
        }

        public static modInfo parse(JsonObject object) throws RuntimeException{
            if(!object.has("id")) throw new RuntimeException("Can not find the mod id.");
            String id = object.get("id").getAsString(),name = "mod",version = "1.0",describe = "",license = "MIT";
            ModEnvironment environment = ModEnvironment.UNIVERSAL;
            ArrayList<String> authors = new ArrayList<>();
            if(object.has("name")) name = object.get("name").getAsString();
            if(object.has("version")) version = object.get("version").getAsString();
            if(object.has("describe")) describe = object.get("describe").getAsString();
            if(object.has("license")) license = object.get("license").getAsString();
            if(object.has("environment")) environment = ModEnvironment.valueOf(object.get("environment").getAsString());
            if(object.has("authors") && object.get("authors") instanceof JsonArray array){
                for (JsonElement element : array) {
                    authors.add(element.getAsString());
                }
            }
            return new modInfo(id,name,version,describe,environment,license,authors);
        }
    }
}
