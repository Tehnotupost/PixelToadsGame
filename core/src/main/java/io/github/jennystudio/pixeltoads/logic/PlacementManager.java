package io.github.jennystudio.pixeltoads.logic;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Logger;

public class PlacementManager {
    private static final Logger log = new Logger("PlacementManager", Logger.INFO);
    private final Array<Vector2> path;
    private final ObjectMap<Vector2, String> roadModifiers;

    // ссылки на дорогу и модификаторы из экрана битвы
    public PlacementManager(Array<Vector2> path, ObjectMap<Vector2, String> roadModifiers) {
        this.path = path;
        this.roadModifiers = roadModifiers;
    }

    public boolean tryPlaceTerrain(Vector2 gridPos, String terrainType) {
        if (path == null || path.size == 0) return false;
        for (Vector2 roadTile : path) {
            if ((int)roadTile.x == (int)gridPos.x && (int)roadTile.y == (int)gridPos.y) {
                roadModifiers.put(roadTile, terrainType);
                log.info("Тайл дороги изменен на: " + terrainType + " в точке " + gridPos);
                return true;
            }
        }
        log.info("Клик мимо дороги в точке: " + gridPos);
        return false;
    }
}
