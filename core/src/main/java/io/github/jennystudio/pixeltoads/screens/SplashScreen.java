package io.github.jennystudio.pixeltoads.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jennystudio.pixeltoads.PixelToadsGame;
import io.github.jennystudio.pixeltoads.utils.Assets;

public class SplashScreen implements Screen {
    private final PixelToadsGame game;
    private Texture splashTexture;
    private float displayTimer = 0;
    private final float DISPLAY_TIME = 3.0f; // Сколько секунд висит заставка

    public SplashScreen(PixelToadsGame game) {
        this.game = game;
        splashTexture = game.assets.getTexture(Assets.SPLASH);
        splashTexture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);
        displayTimer += delta;
        if (displayTimer > DISPLAY_TIME || Gdx.input.justTouched()) {
            game.setScreen(new MainMenuScreen(game));
        }
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(splashTexture, 0, 0, 480, 270);
        game.batch.end();
    }
    @Override public void show() {}
    @Override public void resize(int width, int height) { game.viewport.update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() { splashTexture.dispose(); }
}
