package io.github.jennystudio.pixeltoads.logic;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class CardActor extends Button {
    private final String terrainType; // Идентификатор местности

    public CardActor(TextureRegion texture, String terrainType) {
        super(new ButtonStyle());
        this.terrainType = terrainType;
        getStyle().up = new TextureRegionDrawable(texture);
        setSize(40, 61);
    }

    public String getTerrainType() {
        return terrainType;
    }
}
