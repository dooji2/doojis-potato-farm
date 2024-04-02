package com.dooji.dpf.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.dooji.dpf.Potato;
import com.dooji.dpf.PotatoSpawner;

import java.util.Random;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    private Random random = new Random();
    private MinecraftClient client = MinecraftClient.getInstance();
    private final PotatoSpawner potatoSpawner = new PotatoSpawner(client);
    private final Identifier potatoTexture = new Identifier("dpf", "icon.png");

    private Boolean timerStarted = false;

    @Inject(method = "init", at = @At("TAIL"))
    private void initMixin(CallbackInfo info) {
        int minX = 50;
        int maxX = 500;
        int minY = 50;
        int maxY = 300;

        for (Element element : ((TitleScreen) (Object) this).children()) {
            if (element instanceof ButtonWidget) {
                ButtonWidget button = (ButtonWidget) element;
                int x = this.random.nextInt(maxX - minX) + minX;
                int y = this.random.nextInt(maxY - minY) + minY;
                button.setX(x);
                button.setY(y);

                button.setMessage(getRandomPotatoText());
            }
        }

        if (!timerStarted) {
            potatoSpawner.startSpawning();
            timerStarted = true;
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void renderMixin(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        for (Potato potato : potatoSpawner.getPotatoes()) {
            context.drawTexture(potatoTexture, potato.getX(), potato.getY(), 0, 0, potato.getSize(), potato.getSize(),
                    potato.getSize(), potato.getSize());
        }
    }

    private Text getRandomPotatoText() {
        String[] potatoPhrases = {
                "McDonald's",
                "Potato",
                "KFC",
                "Fries",
                "Poisonous Potato",
                "Poisonous Fries"
        };

        return Text.of(potatoPhrases[random.nextInt(potatoPhrases.length)]);
    }
}
