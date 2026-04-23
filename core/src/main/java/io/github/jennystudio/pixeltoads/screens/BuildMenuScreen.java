package io.github.jennystudio.pixeltoads.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jennystudio.pixeltoads.PixelToadsGame;
import io.github.jennystudio.pixeltoads.ui.UIStyles;

public class BuildMenuScreen implements Screen {
    private final PixelToadsGame game;
    private final BattleScreen hubScreen; // Ссылка на экран озера
    private Stage stage;

    public BuildMenuScreen(final PixelToadsGame game, final BattleScreen hubScreen, Skin skin) {
        this.game = game;
        this.hubScreen = hubScreen;
        this.stage = new Stage(game.viewport);
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);
        Label title = new Label("BUILDING TREE", game.skin);
        table.add(title).padBottom(0).colspan(2);
        table.row();
        UIStyles.addBackButton(stage, game.skin, () -> game.setScreen(hubScreen));
        // 1. Кувшинка (Доступна)
        TextButton lilyBtn = new TextButton("LILY PAD", game.skin);
        lilyBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hubScreen.setLilyPadBuilt(true); // Сообщаем озеру, что построили
                game.setScreen(hubScreen);       // Возвращаемся
            }
        });
        // 2. Другие постройки (Заблокированы)
        TextButton lockedBtn1 = new TextButton("??? LOCKED ???", game.skin);
        lockedBtn1.setDisabled(true); // Кнопка не нажимается
        TextButton lockedBtn2 = new TextButton("??? LOCKED ???", game.skin);
        lockedBtn2.setDisabled(true);
        table.add(lilyBtn).width(150).height(50).pad(10);
        table.row();
        table.add(lockedBtn1).width(150).height(50).pad(10);
        table.row();
        table.add(lockedBtn2).width(150).height(50).pad(10);
        table.row();
    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0.05f, 0.1f, 1f); // Темный фон меню
        stage.act(delta);
        stage.draw();
    }
    @Override public void show() { Gdx.input.setInputProcessor(stage); }
    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void dispose() { stage.dispose(); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
