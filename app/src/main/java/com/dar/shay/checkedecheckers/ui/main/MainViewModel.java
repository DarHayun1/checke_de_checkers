package com.dar.shay.checkedecheckers.ui.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dar.shay.checkedecheckers.datamodels.Game;
import com.dar.shay.checkedecheckers.datamodels.Point;
import com.dar.shay.checkedecheckers.datamodels.Square;
import com.dar.shay.checkedecheckers.datamodels.TilePickResult;

public class MainViewModel extends ViewModel {

    Game game = new Game(true);
    private MutableLiveData<Square[][]> boardLiveData = new MutableLiveData<>();

    public LiveData<Square[][]> getBoard() {
        return boardLiveData;
    }

    public void refreshData() {
        Log.d("Checke", game.toString());
        boardLiveData.setValue(game.getBoard());
    }

    public void newGame(boolean is_black_start) {
        game = new Game(is_black_start);
        refreshData();
    }

    public TilePickResult tileClicked(Point destination) {
        TilePickResult tilePickResult = game.tilePick(destination);
        refreshData();
        return tilePickResult;
    }
}