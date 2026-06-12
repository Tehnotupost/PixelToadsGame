package io.github.jennystudio.pixeltoads.logic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import io.github.jennystudio.pixeltoads.screens.BattleScreen;

public class CardArea extends Table {
    private final Skin skin;
    private final BattleScreen battleScreen;
    private final Array<CardActor> cardsInHand;

    public CardArea(Skin skin, BattleScreen battleScreen) {
        this.skin = skin;
        this.battleScreen = battleScreen;
        this.cardsInHand = new Array<>();

        this.setBackground(skin.newDrawable("white-pixel", Color.valueOf("#2c3e50")));
        this.add(new Label("CARDS SLOT", skin)).pad(10);
        rebuildUI();
    }

    private void rebuildUI() {
        this.clearChildren();
        this.add(new Label("CARDS SLOT", skin)).pad(10);
        for (CardActor card : cardsInHand) {
            this.add(card).pad(5).size(45, 64);
        }
    }

    public void addCard(final CardActor card) {
//        if (cardsInHand.size >= 6) {
//            Gdx.app.log("CardHand", "Рука полна! Не удалось добавить карту: " + card.getTerrainType());
//            return;
//        }
        card.setTransform(true);
        card.setOrigin(22.5f, 32f);

        card.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!battleScreen.isFrogMoving()) {
                    battleScreen.setPlacingTile(true);
                    battleScreen.setSelectedCard(card);
                    Gdx.app.log("CardHand", "Активирована карта: " + card.getTerrainType());

                    Actor cardActor = event.getListenerActor();
                    cardActor.clearActions();
                    cardActor.addAction(com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo(
                        1.3f, 1.3f, 0.2f, com.badlogic.gdx.math.Interpolation.swingOut
                    ));
                }
            }
        });
        cardsInHand.add(card);
        rebuildUI();
        Gdx.app.log("CardHand", "Карта " + card.getTerrainType() + " добавлена в руку. Всего карт: " + cardsInHand.size);
//        this.add(card).pad(5).size(45, 64);
    }

    public void removeCard(CardActor card) {
        if (cardsInHand.removeValue(card, true)) {
            card.remove();
            rebuildUI();
            Gdx.app.log("CardHand", "Карта удалена из руки. Осталось карт: " + cardsInHand.size);
        }
    }

    public Array<CardActor> getCardsInHand() {
        return cardsInHand;
    }

}
