package io.github.jennystudio.pixeltoads.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jennystudio.pixeltoads.PixelToadsGame;

public class MainMenuScreen implements Screen {
    final PixelToadsGame game;
    Texture mainMenueTexture;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(final PixelToadsGame game) {
        this.game = game;
        mainMenueTexture = new Texture("sprites/forest.png");
        stage = new Stage(game.viewport);
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        TextButton startBtn = new TextButton("Start", game.skin);
        TextButton savesBtn = new TextButton("Saves", game.skin);
        TextButton settingsBtn = new TextButton("Settings", game.skin);
        TextButton trophiesBtn = new TextButton("Trophies", game.skin);
        TextButton exitBtn = new TextButton("Exit", game.skin);

        startBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                 game.setScreen(new BattleScreen(game, skin));
            }
        });
        savesBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SavesScreen(game));
            }
        });
        settingsBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new SettingsScreen(game));
            }
        });
        trophiesBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new TrophiesScreen(game));
            }
        });
        exitBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(startBtn).fillX().uniformX().pad(5);
        table.row();
        table.add(savesBtn).fillX().uniformX().pad(5);
        table.row();
        table.add(settingsBtn).fillX().uniformX().pad(5);
        table.row();
        table.add(trophiesBtn).fillX().uniformX().pad(5);
        table.row();
        table.add(exitBtn).fillX().uniformX().pad(5);
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
        skin.dispose();
    }
}
