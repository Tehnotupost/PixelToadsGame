package io.github.jennystudio.pixeltoads.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    public final AssetManager manager = new AssetManager();
    public static final String LAKE_BG = "sprites/lake_bg.png";
    public static final String LILY_PAD = "sprites/lily_pad.png";
    public static final String FROG = "sprites/frog.png";
    public static final String FROG_L = "sprites/froag.png";
    public static final String FLY = "sprites/fly.png";
    public static final String SPLASH = "sprites/toad_warrior.png";
    public static final String RUN_MAP = "sprites/runmap.png";
    public static final String ROAD_H = "sprites/roadH.png";
    public static final String ROAD_V = "sprites/roadV.png";
    public static final String ROAD_UR = "sprites/roadUR.png";
    public static final String ROAD_DR = "sprites/roadDR.png";
    public static final String ROAD_UL = "sprites/roadUL.png";
    public static final String ROAD_DL = "sprites/roadDL.png";

    public void loadAll() {
        manager.load(LAKE_BG, Texture.class);
        manager.load(LILY_PAD, Texture.class);
        manager.load(FROG, Texture.class);
        manager.load(FROG_L, Texture.class);
        manager.load(FLY, Texture.class);
        manager.load(SPLASH, Texture.class);
        manager.load(RUN_MAP, Texture.class);
        manager.load(ROAD_H, Texture.class);
        manager.load(ROAD_V, Texture.class);
        manager.load(ROAD_UR, Texture.class);
        manager.load(ROAD_DR, Texture.class);
        manager.load(ROAD_UL, Texture.class);
        manager.load(ROAD_DL, Texture.class);
        manager.finishLoading();
        for (Texture tex : manager.getAll(Texture.class, new com.badlogic.gdx.utils.Array<Texture>())) {
            tex.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        }
    }
    public Texture getTexture(String path) {
        return manager.get(path, Texture.class);
    }
    public void dispose() {
        manager.dispose();
    }
}
