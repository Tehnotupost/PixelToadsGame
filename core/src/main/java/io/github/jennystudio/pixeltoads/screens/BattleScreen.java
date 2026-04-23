package io.github.jennystudio.pixeltoads.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import io.github.jennystudio.pixeltoads.PixelToadsGame;
import io.github.jennystudio.pixeltoads.entities.Fly;
import io.github.jennystudio.pixeltoads.entities.Frog;
import io.github.jennystudio.pixeltoads.ui.UIStyles;
import io.github.jennystudio.pixeltoads.utils.Assets;

public class BattleScreen implements Screen {
    final PixelToadsGame game;
    private Stage stage;
    private Skin skin;
    private Texture bgTexture, lilyTexture, frogTexture, flyTexture;
    private boolean isLilyPadBuilt = false; //сохранения
    private boolean isFirstRunDone = false;
    private TextButton runBtn, buildBtn;
    private Image lilyImage;
    private ShapeRenderer shapeRenderer; // Инструмент для рисования линий
    private Frog frog;
    private Fly fly;

    public BattleScreen(final PixelToadsGame game, Skin skin) {
        this.game = game;
        this.skin = skin;
        this.stage = new Stage(game.viewport);
        // Загрузка текстур
        bgTexture = game.assets.getTexture(Assets.LAKE_BG);
        lilyTexture = game.assets.getTexture(Assets.LILY_PAD);
        frogTexture = game.assets.getTexture(Assets.FROG);
        flyTexture = game.assets.getTexture(Assets.FLY);
        initEnvironment();
        initUI();
//        // --- ИНИЦИАЛИЗАЦИЯ ЯЗЫКА ---
        shapeRenderer = new ShapeRenderer();
    }

    private void initUI() {
        Table table = new Table();
        table.bottom().padBottom(10);
        table.setFillParent(true);
        stage.addActor(table);
        buildBtn = new TextButton("BUILD", game.skin);
        runBtn = new TextButton("RUN", game.skin);
        runBtn.setVisible(false); // Скрыта до постройки кувшинки
        //тут поведение кнопок
        UIStyles.addBackButton(stage, game.skin, () -> game.setScreen(new MainMenuScreen(game)));
        buildBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new BuildMenuScreen(game, BattleScreen.this, skin));
            }
        });
        runBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                isFirstRunDone = true;
                checkFrogAppearance();
            }
        });
        table.add(buildBtn).pad(5);
        table.add(runBtn).pad(5);
    }

    private void initEnvironment() {
        // Кувшинка
        lilyImage = new Image(lilyTexture);
        lilyImage.setSize(128, 96);
        lilyImage.setPosition(160, 40);
        lilyImage.getColor().a = 0; // Прозрачная
        stage.addActor(lilyImage);
        // Лягушка
        frog = new Frog(game);
        frog.setPosition(160, 75);
        frog.setVisible(isFirstRunDone);
        stage.addActor(frog);
        // Муха для кормления
        fly = new Fly(game);
        fly.setVisible(isFirstRunDone);
        fly.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                eatFlyLogic();
            }
        });
        stage.addActor(fly);
    }
    private void eatFlyLogic() {
        frog.shootTongue(fly.getX() + 8, fly.getY() + 8);
        fly.onEaten(() -> fly.setPosition(MathUtils.random(40, 440), MathUtils.random(100, 230)));

        Timer.schedule(new Timer.Task() {
            @Override
            public void run() { frog.retractTongue(); }
        }, 0.3f);
    }
    private void checkFrogAppearance() {
        if (isFirstRunDone && isLilyPadBuilt) {
            frog.setVisible(true);
            fly.setVisible(true);
        }
    }

    public void setLilyPadBuilt(boolean built) {
        this.isLilyPadBuilt = built;
        if (built) {
            lilyImage.getColor().a = 1; // Делаем кувшинку видимой
            runBtn.setVisible(true);    // Показываем кнопку RUN
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, 480, 270);
        game.batch.end();
        stage.act(delta);
        stage.draw();
        // Рисуем язык лягушки простыми линиями, если она ест
        shapeRenderer.setProjectionMatrix(game.viewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (frog != null) {
            frog.drawTongue(shapeRenderer);
        }
        shapeRenderer.end();
    }
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        bgTexture.dispose();
        lilyTexture.dispose();
        frogTexture.dispose();
        flyTexture.dispose();
        shapeRenderer.dispose();
    }
}
