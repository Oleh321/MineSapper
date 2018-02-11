package code;

import code.enums.CellStatus;
import code.enums.GameStatus;
import javafx.scene.canvas.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * Игровое поле
 */
public class GameField {
    private int rowAmount;
    private int columnAmount;
    private int bombAmount;
    private int markedAmount;

    private Cell[][] grid;

    private GameStatus currentStatus;

    public GameField(int rowAmount, int columnAmount, int bombAmount) {
        this.rowAmount = rowAmount;
        this.columnAmount = columnAmount;
        this.bombAmount = bombAmount;

        prepareGame();
    }

    public GameStatus open(int x, int y) {
        // Если клетка не закрыта, то мы с ней не сможем взаимодействовать ЛКМ
        if(grid[y][x].getCellStatus()!=CellStatus.CLOSED)
            return currentStatus;
        // Если игра еще не началась, то мы созаем поле и начинаем игру
        if(currentStatus==GameStatus.CREATED) {
            startGame(x, y);
            currentStatus = GameStatus.STARTED;
        }
        // Если мы попали на бомбу, то игра проиграна
        if(grid[y][x].getValue()==9){
            currentStatus = GameStatus.LOSED;
            return currentStatus;
        }
        grid[y][x].setCellStatus(CellStatus.OPENED);
        openNear(x,y);

        if(isWin())
            currentStatus = GameStatus.WON;

        return currentStatus;
    }

    public void marked(int x, int y) {
        // Если клетка открыта, то мы ее не можем маркеровать
        // Мы не можем маркеровать больше, чем колич бомб
        if(grid[y][x].getCellStatus()== CellStatus.OPENED ||
                (markedAmount == bombAmount && grid[y][x].getCellStatus()== CellStatus.CLOSED))
            return;
        if(grid[y][x].getCellStatus()== CellStatus.CLOSED) {
            grid[y][x].setCellStatus(CellStatus.MARKED);
            markedAmount++;
        } else {
            grid[y][x].setCellStatus(CellStatus.CLOSED);
            markedAmount--;
        }

    }

    public GameStatus smartOpen(int x, int y) {
        if(grid[y][x].getCellStatus()!=CellStatus.OPENED)
            return currentStatus;

        int markedNear = 0;

        for(Cell cell : getNeighbors(x,y)){
            if(cell.getCellStatus()==CellStatus.MARKED)
                markedNear++;
        }
        if(markedNear==grid[y][x].getValue())
            for(Cell cell : getNeighbors(x,y))
                if(cell.getCellStatus()==CellStatus.CLOSED)
                    openNear(cell.getPositionX(), cell.getPositionY());

        openNear(x,y);
        if(isWin())
            currentStatus = GameStatus.WON;
        return currentStatus;
    }

    public void prepareGame(){
        grid = new Cell[rowAmount][columnAmount];
        for(int i = 0; i < rowAmount; i++)
            for (int j = 0; j < columnAmount; j++)
                grid[i][j] = new Cell(j, i, CellStatus.CLOSED);

        markedAmount = 0;
        currentStatus = GameStatus.CREATED;
    }

    public void drawLose(int x, int y, Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());

        gc.setFont(javafx.scene.text.Font.font(14));
        for(int i = 0; i < rowAmount; i++)
            for (int j = 0; j < columnAmount; j++) {
                gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                gc.strokeRect(j * Cell.SIZE, i * Cell.SIZE,Cell.SIZE-1, Cell.SIZE-1);

                if(grid[i][j].getValue()==9 && grid[i][j].getCellStatus()== CellStatus.MARKED)
                    gc.drawImage(new javafx.scene.image.Image("images//flag.png"),j*Cell.SIZE+2, i*Cell.SIZE+2, Cell.SIZE-4, Cell.SIZE-4);
                if(grid[i][j].getValue()!=9 && grid[i][j].getCellStatus()== CellStatus.MARKED)
                    gc.drawImage(new javafx.scene.image.Image("images//no_bomb.png"), j*Cell.SIZE+2, i*Cell.SIZE+2, Cell.SIZE-4, Cell.SIZE-4);
                if(grid[i][j].getValue()==9 && grid[i][j].getCellStatus() != CellStatus.MARKED)
                    gc.drawImage(new javafx.scene.image.Image("images//bomb.png"), j*Cell.SIZE+2, i*Cell.SIZE+2, Cell.SIZE-4, Cell.SIZE-4);
            }
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(x * Cell.SIZE, y * Cell.SIZE, Cell.SIZE-1, Cell.SIZE-1);
        gc.drawImage(new javafx.scene.image.Image("images//explosion.png") ,x * Cell.SIZE, y * Cell.SIZE, Cell.SIZE-2, Cell.SIZE-2);

    }

    public void draw(Canvas canvas){
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.fillRect(0,0, canvas.getWidth(), canvas.getHeight());
        gc.setFont(Font.font(14));
        for(int i = 0; i < rowAmount; i++)
            for (int j = 0; j < columnAmount; j++) {
                gc.setFill(javafx.scene.paint.Color.LIGHTGRAY);
                if(grid[i][j].getCellStatus()== CellStatus.OPENED) {
                    gc.strokeRect(j * Cell.SIZE, i * Cell.SIZE, Cell.SIZE-1, Cell.SIZE-1);
                    gc.setFill(javafx.scene.paint.Color.BLACK);
                    if(grid[i][j].getValue()!=0)
                        gc.fillText(String.valueOf(grid[i][j].getValue()), j*Cell.SIZE + 10, i*Cell.SIZE + 20);
                } else {
                    gc.fillRect(j * Cell.SIZE, i * Cell.SIZE, Cell.SIZE - 1, Cell.SIZE - 1);
                    if(grid[i][j].getCellStatus()== CellStatus.MARKED)
                        gc.drawImage(new javafx.scene.image.Image("images//flag.png"),j*Cell.SIZE+2, i*Cell.SIZE+2, Cell.SIZE-4, Cell.SIZE-4);
                }
            }
    }

    public int getMarkedAmount(){
        return markedAmount;
    }

    private void startGame(int xFirstShot, int yFirstShot){
        createBomb(xFirstShot,yFirstShot);
        for(int y = 0; y < rowAmount; y++) {
            for (int x = 0; x < columnAmount; x++) {
                setAmountOfBombAround(x, y);
            }
        }
       //printGameField();
    }

    private void createBomb(int x, int y) {
        int count = 0;
        do{
            int randX = (int) (Math.random()*columnAmount);
            int randY = (int) (Math.random()*rowAmount);

            if((randX!=x && randY!=y) && grid[randY][randX].getValue()!=9){
                grid[randY][randX].setValue(9);
                count++;
            }
        } while(count!=bombAmount);

    }

    private void setAmountOfBombAround(int x, int y){
        if(grid[y][x].getValue()==9)
            return;

        int count = 0;

        for(Cell cell : getNeighbors(x, y))
            if (cell.getValue()==9)
                count++;
        grid[y][x].setValue(count);
    }

    private List<Cell> getNeighbors(int x, int y) {
        List<Cell> neighbors = new ArrayList<>();
        int[] points = new int[] {
                -1, -1,
                -1, 0,
                -1, 1,
                0, -1,
                0, 1,
                1, -1,
                1, 0,
                1, 1
        };

        for (int i = 0; i < points.length; i++) {
            int dy = points[i];
            int dx = points[++i];

            int newY = y + dy;
            int newX = x + dx;

            if (newX >= 0 && newX < columnAmount
                    && newY >= 0 && newY < rowAmount) {
                neighbors.add(grid[newY][newX]);
            }
        }
        return neighbors;
    }

    private boolean isWin(){
        boolean win = true;
        for (int i = 0; i < rowAmount; i++)
            for (int j = 0; j < columnAmount; j++) {
                if(grid[i][j].getCellStatus() == CellStatus.CLOSED && grid[i][j].getValue()!=9)
                    win = false;
            }
        return win;
    }

    private void openNear(int x, int y) {
        // Если возле нее находится бомбы или она промаркирована, то мы не можем идти дальше

        grid[y][x].setCellStatus(CellStatus.OPENED);

        if(grid[y][x].getValue() == 9){
            currentStatus = GameStatus.LOSED;
            return;
        }
        if (grid[y][x].getValue() != 0) {
            return;
        }

        for (Cell cell : getNeighbors(x, y)) {
            if (grid[cell.getPositionY()][cell.getPositionX()].getCellStatus() == CellStatus.CLOSED)
                openNear(cell.getPositionX(), cell.getPositionY());
        }
    }

    private void printGameField() {
        for (int i = 0; i < rowAmount; i++) {
            for (int j = 0; j < columnAmount; j++) {
                System.out.print(" " + grid[i][j].getValue() + " ");
            }
            System.out.println();
        }
    }
}
