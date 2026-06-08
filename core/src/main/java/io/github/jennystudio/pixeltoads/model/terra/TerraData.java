package io.github.jennystudio.pixeltoads.model.terra;

public class TerraData {
    public String name;
    public float speedMultiplier; // 0.5f - замедление в 2 раза
    public int damagePerTurn;     // Урон, если лягушка стоит здесь
    public String textureRegionName; // Имя картинки в атласе

    public TerraData(String name, float speedMultiplier, int damagePerTurn, String texture) {
        this.name = name;
        this.speedMultiplier = speedMultiplier;
        this.damagePerTurn = damagePerTurn;
        this.textureRegionName = texture;
    }
}
