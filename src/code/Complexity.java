package code;

/**
 * Сложность
 */
public class Complexity {
    private String title;
    private int rows;
    private int columns;
    private int bombs;

    public Complexity(String title, int rows, int columns, int bombs) {
        this.title = title;
        this.rows = rows;
        this.columns = columns;
        this.bombs = bombs;
    }

    public Complexity() {    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getBombs() {
        return bombs;
    }

    public void setBombs(int bombs) {
        this.bombs = bombs;
    }
}
