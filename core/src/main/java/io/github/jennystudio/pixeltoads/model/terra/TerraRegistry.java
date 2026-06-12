package io.github.jennystudio.pixeltoads.model.terra;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectMap;
import io.github.jennystudio.pixeltoads.PixelToadsGame;
import io.github.jennystudio.pixeltoads.utils.Assets;

public class TerraRegistry {
    private static class TileSet {
        Texture h, v, ur, dr, ul, dl;
        TileSet(Texture h, Texture v, Texture ur, Texture dr, Texture ul, Texture dl) {
            this.h = h; this.v = v; this.ur = ur; this.dr = dr; this.ul = ul; this.dl = dl;
        }
    }

    private final ObjectMap<String, TileSet> registry = new ObjectMap<>();

    public TerraRegistry(PixelToadsGame game) {
        registry.put("NORMAL", new TileSet(
            game.assets.getTexture(Assets.ROAD_H),
            game.assets.getTexture(Assets.ROAD_V),
            game.assets.getTexture(Assets.ROAD_UR),
            game.assets.getTexture(Assets.ROAD_DR),
            game.assets.getTexture(Assets.ROAD_UL),
            game.assets.getTexture(Assets.ROAD_DL)
        ));

        registry.put("GROVE", new TileSet(
            game.assets.getTexture(Assets.GROVE_H),
            game.assets.getTexture(Assets.GROVE_V),
            game.assets.getTexture(Assets.GROVE_UR),
            game.assets.getTexture(Assets.GROVE_DR),
            game.assets.getTexture(Assets.GROVE_UL),
            game.assets.getTexture(Assets.GROVE_DL)
        ));

        registry.put("SWAMP", new TileSet(
            game.assets.getTexture(Assets.SWAMP_H),
            game.assets.getTexture(Assets.SWAMP_V),
            game.assets.getTexture(Assets.SWAMP_UR),
            game.assets.getTexture(Assets.SWAMP_DR),
            game.assets.getTexture(Assets.SWAMP_UL),
            game.assets.getTexture(Assets.SWAMP_DL)
        ));

    }

    public Texture getTile(String terraType, Vector2 p, Vector2 c, Vector2 n) {
        TileSet set = registry.get(terraType);
        if (set == null) set = registry.get("NORMAL");

        int dx1 = (int)(p.x - c.x);
        int dy1 = (int)(p.y - c.y);
        int dx2 = (int)(n.x - c.x);
        int dy2 = (int)(n.y - c.y);

        if (dx1 != 0 && dx2 != 0) return set.h;
        if (dy1 != 0 && dy2 != 0) return set.v;

        if ((dy1 == 1 && dx2 == 1) || (dx1 == 1 && dy2 == 1)) return set.dl;
        if ((dy1 == -1 && dx2 == 1) || (dx1 == 1 && dy2 == -1)) return set.ul;
        if ((dy1 == 1 && dx2 == -1) || (dx1 == -1 && dy2 == 1)) return set.dr;
        if ((dy1 == -1 && dx2 == -1) || (dx1 == -1 && dy2 == -1)) return set.ur;

        return set.h;
    }
}
