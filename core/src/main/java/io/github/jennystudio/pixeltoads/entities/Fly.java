package io.github.jennystudio.pixeltoads.entities;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import io.github.jennystudio.pixeltoads.PixelToadsGame;
import io.github.jennystudio.pixeltoads.utils.Assets;

public class Fly extends Image {
    public Fly(PixelToadsGame game) {
        super(game.assets.getTexture(Assets.FLY));
        setSize(16, 16);
        setOrigin(8, 8);
        startBuzzing();
    }

    public void startBuzzing() {
        clearActions();
        addAction(Actions.forever(Actions.sequence(
            Actions.run(() -> {
                float newX = (float) Math.random() * 400 + 40;
                float newY = (float) Math.random() * 150 + 80;
                addAction(Actions.moveTo(newX, newY, 2.0f + (float)Math.random()));
            }), Actions.delay(1.0f))));
    }

    public void onEaten(Runnable onRespawn) {
        clearActions();
        addAction(Actions.sequence(
            Actions.fadeOut(0.1f),
            Actions.run(onRespawn), // Телепортация
            Actions.fadeIn(0.2f),
            Actions.run(this::startBuzzing) // Снова лететь
        ));
    }
}
