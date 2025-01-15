package cn.ksmcbrigade.cml.utils;

import cn.ksmcbrigade.cml.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.metadata.*;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.fabricmc.loader.impl.ModContainerImpl;
import net.fabricmc.loader.impl.discovery.ModCandidate;
import net.fabricmc.loader.impl.metadata.*;
import net.fabricmc.loader.impl.util.version.StringVersion;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Paths;
import java.util.*;

public class ModListUtils {
    public static modEntryPoints remove(String modId) throws NoSuchFieldException, IllegalAccessException {
        modEntryPoints result = getEntryPoints(modId);

        FabricLoaderImpl loader = FabricLoaderImpl.INSTANCE;

        if(!loader.isModLoaded(modId)) return result;

        Field modMapField = loader.getClass().getDeclaredField("modMap");
        Field modCandidatesField = loader.getClass().getDeclaredField("modCandidates");
        Field modsField = loader.getClass().getDeclaredField("mods");
        Field modEntryPointsField = loader.getClass().getDeclaredField("entrypointStorage");

        modsField.setAccessible(true);
        modMapField.setAccessible(true);
        modCandidatesField.setAccessible(true);
        modEntryPointsField.setAccessible(true);

        Map<String, ModContainerImpl> modMap = new java.util.HashMap<>(Map.copyOf((Map<String, ModContainerImpl>) modMapField.get(loader)));
        ArrayList<ModCandidate> modCandidates = modCandidatesField.get(loader)==null?new ArrayList<>():new ArrayList<>((List<ModCandidate>)modCandidatesField.get(loader));
        List<ModContainerImpl> mods = new ArrayList<>((List<ModContainerImpl>)modsField.get(loader));

        modMap.remove(modId);
        ModCandidate candidate = null;
        for (ModCandidate modCandidate : modCandidates) {
            if(modCandidate.getMetadata().getId().equalsIgnoreCase(modId)){
                candidate = modCandidate;
                break;
            }
        }
        if(candidate!=null) modCandidates.remove(candidate);
        ModContainerImpl container = null;
        for (ModContainerImpl modContainer : mods) {
            if(modContainer.getMetadata().getId().equalsIgnoreCase(modId)){
                container = modContainer;
                break;
            }
        }
        if(container!=null) mods.remove(container);

        modMapField.set(loader,modMap);
        modCandidatesField.set(loader,modCandidates);
        modsField.set(loader,mods);

        return result;
    }

    public static void add(Config.modInfo info) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Method createMethod = ModCandidate.class.getDeclaredMethod("createPlain", List.class, LoaderModMetadata.class, boolean.class, Collection.class);
        LoaderModMetadata metadata = new LoaderModMetadata() {
            @Override
            public int getSchemaVersion() {
                return 1;
            }

            @Override
            public Map<String, String> getLanguageAdapterDefinitions() {
                return Map.of();
            }

            @Override
            public Collection<NestedJarEntry> getJars() {
                return List.of();
            }

            @Override
            public Collection<String> getMixinConfigs(EnvType type) {
                return List.of();
            }

            @Override
            public String getAccessWidener() {
                return null;
            }

            @Override
            public boolean loadsInEnvironment(EnvType type) {
                return true;
            }

            @Override
            public Collection<String> getOldInitializers() {
                return List.of();
            }

            @Override
            public List<EntrypointMetadata> getEntrypoints(String type) {
                return List.of();
            }

            @Override
            public Collection<String> getEntrypointKeys() {
                return List.of();
            }

            @Override
            public void emitFormatWarnings() {

            }

            @Override
            public void setVersion(Version version) {

            }

            @Override
            public void setDependencies(Collection<ModDependency> dependencies) {

            }

            @Override
            public String getType() {
                return "fabric";
            }

            @Override
            public String getId() {
                return info.modId();
            }

            @Override
            public Collection<String> getProvides() {
                return List.of();
            }

            @Override
            public Version getVersion() {
                return new StringVersion(info.modVersion());
            }

            @Override
            public ModEnvironment getEnvironment() {
                return info.environment();
            }

            @Override
            public Collection<ModDependency> getDependencies() {
                return List.of();
            }

            @Override
            public String getName() {
                return info.modName();
            }

            @Override
            public String getDescription() {
                return info.describe();
            }

            @Override
            public Collection<Person> getAuthors() {
                ArrayList<Person> authors = new ArrayList<>();
                for (String modAuthor : info.modAuthors()) {
                    authors.add(new Person() {
                        @Override
                        public String getName() {
                            return modAuthor;
                        }

                        @Override
                        public ContactInformation getContact() {
                            return ContactInformation.EMPTY;
                        }
                    });
                }
                return authors;
            }

            @Override
            public Collection<Person> getContributors() {
                return List.of();
            }

            @Override
            public ContactInformation getContact() {
                return ContactInformation.EMPTY;
            }

            @Override
            public Collection<String> getLicense() {
                return List.of(info.license());
            }

            @Override
            public Optional<String> getIconPath(int size) {
                return Optional.empty();
            }

            @Override
            public boolean containsCustomValue(String key) {
                return false;
            }

            @Override
            public CustomValue getCustomValue(String key) {
                return null;
            }

            @Override
            public Map<String, CustomValue> getCustomValues() {
                return Map.of();
            }

            @Override
            public boolean containsCustomElement(String key) {
                return false;
            }
        };
        createMethod.setAccessible(true);
        ModCandidate candidate = (ModCandidate) createMethod.invoke(null,Collections.singletonList(Paths.get(System.getProperty("java.home"))),metadata,false,Collections.emptyList());
        ModContainerImpl container = new ModContainerImpl(candidate);

        FabricLoaderImpl loader = FabricLoaderImpl.INSTANCE;
        Field modMapField = loader.getClass().getDeclaredField("modMap");
        Field modCandidatesField = loader.getClass().getDeclaredField("modCandidates");
        Field modsField = loader.getClass().getDeclaredField("mods");

        modsField.setAccessible(true);
        modMapField.setAccessible(true);
        modCandidatesField.setAccessible(true);

        Map<String, ModContainerImpl> modMap = new java.util.HashMap<>(Map.copyOf((Map<String, ModContainerImpl>) modMapField.get(loader)));
        ArrayList<ModCandidate> modCandidates = modCandidatesField.get(loader)==null?new ArrayList<>():new ArrayList<>((List<ModCandidate>)modCandidatesField.get(loader));
        List<ModContainerImpl> mods = new ArrayList<>((List<ModContainerImpl>)modsField.get(loader));

        modMap.put(info.modId(),container);
        modCandidates.add(candidate);
        mods.add(container);

        modMapField.set(loader,modMap);
        modCandidatesField.set(loader,modCandidates);
        modsField.set(loader,mods);
    }

    private static modEntryPoints getEntryPoints(String modId){
        ArrayList<EntrypointContainer<ModInitializer>> mains = new ArrayList<>();
        ArrayList<EntrypointContainer<ClientModInitializer>> clients = new ArrayList<>();
        for (EntrypointContainer<ModInitializer> container : FabricLoaderImpl.INSTANCE.getEntrypointContainers("main", ModInitializer.class)) {
            if(container.getProvider().getMetadata().getId().equalsIgnoreCase(modId)){
                mains.add(container);
            }
        }
        for (EntrypointContainer<ClientModInitializer> container : FabricLoaderImpl.INSTANCE.getEntrypointContainers("client", ClientModInitializer.class)) {
            if(container.getProvider().getMetadata().getId().equalsIgnoreCase(modId)){
                clients.add(container);
            }
        }
        return new modEntryPoints(mains,clients);
    }

    public record modEntryPoints(ArrayList<EntrypointContainer<ModInitializer>> mains, ArrayList<EntrypointContainer<ClientModInitializer>> clients){}
}
