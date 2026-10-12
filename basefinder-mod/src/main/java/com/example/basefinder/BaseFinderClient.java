package com.example.basefinder;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.minecraft.util.InputUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;

/**
 * Client-only, heuristic base alert. It does not read server files or unloaded chunks.
 * Use only in single-player or on servers where this kind of scan is permitted.
 */
public class BaseFinderClient implements ClientModInitializer {
    public static boolean enabled = true;
    public static int radius = 16;
    public static int threshold = 7;

    private static final KeyBinding OPEN_MENU = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "key.basefinder.open",
                    InputUtil.Type.KEYSYM,
                    GLFW.GLFW_KEY_N,
                    "category.basefinder"
            )
    );

    private long ticks = 0;
    private long lastAlertTick = -1200;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MENU.wasPressed()) {
                if (client.player != null) {
                    client.setScreen(new BaseFinderScreen());
                }
            }

            if (client.player == null || client.world == null) {
                return;
            }

            ticks++;
            // Scan twice every four seconds to keep the per-tick overhead low.
            if (enabled && ticks % 40 == 0) {
                scanNearby(client);
            }
        });
    }

    private void scanNearby(MinecraftClient client) {
        World world = client.world;
        BlockPos origin = client.player.getBlockPos();
        int r = Math.max(8, Math.min(32, radius));
        int minY = Math.max(world.getBottomY(), origin.getY() - 10);
        int maxY = Math.min(world.getTopY() - 1, origin.getY() + 10);
        int found = 0;
        int bestDistanceSq = Integer.MAX_VALUE;
        BlockPos nearestIndicator = null;
        Set<Block> kinds = new HashSet<>();

        for (int x = origin.getX() - r; x <= origin.getX() + r; x++) {
            for (int z = origin.getZ() - r; z <= origin.getZ() + r; z++) {
                int dx = x - origin.getX();
                int dz = z - origin.getZ();
                int horizontalDistanceSq = dx * dx + dz * dz;
                if (horizontalDistanceSq > r * r || !world.isChunkLoaded(x >> 4, z >> 4)) {
                    continue;
                }

                for (int y = minY; y <= maxY; y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (!isBaseIndicator(block)) {
                        continue;
                    }

                    found++;
                    kinds.add(block);
                    int dy = y - origin.getY();
                    int distanceSq = horizontalDistanceSq + dy * dy;
                    if (distanceSq < bestDistanceSq) {
                        bestDistanceSq = distanceSq;
                        nearestIndicator = pos.toImmutable();
                    }
                }
            }
        }

        if (found >= Math.max(3, Math.min(20, threshold))
                && nearestIndicator != null
                && ticks - lastAlertTick >= 600) {
            lastAlertTick = ticks;
            client.player.sendMessage(Text.literal(
                    "[BaseFinder] Possible base nearby: " + found + " base-like blocks (" + kinds.size()
                            + " types). Nearest indicator: " + nearestIndicator.getX() + ", "
                            + nearestIndicator.getY() + ", " + nearestIndicator.getZ()
                            + ". False positives are possible."), false);
        }
    }

    private static boolean isBaseIndicator(Block block) {
        return block instanceof ChestBlock
                || block instanceof BarrelBlock
                || block instanceof BedBlock
                || block instanceof DoorBlock
                || block instanceof AbstractFurnaceBlock
                || block instanceof ShulkerBoxBlock
                || block == net.minecraft.block.Blocks.CRAFTING_TABLE
                || block == net.minecraft.block.Blocks.ANVIL
                || block == net.minecraft.block.Blocks.CHIPPED_ANVIL
                || block == net.minecraft.block.Blocks.DAMAGED_ANVIL
                || block == net.minecraft.block.Blocks.ENCHANTING_TABLE
                || block == net.minecraft.block.Blocks.BREWING_STAND
                || block == net.minecraft.block.Blocks.LOOM
                || block == net.minecraft.block.Blocks.CARTOGRAPHY_TABLE
                || block == net.minecraft.block.Blocks.SMITHING_TABLE
                || block == net.minecraft.block.Blocks.STONECUTTER
                || block == net.minecraft.block.Blocks.HOPPER
                || block == net.minecraft.block.Blocks.DISPENSER
                || block == net.minecraft.block.Blocks.DROPPER
                || block == net.minecraft.block.Blocks.LECTERN
                || block == net.minecraft.block.Blocks.BEACON
                || block == net.minecraft.block.Blocks.RESPAWN_ANCHOR
                || block == net.minecraft.block.Blocks.JUKEBOX
                || block == net.minecraft.block.Blocks.NOTE_BLOCK
                || block == net.minecraft.block.Blocks.COMPOSTER;
    }
}
