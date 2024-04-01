package com.dooji.dpf.mixin;

import net.minecraft.GameVersion;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.dooji.dpf.FakeGameVersion;

import java.lang.reflect.Field;

import java.util.Random;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

    private Random random = new Random();

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
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void renderMixin(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo info) {
        String fakeVersionId = getRandomFakeVersion();
        String fakeVersionName = fakeVersionId;
        int fakeProtocolVersion = 757;

        GameVersion fakeVersion = new FakeGameVersion(fakeVersionId, fakeVersionName, fakeProtocolVersion);

        try {
            Field field = SharedConstants.class.getDeclaredField("gameVersion");
            field.setAccessible(true);
            field.set(null, fakeVersion);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            e.printStackTrace();
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

    private String getRandomFakeVersion() {
        String[] versions = {
                "Potato 1.90.4",
                "Potato Alpha 912.213.1",
                "Potato Beta 2.0",
                "Potato Gamma 3.14",
        };

        return versions[random.nextInt(versions.length)];
    }
}
