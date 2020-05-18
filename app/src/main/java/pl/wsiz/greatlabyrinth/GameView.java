package pl.wsiz.greatlabyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class GameView extends View {

    private enum Direction{
        up, down, left, right
    }

    private Cell[][] cells;
    private Cell player, exit;
    private static final int columns = 7, rows = 10;
    private static final float wallThickness = 4;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint, playerPaint, exitPaint;
    private Random random;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(wallThickness);

        playerPaint = new Paint();
        playerPaint.setColor(Color.RED);

        exitPaint = new Paint();
        exitPaint.setColor(Color.BLUE);

        random = new Random();

        createLabyrinth();
    }

    private Cell getNeighbour(Cell cell){
        ArrayList<Cell> neighbours = new ArrayList<>();

        //left neighbour
        if(cell.column > 0) {
            if (!cells[cell.column - 1][cell.row].visited) {
                neighbours.add(cells[cell.column - 1][cell.row]);
            }
        }

        //right neighbour
        if(cell.column < columns-1) {
            if (!cells[cell.column + 1][cell.row].visited) {
                neighbours.add(cells[cell.column + 1][cell.row]);
            }
        }

        //top neighbour
        if(cell.row > 0) {
            if (!cells[cell.column][cell.row - 1].visited) {
                neighbours.add(cells[cell.column][cell.row - 1]);
            }
        }

        //bottom neighbour
        if(cell.row < rows-1) {
            if (!cells[cell.column][cell.row + 1].visited) {
                neighbours.add(cells[cell.column][cell.row + 1]);
            }
        }

        if(neighbours.size() > 0) {
            int index = random.nextInt(neighbours.size());
            return neighbours.get(index);
        }
        return null;
    }

    private void removeWall(Cell current, Cell next){
        if(current.column == next.column && current.row == next.row+1) {
            current.topWall = false;
            next.bottomWall = false;
        }
        if(current.column == next.column && current.row == next.row-1) {
            current.bottomWall = false;
            next.topWall = false;
        }
        if(current.column == next.column+1 && current.row == next.row) {
            current.leftWall = false;
            next.rightWall = false;
        }
        if(current.column == next.column-1 && current.row == next.row) {
            current.rightWall = false;
            next.leftWall = false;
        }
    }

    private void createLabyrinth(){
        Stack<Cell> stack = new Stack<>();
        Cell current, next;

        cells = new Cell[columns][rows];

        for(int i=0; i<columns; i++){
            for(int j=0; j<rows;j++){
                cells[i][j] = new Cell(i, j);
            }
        }

        player = cells[0][0];
        exit = cells[columns-1][rows-1];

        current = cells[0][0];
        current.visited = true;

        do {
            next = getNeighbour(current);
            if (next != null) {
                removeWall(current, next);
                stack.push(current);
                current = next;
                current.visited = true;
            } else
                current = stack.pop();
        }while(!stack.empty());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GREEN);

        int width = getWidth();
        int height = getHeight();

        if(width/height < columns/rows) {
            cellSize = width/(columns+3);
        }
        else {
            cellSize = height/(rows+3);
        }

        hMargin = (width-columns*cellSize)/2;
        vMargin = (height-rows*cellSize)/2;

        canvas.translate(hMargin, vMargin);

        for(int i=0; i<columns; i++){
            for(int j=0; j<rows;j++){
                if(cells[i][j].topWall)
                    canvas.drawLine(i*cellSize, j*cellSize, (i+1)*cellSize,j*cellSize, wallPaint);
                if(cells[i][j].leftWall)
                    canvas.drawLine(i*cellSize, j*cellSize, i*cellSize,(j+1)*cellSize, wallPaint);
                if(cells[i][j].bottomWall)
                    canvas.drawLine(i*cellSize, (j+1)*cellSize, (i+1)*cellSize,(j+1)*cellSize, wallPaint);
                if(cells[i][j].rightWall)
                    canvas.drawLine((i+1)*cellSize, j*cellSize, (i+1)*cellSize,(j+1)*cellSize, wallPaint);
            }
        }

        float margin = cellSize/10;

        canvas.drawRect(player.column*cellSize+margin, player.row*cellSize+margin,(player.column+1)*cellSize-margin,(player.row+1)*cellSize-margin, playerPaint);
        canvas.drawRect(exit.column*cellSize+margin, exit.row*cellSize+margin,(exit.column+1)*cellSize-margin,(exit.row+1)*cellSize-margin, exitPaint);
    }

    private void movePlayer(Direction direction){
        switch (direction){
            case up:
                if(!player.topWall)
                player = cells[player.column][player.row-1];
                break;
            case down:
                if(!player.bottomWall)
                    player = cells[player.column][player.row+1];
                break;
            case left:
                if(!player.leftWall)
                    player = cells[player.column-1][player.row];
                break;
            case right:
                if(!player.rightWall)
                    player = cells[player.column+1][player.row];
                break;
        }

        checkExit();
        invalidate();
    }

    private void checkExit(){
        if(player == exit)
            createLabyrinth();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN)
            return true;

        if(event.getAction() == MotionEvent.ACTION_MOVE){
            float x = event.getX();
            float y = event.getY();

            float playerCenterX = hMargin + (player.column+0.5f)*cellSize;
            float playerCenterY = vMargin + (player.row+0.5f)*cellSize;

            float xDirection = x - playerCenterX;
            float yDirection = y - playerCenterY;

            float absXD = Math.abs(xDirection);
            float absYD = Math.abs(yDirection);

            if(absXD > cellSize || absYD > cellSize){
                if(absXD>absYD){
                    //move in x-direction
                    if(xDirection>0){
                        movePlayer(Direction.right);
                    }
                    else{
                        movePlayer(Direction.left);
                    }
                }
                else {
                    //move in y-direction
                    if (yDirection > 0) {
                        movePlayer(Direction.down);
                    } else {
                        movePlayer(Direction.up);
                    }
                }
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    private class Cell {
        boolean leftWall = true;
        boolean rightWall = true;
        boolean topWall = true;
        boolean bottomWall = true;
        boolean visited = false;
        int column, row;

        public Cell(int column, int row) {
            this.column = column;
            this.row = row;
        }
    }
}
