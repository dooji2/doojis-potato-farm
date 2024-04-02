package com.dooji.dpf;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.SubtitleS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PotatoActions {

    private final ServerPlayerEntity player;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private final PotatoSpawner potatoSpawner = new PotatoSpawner(client);
    private final Identifier potatoTexture = new Identifier("dpf", "icon.png");
    private final World world;
    private final Random random = new Random();
    private final Timer timer = new Timer();
    private Boolean timerStarted = false;
    private Boolean didIt = false;
    private int countdownSeconds = 10;
    private int delay1;
    private int delay2;

    private static final Item[] potatoItems = new Item[] {
            Blocks.BAKED_POTATO_BRICKS.asItem(),
            Blocks.EXPIRED_BAKED_POTATO_BRICKS.asItem(),
            Blocks.CHARRED_BAKED_POTATO_BRICKS.asItem(),
            Blocks.TATERSTONE_SLAB.asItem(),
            Blocks.TATERSTONE_STAIRS.asItem(),
            Blocks.TATERSTONE_WALL.asItem(),
            Blocks.POTONE_SLAB.asItem(),
            Blocks.POTONE_STAIRS.asItem(),
            Blocks.POTONE_WALL.asItem(),
            Blocks.BAKED_POTATO_BRICK_SLAB.asItem(),
            Blocks.BAKED_POTATO_BRICK_STAIRS.asItem(),
            Blocks.BAKED_POTATO_BRICK_WALL.asItem(),
            Blocks.EXPIRED_BAKED_POTATO_BRICK_SLAB.asItem(),
            Blocks.EXPIRED_BAKED_POTATO_BRICK_STAIRS.asItem(),
            Blocks.EXPIRED_BAKED_POTATO_BRICK_WALL.asItem(),
            Blocks.CHARRED_BAKED_POTATO_BRICK_SLAB.asItem(),
            Blocks.CHARRED_BAKED_POTATO_BRICK_STAIRS.asItem(),
            Blocks.CHARRED_BAKED_POTATO_BRICK_WALL.asItem(),
            Blocks.POISONOUS_MASHED_POTATO.asItem(),
            Blocks.POISONOUS_POTATO_BLOCK.asItem(),
            Blocks.COMPRESSED_POISONOUS_POTATO_BLOCK.asItem(),
            Blocks.DOUBLE_COMPRESSED_POISONOUS_POTATO_BLOCK.asItem(),
            Blocks.TRIPLE_COMPRESSED_POISONOUS_POTATO_BLOCK.asItem(),
            Blocks.QUADRUPLE_COMPRESSED_POISONOUS_POTATO_BLOCK.asItem(),
            Items.POISONOUS_POTATO_SLICES,
            Items.POISONOUS_POTATO_FRIES,
            Items.POISONOUS_POTATO_STICKS,
            Items.POISONOUS_POTATO_CHIPS,
            Items.HOT_POTATO
    };

    private static final Block[] potatoBlocks = new Block[] {
            Blocks.BAKED_POTATO_BRICKS,
            Blocks.EXPIRED_BAKED_POTATO_BRICKS,
            Blocks.CHARRED_BAKED_POTATO_BRICKS,
            Blocks.TATERSTONE_SLAB,
            Blocks.TATERSTONE_STAIRS,
            Blocks.TATERSTONE_WALL,
            Blocks.POTONE_SLAB,
            Blocks.POTONE_STAIRS,
            Blocks.POTONE_WALL,
            Blocks.BAKED_POTATO_BRICK_SLAB,
            Blocks.BAKED_POTATO_BRICK_STAIRS,
            Blocks.BAKED_POTATO_BRICK_WALL,
            Blocks.EXPIRED_BAKED_POTATO_BRICK_SLAB,
            Blocks.EXPIRED_BAKED_POTATO_BRICK_STAIRS,
            Blocks.EXPIRED_BAKED_POTATO_BRICK_WALL,
            Blocks.CHARRED_BAKED_POTATO_BRICK_SLAB,
            Blocks.CHARRED_BAKED_POTATO_BRICK_STAIRS,
            Blocks.CHARRED_BAKED_POTATO_BRICK_WALL,
            Blocks.POISONOUS_MASHED_POTATO,
            Blocks.POISONOUS_POTATO_BLOCK,
            Blocks.COMPRESSED_POISONOUS_POTATO_BLOCK,
            Blocks.DOUBLE_COMPRESSED_POISONOUS_POTATO_BLOCK,
            Blocks.TRIPLE_COMPRESSED_POISONOUS_POTATO_BLOCK,
            Blocks.QUADRUPLE_COMPRESSED_POISONOUS_POTATO_BLOCK
    };

    public PotatoActions(ServerPlayerEntity player) {
        this.player = player;
        this.world = player.getServerWorld();
        registerHudRenderCallback();
        registerChatListener();
    }

    public void startActions() {
        if (!timerStarted) {
            potatoSpawner.startSpawning();
            timerStarted = true;
        }

        updateDelays();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updateDelays();
            }
        }, 20000, 20000);

        setupTimerTask(delay1 * 1000);
        setupTimerTask2(delay2 * 1000);

        monitorWater();
    }

    private void registerHudRenderCallback() {
        HudRenderCallback.EVENT.register(this::onHudRender);
    }

    private void onHudRender(DrawContext context, float tickDelta) {
        if (client.player != null && client.world != null) {
            renderPotatoes(context);
        }
    }

    private void renderPotatoes(DrawContext context) {
        for (Potato potato : potatoSpawner.getPotatoes()) {
            context.drawTexture(potatoTexture, potato.getX(), potato.getY(), 0, 0, potato.getSize(), potato.getSize(),
                    potato.getSize(), potato.getSize());
        }
    }

    private void registerChatListener() {
        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            Text chatMessage = message.getContent();
            return processChatResponse(sender, chatMessage);
        });
    }

    private boolean processChatResponse(ServerPlayerEntity player, Text message) {
        String messageText = message.getString();
        if (messageText.equalsIgnoreCase("Yes")) {
            handleYesResponse();
        } else if (messageText.equalsIgnoreCase("No")) {
            handleNoResponse();
        }
        return true;
    }

    private void handleYesResponse() {
        player.removeStatusEffect(StatusEffects.BLINDNESS);
        player.networkHandler.sendPacket(
                new TitleS2CPacket(Text.literal("Enjoy your potatoes!").formatted(Formatting.GREEN)));
        player.networkHandler.sendPacket(
                new SubtitleS2CPacket(Text.literal(":D").formatted(Formatting.YELLOW)));
        ItemStack potatoesStack = new ItemStack(Items.POISONOUS_POTATO, 64);
        player.giveItemStack(potatoesStack);

        didIt = true;
    }

    private void handleNoResponse() {
        player.networkHandler.disconnect(Text.literal("You don't like potatoes!"));
    }

    private void setupTimerTask(int delay1) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (world != null) {
                    teleportPlayerRandomly();
                    givePlayerPotatoes();
                    sendChatMessage("Potatoes are coming for you");
                }
            }
        }, 0, delay1);
    }

    private void teleportPlayerRandomly() {
        BlockPos pos = getRandomSafeLocation();
        if (pos != null) {
            player.teleport(pos.getX(), pos.getY(), pos.getZ());
        }
    }

    private BlockPos getRandomSafeLocation() {
        int maxTeleportDistance = 100;
        int x = player.getBlockX() + random.nextInt(maxTeleportDistance * 2) - maxTeleportDistance;
        int z = player.getBlockZ() + random.nextInt(maxTeleportDistance * 2) - maxTeleportDistance;
        int y = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);

        y = Math.max(y, 0);

        return new BlockPos(x, y, z);
    }

    private void givePlayerPotatoes() {
        Item randomPotatoItem = potatoItems[random.nextInt(potatoItems.length)];

        ItemStack potatoesStack = new ItemStack(randomPotatoItem, 1);
        player.giveItemStack(potatoesStack);

        player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, random.nextInt(40) + 20, 255));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, random.nextInt(40) + 20, 255));

        player.playSoundToPlayer(SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private void sendChatMessage(String message) {
        Text text = Text.of(message);
        player.sendMessage(text);
    }

    private void monitorWater() {
        Timer waterTimer = new Timer();
        waterTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                BlockPos playerPos = player.getBlockPos();
                int searchDistance = 10;

                for (int dx = -searchDistance; dx <= searchDistance; dx++) {
                    for (int dy = -searchDistance; dy <= searchDistance; dy++) {
                        for (int dz = -searchDistance; dz <= searchDistance; dz++) {
                            BlockPos blockPos = playerPos.add(dx, dy, dz);
                            if (world.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                                Block block = getRandomPotatoBlock();
                                world.setBlockState(blockPos, block.getDefaultState());
                                triggerRainAndThunder();
                            }
                        }
                    }
                }
            }
        }, 0, 1000);
    }

    private Block getRandomPotatoBlock() {
        return potatoBlocks[random.nextInt(potatoBlocks.length)];
    }

    private void triggerRainAndThunder() {
        world.setRainGradient(1.0f);
        world.setThunderGradient(1.0f);
    }

    private void setupTimerTask2(int delay2) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (world != null) {
                    ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
                        if (countdownSeconds > 0 && countdownSeconds <= 10) {
                            Text chatMessage = message.getContent();
                            processChatResponse(sender, chatMessage);
                        }
                        return true;
                    });
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, Integer.MAX_VALUE, 0));

                    Text title = Text.literal("Do you like potatoes?");
                    Text subtitle = Text.literal("You have " + countdownSeconds + " seconds to say Yes or No in chat");
                    player.networkHandler.sendPacket(
                            new TitleS2CPacket(((MutableText) Text.of(title)).formatted(Formatting.YELLOW)));
                    player.networkHandler.sendPacket(
                            new SubtitleS2CPacket(((MutableText) Text.of(subtitle)).formatted(Formatting.RED)));

                    startCountdown();
                }
            }
        }, 0, delay2);
    }

    private void startCountdown() {
        Timer countdownTimer = new Timer();
        countdownTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                countdownSeconds--;
                if (countdownSeconds == 0 && !didIt) {
                    handleNoResponse();
                } else if (countdownSeconds == 0 && didIt) {
                    countdownTimer.cancel();
                    countdownSeconds = 10;
                    didIt = false;
                }
            }
        }, 1000, 1000);
    }

    private void updateDelays() {
        delay1 = random.nextInt(41) + 10;
        delay2 = random.nextInt(51) + 40;
    }
}
