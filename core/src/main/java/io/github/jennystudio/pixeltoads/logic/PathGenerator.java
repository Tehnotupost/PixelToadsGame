package io.github.jennystudio.pixeltoads.logic;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class PathGenerator {
    private final int width;
    private final int height;

    public PathGenerator(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Array<Vector2> generateLoopPath() {
        Array<Vector2> path = new Array<>();
        boolean success = false;
        int maxGlobalAttempts = 100;
        int globalAttempts = 0;

        while (!success && globalAttempts < maxGlobalAttempts) {
            globalAttempts++;
            path.clear();

            Vector2 startPos = new Vector2(3, MathUtils.random(5, 9));
            Vector2 endPos = new Vector2(width - 3, MathUtils.random(5, 9));

            Array<Vector2> topPath = buildGuidedPath(startPos, endPos, true, new Array<>(), 7);
            if (topPath == null) continue;

            Array<Vector2> bottomPath = buildGuidedPath(startPos, endPos, false, topPath, 7);
            if (bottomPath != null) {
                path.addAll(topPath);
                bottomPath.reverse();
                bottomPath.removeIndex(0);
                bottomPath.removeIndex(bottomPath.size - 1);
                path.addAll(bottomPath);
                success = true;
            }
        }
        return path;
    }

    private Array<Vector2> buildGuidedPath(Vector2 start, Vector2 end, boolean preferUp, Array<Vector2> avoidList, int minLen) {
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
                if (check.x >= 2 && check.x < width - 2 && check.y >= 3 && check.y < height - 2) {
                    if (check.equals(end)) {
                        if (currentPath.size >= minLen) neighbors.add(check);
                        continue;
                    }
                    if (!currentPath.contains(check, false) && !isTouchingList(check, avoidList) && countPathNeighbors(check, currentPath) == 1) {
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
}
