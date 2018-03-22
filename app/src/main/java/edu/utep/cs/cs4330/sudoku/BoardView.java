package edu.utep.cs.cs4330.sudoku;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import java.text.CollationElementIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.utep.cs.cs4330.sudoku.model.Board;
import edu.utep.cs.cs4330.sudoku.model.Square;

/**
 * A special view class to display a Sudoku board modeled by the
 * {@link edu.utep.cs.cs4330.sudoku.model.Board} class. You need to write code for
 * the <code>onDraw()</code> method.
 *
 * @see edu.utep.cs.cs4330.sudoku.model.Board
 * @author cheon
 */
public class BoardView extends View {

    /** To notify a square selection. */
    public interface SelectionListener {

        /** Called when a square of the board is selected by tapping.
         * */
        void onSelection(Square square);
    }

    /** Listeners to be notified when a square is selected. */
    private final List<SelectionListener> listeners = new ArrayList<>();

    /** Number of squares in rows and columns.*/
    private int boardSize = 9;

    /** Board to be displayed by this view. */
    private Board board;

    /** Width and height of each square. This is automatically calculated
     * this view's dimension is changed. */
    private float squareSize;

    /** Translation of screen coordinates to display the grid at the center. */
    private float transX;

    /** Translation of screen coordinates to display the grid at the center. */
    private float transY;

    /** Current selected square. */
    public Square selectedSquare;

    /** Paint to draw the background of the grid. */
    private final Paint boardPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    {
        int boardColor = Color.rgb(201, 186, 145);
        boardPaint.setColor(boardColor);
        boardPaint.setAlpha(80); // semi transparent
    }

    /** Create a new board view to be run in the given context. */
    public BoardView(Context context) { //@cons
        this(context, null);
    }

    /** Create a new board view by inflating it from XML. */
    public BoardView(Context context, AttributeSet attrs) { //@cons
        this(context, attrs, 0);
    }

    /** Create a new instance by inflating it from XML and apply a class-specific base
     * style from a theme attribute. */
    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSaveEnabled(true);
        getViewTreeObserver().addOnGlobalLayoutListener(layoutListener);
    }

    /** Set the board to be displayed by this view. */
    public void setBoard(Board board) {
        this.board = board;
        boardSize = board.size;
    }

    /** Draw a 2-D graphics representation of the associated board. */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(transX, transY);
        if (board != null) {
            drawGrid(canvas);
            drawSquares(canvas);
            drawSelectedSquare(canvas);
        }
        canvas.translate(-transX, -transY);
    }

    private void drawSelectedSquare(Canvas canvas) {
        // WRITE YOUR CODE HERE ...
        Paint p = new Paint();
        p.setColor(Color.rgb(154, 205, 50));
        p.setAlpha(60);
        if(selectedSquare == null) return;
        float x = selectedSquare.getX() * lineGap();
        float y = selectedSquare.getY() * lineGap();
        canvas.drawRect(x, y, x + lineGap(), y + lineGap(), p);
    }

    /** Draw horizontal and vertical grid lines. */
    private void drawGrid(Canvas canvas) {
        final float maxCoord = maxCoord();
        canvas.drawRect(0, 0, maxCoord, maxCoord, boardPaint);
        // WRITE YOUR CODE HERE ...
        Paint dark = new Paint();
        dark.setColor(Color.rgb(202, 187, 146));

        Paint thick = new Paint();
        thick.setColor(Color.rgb(202, 187, 146));
        thick.setStrokeWidth(10);

        double hard = Math.sqrt(boardSize);
        float diff = (float) maxCoord() / boardSize;
        for(float x = 0; x < maxCoord(); x += diff) {
            for(float y = 0; y < maxCoord(); y += diff) {
                if(x%Math.floor(maxCoord() / hard) == 0 && x != 0) {
                    canvas.drawLine(x,y,x, y + diff, thick); // left
                }
                if(y%Math.floor(maxCoord() / hard) == 0 && y != 0) {
                    canvas.drawLine(x,y,x + diff, y, thick);// top
                }
                canvas.drawLine(x,y,x, y + diff, dark); // left
                canvas.drawLine(x + diff,y,x + diff, y + diff, dark); // right
                canvas.drawLine(x+ diff,y + diff, x, y + diff, dark);// bottom
                canvas.drawLine(x,y,x + diff, y, dark);// top
            }
        }
        //
    }

    /** Draw all the squares (numbers) of the associated board. */
    private void drawSquares(Canvas canvas) {
        Paint dark = new Paint();
        dark.setColor(Color.rgb(202, 187, 146));


        Collection<Square> squareCollection = board.getSquares();
        float center = boardSize * (float) Math.sqrt(boardSize);
        for(Square square : squareCollection) {
            float x;
            float y;
            if(boardSize == 9) {
                dark.setTextSize(100);
                x = square.getX() * lineGap() + squareSize / 4;
                y = ((square.getY() * lineGap()) + lineGap());
            }else {
                dark.setTextSize(175);
                x = square.getX() * (lineGap()) + squareSize/(float)3.25;
                y = ((square.getY() * lineGap()) + lineGap()/(float)1.25);
            }

            if(square.getValue() == -1 || square.getValue() == 0) {
                canvas.drawText("", x,y-center,dark);
            }
            else {
                canvas.drawText(square.getValue() + "", x, y-center, dark);
            }
        }
    }

    /** Overridden here to detect tapping on the board and
     * to notify the selected square if exists. */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int xy = locateSquare(event.getX(), event.getY());
                if (xy >= 0) {
                    // xy encoded as: x * 100 + y
                    int x = xy / 100;
                    int y = xy % 100;
                    Collection<Square> squares = board.getSquares();
                    Square selectedSquare = null;
                    for(Square square : squares) {
                        if(square.getX() == x && square.getY() == y) {
                            selectedSquare = square;
                        }
                    }
                    notifySelection(selectedSquare);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    /**
     * Given screen coordinates, locate the corresponding square of the board, or
     * -1 if there is no corresponding square in the board.
     * The result is encoded as <code>x*100 + y</code>, where x and y are 0-based
     * column/row indexes of the corresponding square.
     */
    private int locateSquare(float x, float y) {
        x -= transX;
        y -= transY;
        if (x <= maxCoord() &&  y <= maxCoord()) {
            final float squareSize = lineGap();
            int ix = (int) (x / squareSize);
            int iy = (int) (y / squareSize);
            return ix * 100 + iy;
        }
        return -1;
    }

    /** To obtain the dimension of this view. */
    private final ViewTreeObserver.OnGlobalLayoutListener layoutListener
            =  new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            squareSize = lineGap();
            float width = Math.min(getMeasuredWidth(), getMeasuredHeight());
            transX = (getMeasuredWidth() - width) / 2f;
            transY = (getMeasuredHeight() - width) / 2f;
        }
    };

    /** Return the distance between two consecutive horizontal/vertical lines. */
    protected float lineGap() {
        return Math.min(getMeasuredWidth(), getMeasuredHeight()) / (float) boardSize;
    }

    /** Return the number of horizontal/vertical lines. */
    private int numOfLines() { //@helper
        return boardSize + 1;
    }

    /** Return the maximum screen coordinate. */
    protected float maxCoord() { //@helper
        return lineGap() * (numOfLines() - 1);
    }

    /** Register the given listener. */
    public void addSelectionListener(SelectionListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /** Unregister the given listener. */
    public void removeSelectionListener(SelectionListener listener) {
        listeners.remove(listener);
    }

    /** Notify a square selection to all registered listeners.
     *
     */
    private void notifySelection(Square square) {
        for (SelectionListener listener: listeners) {
            listener.onSelection(square);
        }
    }
}
