package io.github.jennystudio.pixeltoads.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import io.github.jennystudio.pixeltoads.PixelToadsGame;
import io.github.jennystudio.pixeltoads.ui.UIStyles;
import io.github.jennystudio.pixeltoads.utils.Assets;

public class BattleScreen implements Screen {
    final PixelToadsGame game;
    private Stage stage;
    private Skin skin;
    private final HubScreen hubScreen;
    private Texture bgTexture, RHTexture, RVTexture, RURTexture, RDRTexture, RULTexture, RDLTexture;
    private boolean isFirstRunDone;
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

    public BattleScreen(final PixelToadsGame game, Skin skin, HubScreen hubScreen) {
        this.game = game;
        this.skin = skin;
        this.stage = new Stage(game.viewport);
        this.hubScreen = hubScreen;
        loadTexture();
        generateLoopPath();
        initUI();
    }

    private void loadTexture(){
        bgTexture = game.assets.getTexture(Assets.RUN_MAP);
        RHTexture = game.assets.getTexture(Assets.ROAD_H);
        RVTexture = game.assets.getTexture(Assets.ROAD_V);
        RURTexture = game.assets.getTexture(Assets.ROAD_UR);
        RDRTexture = game.assets.getTexture(Assets.ROAD_DR);
        RULTexture = game.assets.getTexture(Assets.ROAD_UL);
        RDLTexture = game.assets.getTexture(Assets.ROAD_DL);
        frogTexture = game.assets.getTexture(Assets.FROG_L);
    }

    private void generateLoopPath() {
        path = new Array<>();
        int width = 15;
        int height = 12;
        boolean success = false;
        int maxGlobalAttempts = 100;
        int globalAttempts = 0;

        while (!success && globalAttempts < maxGlobalAttempts) {
            globalAttempts++;
            path.clear();
            Vector2 startPos = new Vector2(3, MathUtils.random(5, 9));
            Vector2 endPos = new Vector2(width - 3, MathUtils.random(5, 9));
            Array<Vector2> topPath = buildGuidedPath(startPos, endPos, width, height, true, new Array<>(), 7);
            if (topPath == null) continue;
            Array<Vector2> bottomPath = buildGuidedPath(startPos, endPos, width, height, false, topPath, 7);
            if (bottomPath != null) {
                path.addAll(topPath);
                bottomPath.reverse();
                bottomPath.removeIndex(0);
                bottomPath.removeIndex(bottomPath.size - 1);
                path.addAll(bottomPath);
                success = true;
            }
        }
    }

    private Array<Vector2> buildGuidedPath(Vector2 start, Vector2 end, int w, int h, boolean preferUp, Array<Vector2> avoidList, int minLen) {
        Array<Vector2> currentPath = new Array<>();
        Vector2 current = new Vector2(start);
        currentPath.add(new Vector2(current));
        Vector2 lastDir = null;

        int safety = 0;
        while (!current.equals(end) && safety < 100) {
            safety++;
            Array<Vector2> neighbors = new Array<>();
            Vector2[] dirs = {new Vector2(1, 0), new Vector2(-1, 0), new Vector2(0, 1), new Vector2(0, -1)};
            for (Vector2 d : dirs) {
                Vector2 check = new Vector2(current.x + d.x, current.y + d.y);
                // Проверка границ
                if (check.x >= 2 && check.x < w-2 && check.y >= 3 && check.y < h-2) {
                    if (check.equals(end)) {
                        if (currentPath.size >= minLen) neighbors.add(check);
                        continue;
                    }
                    // не наступать на себя и не трогать avoidList
                    if (!currentPath.contains(check, false) && !isTouchingList(check, avoidList) && countPathNeighbors(check, currentPath)==1) {
                        neighbors.add(check);
                    }
                }
            }
            if (neighbors.size == 0) return null;
            Vector2 prev = new Vector2(current);
            current = selectBestNeighbor(neighbors, end, preferUp, currentPath, lastDir);
            currentPath.add(new Vector2(current));
            lastDir = new Vector2(current.x - prev.x, current.y - prev.y);
        }
        return (current.equals(end) && currentPath.size >= minLen) ? currentPath : null;
    }

    private int countPathNeighbors(Vector2 pos, Array<Vector2> currentPath) {
        int count = 0;
        Vector2[] dirs = {new Vector2(1,0), new Vector2(-1,0), new Vector2(0,1), new Vector2(0,-1)};
        for (Vector2 d : dirs) {
            if (currentPath.contains(new Vector2(pos.x + d.x, pos.y + d.y), false)) {
                count++;
            }
        }
        return count;
    }

    private Vector2 selectBestNeighbor(Array<Vector2> neighbors, Vector2 end, boolean preferUp, Array<Vector2> currentPath, Vector2 lastDir) {
        Vector2 best = neighbors.random();
        float bestScore = Float.MAX_VALUE;
        Vector2 lastPos = currentPath.peek();
        for (Vector2 n : neighbors) {
            float dist = n.dst2(end);
            Vector2 thisDir = new Vector2(n.x - lastPos.x, n.y - lastPos.y);
            float inertiaPenalty = 0;
            if (lastDir != null && thisDir.equals(lastDir)) {
                inertiaPenalty = 10f;
            }
            float bias = preferUp ? (12 - n.y) : n.y - 1;
            float randomNoise = MathUtils.random(0f, 3f);
            float distWeight = (currentPath.size < 10) ? 0.2f : 1.5f;
            float score = (dist * distWeight) + (bias * 12) + inertiaPenalty + randomNoise;

            if (score < bestScore) {
                bestScore = score;
                best = n;
            }
        }
        if (MathUtils.randomBoolean(0.15f)) return neighbors.random();
        return best;
    }

    private boolean isTouchingList(Vector2 pos, Array<Vector2> currentPath) {
        int neighbors = 0;
        for (Vector2 p : currentPath) {
            if (Math.abs(pos.x - p.x) + Math.abs(pos.y - p.y) == 1) neighbors++;
        }
        return neighbors > 1;
    }

    private void drawRoad() {
        if (path == null || path.size == 0) return;
        for (int i = 0; i < path.size; i++) {
            Vector2 current = path.get(i);
            Vector2 prev = path.get(i == 0 ? path.size - 1 : i - 1);
            Vector2 next = path.get((i + 1) % path.size);

            Texture tile = getTileForPath(prev, current, next);
            game.batch.draw(tile, current.x * TILE_SIZE+ offsetX, current.y * TILE_SIZE+ offsetY, TILE_SIZE, TILE_SIZE);
        }
    }

    private Texture getTileForPath(Vector2 p, Vector2 c, Vector2 n) {
        int dx1 = (int)(p.x - c.x);
        int dy1 = (int)(p.y - c.y);
        int dx2 = (int)(n.x - c.x);
        int dy2 = (int)(n.y - c.y);
        if (dx1 != 0 && dx2 != 0) return RHTexture;
        if (dy1 != 0 && dy2 != 0) return RVTexture;
        // Углы
        if ((dy1 == 1 && dx2 == 1) || (dx1 == 1 && dy2 == 1)) return RDLTexture;
        if ((dy1 == -1 && dx2 == 1) || (dx1 == 1 && dy2 == -1)) return RULTexture;
        if ((dy1 == 1 && dx2 == -1) || (dx1 == -1 && dy2 == 1)) return RDRTexture;
        if ((dy1 == -1 && dx2 == -1) || (dx1 == -1 && dy2 == -1)) return RURTexture;
        return RHTexture;
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
//        Table cardArea = new Table();
//        cardArea.setBackground(game.skin.newDrawable("white-pixel", Color.valueOf("#2c3e50")));
//        cardArea.add(new Label("CARDS SLOT", game.skin)).pad(10);
        // Сюда позже добавим настоящие карты
//        leftSide.add(cardArea).expandX().fillX().height(68);
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
//        UIStyles.addBackButton(rightSidebar.getStage(), game.skin, () -> game.setScreen(hubScreen));
        root.add(leftSide).expand().fill();
        root.add(rightSidebar).width(138).fillY();
//        Table table = new Table();
//        table.bottom().padBottom(10);
//        table.setFillParent(true);
//        stage.addActor(table);
//        runBtn = new TextButton("RUN", game.skin);
//        //тут поведение кнопок
        UIStyles.addBackButton(stage, game.skin, () -> game.setScreen(hubScreen));
//        isFirstRunDone = true;
//        hubScreen.checkFrogAppearance();
//        table.add(runBtn).pad(5);
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
