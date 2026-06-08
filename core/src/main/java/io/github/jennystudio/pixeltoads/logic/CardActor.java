package io.github.jennystudio.pixeltoads.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CardActor extends Button {
    public String terrainType = "GROVE"; // Идентификатор местности

    public CardActor(TextureRegion texture) {
        super(new ButtonStyle());
        getStyle().up = new TextureRegionDrawable(texture);
        setSize(40, 61);
    }
}
