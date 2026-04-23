package io.github.jennystudio.pixeltoads.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class UIStyles {
    public static Skin createDefaultSkin() {
        Skin skin = new Skin();
        // 1. Шрифт
        BitmapFont font = new BitmapFont();
        skin.add("default-font", font);
        // 2. Текстура для фона кнопок
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.valueOf("#dff6f5"));
        pixmap.fill();
        Texture whiteTexture = new Texture(pixmap);
        skin.add("white-pixel", whiteTexture);
        pixmap.dispose();
        // 3. Стиль ТЕКСТОВОЙ КНОПКИ
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.newDrawable("white-pixel", Color.valueOf("#397b44"));
        textButtonStyle.down = skin.newDrawable("white-pixel", Color.valueOf("#71aa34"));
        textButtonStyle.over = skin.newDrawable("white-pixel", Color.valueOf("#b6d53c"));
        textButtonStyle.font = skin.getFont("default-font");
        textButtonStyle.fontColor = Color.WHITE;
        skin.add("default", textButtonStyle);
        // 4. Настраиваем стиль НАДПИСИ (Label)
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = skin.getFont("default-font");
        labelStyle.fontColor = Color.valueOf("#dff6f5");
        skin.add("default", labelStyle);
        return skin;
    }

    public static void addBackButton(Stage stage, Skin skin, Runnable action) {
        TextButton backBtn = new TextButton("<=", skin);
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });

        Table table = new Table();
        table.top().left();
        table.setFillParent(true);
        table.add(backBtn).pad(10).width(20).height(20);
        stage.addActor(table);
    }
}
