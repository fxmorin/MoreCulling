package ca.fxco.moreculling.config;

import ca.fxco.moreculling.MoreCulling;
import ca.fxco.moreculling.config.cloth.*;
import ca.fxco.moreculling.config.option.LeavesCullingMode;
import ca.fxco.moreculling.utils.CompatUtils;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuConfig implements ModMenuApi {

    private static Screen createConfigScreen(Screen parent) {
        ConfigBuilder builder = MoreCullingConfigBuilder.create().setParentScreen(parent);
        builder.setSavingRunnable(() -> AutoConfig.getConfigHolder(MoreCullingConfig.class).save());
        ConfigCategory generalCategory = builder.getOrCreateCategory(Text.translatable("moreculling.config.category.general"));

        // Leaves Culling
        DynamicIntSliderEntry leavesCullingDepth = new DynamicIntSliderBuilder(Text.translatable("moreculling.config.option.leavesCullingDepth"), 1, 4)
                .setValue(MoreCulling.CONFIG.leavesCullingDepth)
                .setDefaultValue(2)
                .setTooltip(Text.translatable("moreculling.config.option.leavesCullingDepth.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.leavesCullingDepth = newValue;
                    MinecraftClient.getInstance().worldRenderer.reload();
                })
                .setModLimited(CompatUtils.IS_CULLLESSLEAVES_LOADED, Text.translatable("moreculling.config.option.mangroveOnly", "cull-less-leaves"))
                .build();
        DynamicEnumEntry<LeavesCullingMode> leavesCullingMode = new DynamicEnumBuilder<>(Text.translatable("moreculling.config.option.leavesCulling"), LeavesCullingMode.class)
                .setValue(MoreCulling.CONFIG.leavesCullingMode)
                .setDefaultValue(LeavesCullingMode.DEFAULT)
                .setTooltip(Text.translatable("moreculling.config.option.leavesCulling.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.leavesCullingMode = newValue;
                    MinecraftClient.getInstance().worldRenderer.reload();
                })
                .setChangeConsumer((instance,value) -> {
                    leavesCullingDepth.setEnabledState(instance.isEnabled() && value == LeavesCullingMode.DEPTH);
                    if (MoreCulling.CONFIG.includeMangroveRoots && value == LeavesCullingMode.STATE)
                        instance.setValue(LeavesCullingMode.CHECK);
                })
                .setModLimited(CompatUtils.IS_CULLLESSLEAVES_LOADED, Text.translatable("moreculling.config.option.mangroveOnly", "cull-less-leaves"))
                .build();
        DynamicBooleanListEntry includeMangroveRoots = new DynamicBooleanBuilder(Text.translatable("moreculling.config.option.includeMangroveRoots"))
                .setValue(MoreCulling.CONFIG.includeMangroveRoots)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("moreculling.config.option.includeMangroveRoots.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.includeMangroveRoots = newValue;
                    MinecraftClient.getInstance().worldRenderer.reload();
                })
                .setChangeConsumer((instance, value) -> {
                    if (CompatUtils.IS_CULLLESSLEAVES_LOADED) leavesCullingMode.setEnabledState(value);
                    if (value && leavesCullingMode.getValue() == LeavesCullingMode.STATE)
                        leavesCullingMode.setValue(LeavesCullingMode.CHECK);
                })
                .build();

        // Powder Snow Culling
        DynamicBooleanListEntry powderSnowCulling = new DynamicBooleanBuilder(Text.translatable("moreculling.config.option.powderSnowCulling"))
                .setValue(MoreCulling.CONFIG.powderSnowCulling)
                .setDefaultValue(false)
                .setTooltip(Text.translatable("moreculling.config.option.powderSnowCulling.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.powderSnowCulling = newValue;
                    MinecraftClient.getInstance().worldRenderer.reload();
                })
                .build();

        // BlockStates
        generalCategory.addEntry(new DynamicBooleanBuilder(Text.translatable("moreculling.config.option.blockStateCulling"))
                .setValue(MoreCulling.CONFIG.useBlockStateCulling)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.blockStateCulling.tooltip"))
                .setSaveConsumer(newValue -> {
                    MoreCulling.CONFIG.useBlockStateCulling = newValue;
                    MinecraftClient.getInstance().worldRenderer.reload();
                })
                .setChangeConsumer((instance, value) -> {
                    leavesCullingMode.setEnabledState(value);
                    includeMangroveRoots.setEnabledState(value);
                    powderSnowCulling.setEnabledState(value);
                })
                .build());

        // Item Frames
        DynamicIntSliderEntry itemFrameLODRange = new DynamicIntSliderBuilder(Text.translatable("moreculling.config.option.itemFrameLODRange"), 48, 768) // Between 16 & 256 blocks - 1 & 16 chunks
                .setValue(MoreCulling.CONFIG.itemFrameLODRange)
                .setDefaultValue(384)
                .setTooltip(Text.translatable("moreculling.config.option.itemFrameLODRange.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.itemFrameLODRange = newValue)
                .build();
        DynamicBooleanListEntry itemFrameLOD = new DynamicBooleanBuilder(Text.translatable("moreculling.config.option.itemFrameLOD"))
                .setValue(MoreCulling.CONFIG.useItemFrameLOD)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.itemFrameLOD.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useItemFrameLOD = newValue)
                .setChangeConsumer((instance, value) -> itemFrameLODRange.setEnabledState(value))
                .build();
        DynamicFloatSliderEntry itemFrame3FaceCullingRange = new DynamicFloatSliderBuilder(Text.translatable("moreculling.config.option.itemFrame3FaceCullingRange"), 0.0F, 48.0F, 0.5F) // Between 0 & 16 blocks
                .setValue(MoreCulling.CONFIG.itemFrame3FaceCullingRange)
                .setDefaultValue(12.0F)
                .setTooltip(Text.translatable("moreculling.config.option.itemFrame3FaceCullingRange.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.itemFrame3FaceCullingRange = newValue)
                .build();
        DynamicBooleanListEntry itemFrame3FaceCulling = new DynamicBooleanBuilder(Text.translatable("moreculling.config.option.itemFrame3FaceCulling"))
                .setValue(MoreCulling.CONFIG.useItemFrame3FaceCulling)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.itemFrame3FaceCulling.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useItemFrame3FaceCulling = newValue)
                .setChangeConsumer((instance, value) -> itemFrame3FaceCullingRange.setEnabledState(value))
                .build();
        generalCategory.addEntry(new DynamicBooleanBuilder(Text.translatable("moreculling.config.option.customItemFrameRenderer"))
                .setValue(MoreCulling.CONFIG.useCustomItemFrameRenderer)
                .setDefaultValue(true)
                .setTooltip(Text.translatable("moreculling.config.option.customItemFrameRenderer.tooltip"))
                .setSaveConsumer(newValue -> MoreCulling.CONFIG.useCustomItemFrameRenderer = newValue)
                .setChangeConsumer((instance, value) -> {
                    itemFrameLOD.setEnabledState(value);
                    itemFrame3FaceCulling.setEnabledState(value);
                })
                .build());
        generalCategory.addEntry(itemFrameLOD);
        generalCategory.addEntry(itemFrameLODRange);
        generalCategory.addEntry(itemFrame3FaceCulling);
        generalCategory.addEntry(itemFrame3FaceCullingRange);

        generalCategory.addEntry(leavesCullingMode);
        generalCategory.addEntry(leavesCullingDepth);
        generalCategory.addEntry(includeMangroveRoots);

        generalCategory.addEntry(powderSnowCulling);
        leavesCullingDepth.setEnabledState(leavesCullingMode.isEnabled() && MoreCulling.CONFIG.leavesCullingMode == LeavesCullingMode.DEPTH);
        return builder.build();
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuConfig::createConfigScreen;
    }
}
