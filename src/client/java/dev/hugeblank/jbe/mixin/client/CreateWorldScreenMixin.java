package dev.hugeblank.jbe.mixin.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.WorldCreator;
import net.minecraft.resource.DataConfiguration;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;
import java.util.function.Consumer;

@Mixin(CreateWorldScreen.class)
public abstract class CreateWorldScreenMixin {
    @Shadow @Final WorldCreator worldCreator;

    @Shadow @Nullable protected abstract Pair<Path, ResourcePackManager> getScannedPack(DataConfiguration dataConfiguration);

    @Shadow protected abstract void applyDataPacks(ResourcePackManager dataPackManager, boolean fromPackScreen, Consumer<DataConfiguration> configurationSetter);

    @Shadow abstract void openExperimentsScreen(DataConfiguration dataConfiguration);

    @Inject(at = @At("TAIL"), method = "init")
    private void jbe$init(CallbackInfo ci) {
        @Nullable Pair<Path, ResourcePackManager> pair = this.getScannedPack(this.worldCreator.getGeneratorOptionsHolder().dataConfiguration());
        if (pair != null) {
            ResourcePackManager resourcePackManager = pair.getSecond();
            for (ResourcePackProfile resourcePackProfile : resourcePackManager.getProfiles()) {
                if (resourcePackProfile.getSource() == ResourcePackSource.FEATURE && resourcePackProfile.getName().equals("trade_rebalance")) {
                    resourcePackManager.enable(resourcePackProfile.getName());
                }
            }
            this.applyDataPacks(resourcePackManager, false, this::openExperimentsScreen);
        }
    }
}
