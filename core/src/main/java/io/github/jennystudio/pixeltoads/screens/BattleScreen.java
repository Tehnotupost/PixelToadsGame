package io.github.jennystudio.pixeltoads.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jennystudio.pixeltoads.PixelToadsGame;
import io.github.jennystudio.pixeltoads.logic.CardActor;
import io.github.jennystudio.pixeltoads.logic.CardArea;
import io.github.jennystudio.pixeltoads.logic.PathGenerator;
import io.github.jennystudio.pixeltoads.logic.PlacementManager;
import io.github.jennystudio.pixeltoads.model.terra.TerraRegistry;
import io.github.jennystudio.pixeltoads.ui.UIStyles;
import io.github.jennystudio.pixeltoads.utils.Assets;

public class BattleScreen implements Screen {
    final PixelToadsGame game;
    private Stage stage;
    private Skin skin;
    private final HubScreen hubScreen;
    private Texture bgTexture, groveCardTexture, swampCardTexture;
    private TextButton runBtn;
    private Array<Vector2> path;
    private final int TILE_SIZE = 20;
    private Vector2 frogScreenPos = new Vector2(); // Позиция лягушки на экране в пикселях
    private int currentPathIndex = 0;             // Текущий индекс в массиве path
    private boolean isMoving = false;
    private float movementSpeed = 80f;
    private Texture frogTexture;
    float offsetX = 0;
    float offsetY = 0;
    private boolean isPlacingTile = false;
    private CardActor selectedCard = null;
    private com.badlogic.gdx.utils.ObjectMap<Vector2, String> roadModifiers = new com.badlogic.gdx.utils.ObjectMap<>();
    CardActor groveCard, swampCard;
    private Vector2 hoverGridPos = new Vector2(-1, -1);
    private PlacementManager placementManager;
    private TerraRegistry terraRegistry;
    private CardArea cardHand;

    public BattleScreen(final PixelToadsGame game, Skin skin, HubScreen hubScreen) {
        this.game = game;
        this.skin = skin;
        this.stage = new Stage(game.viewport);
        this.hubScreen = hubScreen;
        loadTexture();
        PathGenerator pathGenerator = new PathGenerator(15, 12);
        this.path = pathGenerator.generateLoopPath();
        this.placementManager = new PlacementManager(path, roadModifiers);
        this.terraRegistry = new TerraRegistry(game);
        this.placementManager = new PlacementManager(path, roadModifiers);
        initUI();
    }

    private void loadTexture(){
        bgTexture = game.assets.getTexture(Assets.RUN_MAP);
        frogTexture = game.assets.getTexture(Assets.FROG_L);
        groveCardTexture = game.assets.getTexture(Assets.CARD_GROVE);
        swampCardTexture = game.assets.getTexture(Assets.CARD_SWAMP);
    }

    private void drawRoad() {
        if (path == null || path.size == 0) return;

        for (int i = 0; i < path.size; i++) {
            Vector2 current = path.get(i);
            Vector2 prev = path.get(i == 0 ? path.size - 1 : i - 1);
            Vector2 next = path.get((i + 1) % path.size);

            String modifier = roadModifiers.get(current, "NORMAL");
            boolean isHovered = isPlacingTile && current.equals(hoverGridPos);

            if (modifier.equals("NORMAL") && !isHovered) {
                Texture tile = terraRegistry.getTile("NORMAL", prev, current, next);

                if (isPlacingTile) game.batch.setColor(0.4f, 1.0f, 0.4f, 0.4f);
                else game.batch.setColor(Color.WHITE);

                game.batch.draw(tile, current.x * TILE_SIZE + offsetX, current.y * TILE_SIZE + offsetY, TILE_SIZE, TILE_SIZE);
            }
        }

        for (int i = 0; i < path.size; i++) {
            Vector2 current = path.get(i);
            Vector2 prev = path.get(i == 0 ? path.size - 1 : i - 1);
            Vector2 next = path.get((i + 1) % path.size);

            String modifier = roadModifiers.get(current, "NORMAL");
            boolean isHovered = isPlacingTile && current.equals(hoverGridPos);
            // Определяем тип местности
            String activeType = isHovered ? selectedCard.getTerrainType() : modifier;

            if (!activeType.equals("NORMAL")) {
                Texture tile = terraRegistry.getTile(activeType, prev, current, next);
                if (isHovered) game.batch.setColor(1.0f, 1.0f, 1.0f, 0.7f); // Призрак
                else if (isPlacingTile) game.batch.setColor(0.4f, 1.0f, 0.4f, 0.4f);
                else game.batch.setColor(Color.WHITE);
                game.batch.draw(tile, current.x * TILE_SIZE + offsetX, current.y * TILE_SIZE + offsetY, TILE_SIZE, TILE_SIZE);
            }
        }
        game.batch.setColor(Color.WHITE);
    }

    private void startFrogMovement() {
        if (path == null || path.size == 0) return;
        isMoving = true;
        currentPathIndex = 0;
        Vector2 startTile = path.get(0);
        frogScreenPos.set(
            startTile.x * TILE_SIZE + offsetX,
            startTile.y * TILE_SIZE + offsetY
        );
    }

    private void updateFrog(float delta) {
        if (!isMoving || path == null || path.size == 0) return;
        // следующая точка
        int nextIndex = (currentPathIndex + 1) % path.size;
        Vector2 targetTile = path.get(nextIndex);
        float targetX = targetTile.x * TILE_SIZE + offsetX;
        float targetY = targetTile.y * TILE_SIZE + offsetY;
        //расстояние до цели
        float distance = Vector2.dst(frogScreenPos.x, frogScreenPos.y, targetX, targetY);
        float moveDist = movementSpeed * delta;
        if (moveDist >= distance) {
            frogScreenPos.set(targetX, targetY);
            currentPathIndex = nextIndex;
        } else {
            Vector2 direction = new Vector2(targetX - frogScreenPos.x, targetY - frogScreenPos.y).nor();
            frogScreenPos.add(direction.scl(moveDist));
        }
    }

    private void initUI() {
        Table root = new Table();
        root.setFillParent(true); // Таблица на весь экран
        stage.addActor(root);
        runBtn = new TextButton("GO", game.skin);
        runBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startFrogMovement();
            }
        });
        // Игровая зона + Карты
        Table leftSide = new Table();
        //область для дороги
        leftSide.add().expand().fill();
        leftSide.row();
        // Нижняя область для карт
        cardHand = new CardArea(game.skin, this);
        groveCard = new CardActor(new TextureRegion(groveCardTexture), "GROVE");
        cardHand.addCard(groveCard);
        swampCard = new CardActor(new TextureRegion(swampCardTexture), "SWAMP");
        cardHand.addCard(swampCard);
        leftSide.add(cardHand).expandX().fillX().height(68);
        // Меню и статы
        Table rightSidebar = new Table();
        rightSidebar.setBackground(game.skin.newDrawable("white-pixel", Color.valueOf("#34495e")));
        // Свойства персонажа
        rightSidebar.add(new Label("HP: 100/100", game.skin)).padTop(10).row();
        rightSidebar.add(new Label("ATK: 10", game.skin)).row();
        // Ящик с лутом (просто картинка пока)
        Image chest = new Image(game.assets.getTexture(Assets.LILY_PAD)); // Заглушка
        rightSidebar.add(chest).size(64).pad(20).row();
        // Кнопки меню
        rightSidebar.add(runBtn).width(70).padBottom(5).row();
        // Кнопка Назад через UIStyles
        root.add(leftSide).expand().fill();
        root.add(rightSidebar).width(138).fillY();
        //тут поведение кнопок
        UIStyles.addBackButton(stage, game.skin, () -> {
            hubScreen.setFrogSpawned(true);
            isMoving = false;
            game.setScreen(hubScreen);
        });
    }

    private void handleMapClicks() {
        if (isPlacingTile && Gdx.input.justTouched()) {
            Vector2 clickedCell = getGridPosFromMouse();
            boolean success = placementManager.tryPlaceTerrain(clickedCell, selectedCard.getTerrainType());
            if (success) {
                isPlacingTile = false;
                hoverGridPos.set(-1, -1);

                if (selectedCard != null) {
                    cardHand.removeCard(selectedCard);
                    selectedCard = null;
                }
            }
        }
    }

    private Vector2 getGridPosFromMouse() {
        Vector2 touch = stage.getViewport().unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        int gridX = MathUtils.floor((touch.x - offsetX) / TILE_SIZE);
        int gridY = MathUtils.floor((touch.y - offsetY) / TILE_SIZE);
        return new Vector2(gridX, gridY);
    }

    public boolean isFrogMoving() { return isMoving; }
    public void setPlacingTile(boolean placing) { this.isPlacingTile = placing; }
    public void setSelectedCard(CardActor card) { this.selectedCard = card; }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1);
        if (isPlacingTile) {
            // пиксели мыши в координаты Stage
            Vector2 currentMouseGrid = getGridPosFromMouse();
            hoverGridPos.set(currentMouseGrid.x, currentMouseGrid.y);
            handleMapClicks();
        } else {
            hoverGridPos.set(-1, -1);
        }
        game.batch.begin();
        game.batch.draw(bgTexture, 0, 0, 480, 270);
        drawRoad();
        updateFrog(delta);
        if (isMoving) {
            game.batch.draw(frogTexture, frogScreenPos.x, frogScreenPos.y, TILE_SIZE, TILE_SIZE);
        }
        game.batch.end();
        stage.act(delta);
        stage.draw();
    }
    @Override public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
    }
}
