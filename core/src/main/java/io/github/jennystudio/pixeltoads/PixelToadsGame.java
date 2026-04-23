package io.github.jennystudio.pixeltoads;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import io.github.jennystudio.pixeltoads.screens.SplashScreen;
import io.github.jennystudio.pixeltoads.ui.UIStyles;
import io.github.jennystudio.pixeltoads.utils.Assets;

public class PixelToadsGame extends Game {
    public SpriteBatch batch;
    public Texture image;
    public FitViewport viewport;
    public Assets assets;
    public Skin skin;

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(480, 270);
        assets = new Assets();
        assets.loadAll();
        this.skin = UIStyles.createDefaultSkin();
//        createBasicSkin();
        setScreen(new SplashScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
//        if (skin != null) skin.dispose();
        assets.dispose();
        super.dispose();
    }
}
