package com.dar.shay.checkedecheckers.ui.main;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dar.shay.checkedecheckers.AppExecutors;
import com.dar.shay.checkedecheckers.datamodels.Game;
import com.dar.shay.checkedecheckers.datamodels.Point;
import com.dar.shay.checkedecheckers.datamodels.Square;
import com.dar.shay.checkedecheckers.datamodels.TilePickResult;

public class MainViewModel extends ViewModel {

    private Game game = new Game(false);
    private MutableLiveData<Square[][]> boardLiveData = new MutableLiveData<>();


    private MutableLiveData<Boolean> bTurnLiveData = new MutableLiveData<>(true);


    public LiveData<Square[][]> getBoard() {
        return boardLiveData;
    }

    public LiveData<Boolean> getBTurnLiveData() {
        return bTurnLiveData;
    }

    public void refreshData() {
        Log.d("Checke", "Refresh!");
        boardLiveData.postValue(game.getBoard());
        bTurnLiveData.postValue(game.isBlackTurn());
    }

    public void newGame(boolean is_black_start) {
        game = new Game(is_black_start);
        refreshData();
    }

    public TilePickResult tileClicked(Point destination) {
        return game.tilePick(destination);
    }

    public void agentMove() {
        AppExecutors.getInstance().diskIO().execute(() -> {
            if (game.isBlackTurn())
                game.agentMove();
            refreshData();
        });
    }
}