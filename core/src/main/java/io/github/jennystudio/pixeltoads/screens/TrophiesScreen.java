package io.github.jennystudio.pixeltoads.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jennystudio.pixeltoads.PixelToadsGame;
import io.github.jennystudio.pixeltoads.ui.UIStyles;

public class TrophiesScreen implements Screen {
    final PixelToadsGame game;
    Texture mainMenueTexture;
    private Stage stage;
    private TextButton oops;

    public TrophiesScreen(final PixelToadsGame game) {
        this.game = game;
        mainMenueTexture = new Texture("sprites/toad_warrior.png");
        stage = new Stage(game.viewport);
        initUI();
    }

    private void initUI() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        oops = new TextButton("Oops, there is no trophies!", game.skin);
        //тут поведение кнопок
        UIStyles.addBackButton(stage, game.skin, () -> game.setScreen(new MainMenuScreen(game)));
        table.add(oops).fillX().uniformX().pad(5);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1f);
        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(mainMenueTexture, 0, 0, 480, 270);
        game.batch.end();
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) {
        game.viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        mainMenueTexture.dispose();
        stage.dispose();
    }
}
