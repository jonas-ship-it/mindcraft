package com.mindcraft.pathhelper;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(MindcraftPathHelper.MOD_ID)
public class MindcraftPathHelper {
    public static final String MOD_ID = "mindcraft_pathhelper";

    public MindcraftPathHelper() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("mindcraft_surface")
            .executes(context -> {
                ServerPlayer player = context.getSource().getPlayerOrException();
                ServerLevel level = context.getSource().getLevel();
                BlockPos pos = player.blockPosition();
                int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
                context.getSource().sendSuccess(
                    () -> Component.literal("SurfaceY: " + surfaceY),
                    false
                );
                return surfaceY;
            })
        );

        event.getDispatcher().register(Commands.literal("mindcraft_hazards")
            .then(Commands.argument("radius", IntegerArgumentType.integer(1, 24))
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    ServerLevel level = context.getSource().getLevel();
                    int radius = IntegerArgumentType.getInteger(context, "radius");
                    BlockPos center = player.blockPosition();
                    int waterCount = 0;
                    int lavaCount = 0;

                    for (int dx = -radius; dx <= radius; dx++) {
                        for (int dy = -radius; dy <= radius; dy++) {
                            for (int dz = -radius; dz <= radius; dz++) {
                                BlockPos checkPos = center.offset(dx, dy, dz);
                                var block = level.getBlockState(checkPos).getBlock();
                                if (block == Blocks.WATER) {
                                    waterCount++;
                                } else if (block == Blocks.LAVA) {
                                    lavaCount++;
                                }
                            }
                        }
                    }

                    final String message = "Hazards (r=" + radius + "): water=" + waterCount + ", lava=" + lavaCount;
                    context.getSource().sendSuccess(
                        () -> Component.literal(message),
                        false
                    );
                    return 1;
                })
            )
        );
    }
}
