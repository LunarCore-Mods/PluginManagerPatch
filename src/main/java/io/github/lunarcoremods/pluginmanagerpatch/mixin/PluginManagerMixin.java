package io.github.lunarcoremods.pluginmanagerpatch.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import emu.lunarcore.plugin.PluginManager;
import net.fabricmc.loader.impl.launch.FabricLauncherBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

@Mixin(value = PluginManager.class, remap = false)
public class PluginManagerMixin {
    @Inject(method = "loadPlugins", at = @At(value = "NEW", target = "([Ljava/net/URL;)Ljava/net/URLClassLoader;", ordinal = 0))
    private void addPluginsToClasspath(CallbackInfo ci, @Local URL[] pluginURLs) throws URISyntaxException {
        for (URL url : pluginURLs) {
            FabricLauncherBase.getLauncher().addToClassPath(Paths.get(url.toURI()));
        }
    }

    @Redirect(method = "lambda$loadPlugins$4", at = @At(value = "INVOKE", target = "Ljava/net/URLClassLoader;loadClass(Ljava/lang/String;)Ljava/lang/Class;"))
    private Class<?> injectedLoadClass(URLClassLoader instance, String s) throws ClassNotFoundException {
        return this.getClass().getClassLoader().loadClass(s);
    }
}
