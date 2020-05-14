package pl.wsiz.greatlabyrinth;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class GameView extends View {

    private Cell[][] cells;
    private static final int columns = 7, rows = 10;
    private static final float wallThickness = 4;
    private float cellSize, hMargin, vMargin;
    private Paint wallPaint;

    public GameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        wallPaint = new Paint();
        wallPaint.setColor(Color.BLACK);
        wallPaint.setStrokeWidth(wallThickness);

        createLabyrinth();
    }

    private void createLabyrinth(){
        cells = new Cell[columns][rows];

        for(int i=0; i<columns; i++){
            for(int j=0; j<rows;j++){
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.GREEN);

        int width = getWidth();
        int height = getHeight();

        if(width/height < columns/rows) {
            cellSize = width/(columns+1);
        }
        else {
            cellSize = height/(rows+1);
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
    }

    private class Cell {
        boolean leftWall = true;
        boolean rightWall = true;
        boolean topWall = true;
        boolean bottomWall = true;
        int column, row;

        public Cell(int column, int row) {
            this.column = column;
            this.row = row;
        }
    }
}
