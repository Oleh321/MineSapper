package code;

import code.enums.CellStatus;

/**
 * Клетка
 */
public class Cell {

    public final static int SIZE = 30;

    private int positionX;
    private int positionY;
    private CellStatus cellStatus;
    private int value;

    public Cell(int position_x, int position_y, CellStatus cellStatus) {
        this.positionX = position_x;
        this.positionY = position_y;
        this.cellStatus = cellStatus;
    }

    public int getPositionX() {
        return positionX;
    }

    public void setPositionX(int positionX) {
        this.positionX = positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    public void setPositionY(int positionY) {
        this.positionY = positionY;
    }

    public CellStatus getCellStatus() {
        return cellStatus;
    }

    public void setCellStatus(CellStatus cellStatus) {
        this.cellStatus = cellStatus;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
