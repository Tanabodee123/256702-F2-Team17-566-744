package com.project;

import java.io.Serializable;
import java.util.List;

public class SaveData implements Serializable {
    private static final long serialVersionUID = 1L;

    public int playerHP;
    public int potionTimer;
    public boolean isShieldActive;
    public int score;

    public double playerX;
    public double playerY;

    public List<Double> enemyPositionsX;
    public List<Double> enemyPositionsY;
}
