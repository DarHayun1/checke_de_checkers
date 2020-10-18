package com.dar.shay.checkedecheckers.ui.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dar.shay.checkedecheckers.datamodels.Game;
import com.dar.shay.checkedecheckers.datamodels.Point;
import com.dar.shay.checkedecheckers.datamodels.Square;

public class MainViewModel extends ViewModel {

    Game game = new Game();
    private MutableLiveData<Square[][]> boardLiveData = new MutableLiveData<>();

    public LiveData<Square[][]> getBoard() {
        return boardLiveData;
    }

    public void refreshData(){
        Log.d("Checke", game.toString());
        boardLiveData.setValue(game.getBoard());
    }

    public void move(Point origin, Point destination) {
        game.move(origin, destination, true);
        refreshData();
    }
}