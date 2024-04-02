package com.dooji.dpf;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class PotatoSpawner {
    private final MinecraftClient client;
    private final Random random = new Random();
    private final List<Potato> potatoes = new ArrayList<>();
    private Timer spawnTimer;
    private int spawnInterval = 10000;

    public PotatoSpawner(MinecraftClient client) {
        this.client = client;
    }

    public void startSpawning() {
        spawnTimer = new Timer();
        spawnTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                spawnPotato();
                spawnInterval = (int) (spawnInterval * 0.8);
                spawnInterval = Math.max(spawnInterval, 20000);
            }
        }, spawnInterval, spawnInterval);
    }

    private void spawnPotato() {
        int windowWidth = client.getWindow().getScaledWidth();
        int windowHeight = client.getWindow().getScaledHeight();

        int x = random.nextInt(windowWidth);
        int y = random.nextInt(windowHeight);

        x = Math.max(0, Math.min(windowWidth - 1, x));
        y = Math.max(0, Math.min(windowHeight - 1, y));

        int size = random.nextInt(300 - 50 + 1) + 50;

        potatoes.add(new Potato(x, y, size));

        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.ENTITY_VILLAGER_AMBIENT, 1.0F));
    }

    public void stopSpawning() {
        if (spawnTimer != null) {
            spawnTimer.cancel();
            spawnTimer.purge();
        }
    }

    public List<Potato> getPotatoes() {
        return potatoes;
    }
}