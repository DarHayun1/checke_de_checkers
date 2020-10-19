package com.dar.shay.checkedecheckers.ui.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;

import com.dar.shay.checkedecheckers.MoveListener;
import com.dar.shay.checkedecheckers.R;
import com.dar.shay.checkedecheckers.datamodels.Point;
import com.dar.shay.checkedecheckers.datamodels.Square;

public class BoardView extends FrameLayout {

    private ImageView[][] tiles = new ImageView[8][8];
    private MoveListener mCallback;

    public BoardView(Context context) {
        this(context, null, 0);
    }

    public BoardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BoardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.board_square, this);
        bindViews();

    }

    private void bindViews() {
        LinearLayout rowsLl = findViewById(R.id.rows_ll);
        for (int i = 0; i < 8; i++) {
            LinearLayout ll = (LinearLayout) rowsLl.getChildAt(i);
            for (int j = 0; j < 8; j++) {
                tiles[i][j] = (ImageView) ll.getChildAt(j);
                if (i % 2 == 0 && j % 2 == 1 || i % 2 == 1 && j % 2 == 0) {
                    int finalI = i;
                    int finalJ = j;
                    tiles[i][j].setOnClickListener(
                            v -> tileClicked(finalI, finalJ));
                }
            }
        }
    }

    private void tileClicked(int x, int y) {
        mCallback.tileClicked(new Point(x, y));
    }

    public void setBoard(Square[][] board) {

        for (int i = 0; i < board.length; i++) {
            Square[] row = board[i];
            for (int j = 0; j < row.length; j++) {
                Log.d("Checke", i + "," + j + row[j].toString());
                changeTile(tiles[i][j], row[j]);
            }
        }
    }

    private void changeTile(ImageView view, Square state) {
        switch (state) {

            case BLACK_SOLDIER:
                view.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.black_soldier_ic));
                break;
            case WHITE_SOLDIER:
                view.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.white_soldier_ic));
                break;
            default:
                view.setImageDrawable(null);
        }
    }

    public void setMoveListener(MoveListener callback) {
        mCallback = callback;
    }
}
