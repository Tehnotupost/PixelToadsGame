package io.github.jennystudio.pixeltoads.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.math.Vector2;
import io.github.jennystudio.pixeltoads.PixelToadsGame;
import io.github.jennystudio.pixeltoads.utils.Assets;

public class Frog extends Image {
    private boolean isTongueOut = false;
    private Vector2 tongueTarget = new Vector2();
    private Vector2 mouthPos = new Vector2();

    public Frog(PixelToadsGame game) {
        super(game.assets.getTexture(Assets.FROG));
        setSize(128, 128);
    }

    public void shootTongue(float targetX, float targetY) {
        isTongueOut = true;
        tongueTarget.set(targetX, targetY);
        // Рот лягушки относительно её позиции
        mouthPos.set(getX() + getWidth() * 0.5f, getY() + getHeight() * 0.7f);
    }

    public void retractTongue() {
        isTongueOut = false;
    }

    public void drawTongue(ShapeRenderer sr) {
        if (isTongueOut) {
            sr.setColor(Color.PINK);
            sr.rectLine(mouthPos, tongueTarget, 8f);
            sr.circle(tongueTarget.x, tongueTarget.y, 5f);
            sr.circle(mouthPos.x, mouthPos.y, 5f);
        }
    }
    public boolean isTongueOut() { return isTongueOut; }
}
