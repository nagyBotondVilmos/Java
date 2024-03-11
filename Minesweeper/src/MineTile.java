/* 
 * Nev: Nagy Botond-Vilmos
 * Csoport: 523/2
 * Azonosito: nbim2280
 * Feladat: Projekt - Minesweeper
*/

import javax.swing.*;

public class MineTile extends JButton {
    public String type = "";

    private int r;
    private int c;

    public MineTile(int r, int c) {
        this.r = r;
        this.c = c;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public int getR() {
        return r;
    }

    public int getC() {
        return c;
    }
}