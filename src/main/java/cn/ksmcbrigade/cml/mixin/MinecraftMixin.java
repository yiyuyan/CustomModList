package cn.ksmcbrigade.cml.mixin;

import cn.ksmcbrigade.cml.config.TempConfigs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftMixin {
    @Inject(method = "<init>",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resource/DefaultClientResourcePackProvider;<init>(Ljava/nio/file/Path;)V"))
    public void init(RunArgs args, CallbackInfo ci){
        for (EntrypointContainer<ModInitializer> mainPoint : TempConfigs.mainPoints) {
            mainPoint.getEntrypoint().onInitialize();
        }
        for (EntrypointContainer<ClientModInitializer> clientPoint : TempConfigs.clientPoints) {
            clientPoint.getEntrypoint().onInitializeClient();
        }
        TempConfigs.mainPoints = null;
        TempConfigs.clientPoints = null;
    }
}
