package com.dar.shay.checkedecheckers.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dar.shay.checkedecheckers.MoveListener;
import com.dar.shay.checkedecheckers.R;
import com.dar.shay.checkedecheckers.databinding.MainFragmentBinding;
import com.dar.shay.checkedecheckers.datamodels.Point;
import com.dar.shay.checkedecheckers.datamodels.Square;
import com.dar.shay.checkedecheckers.datamodels.TilePickResult;

public class MainFragment extends Fragment implements MoveListener {

    private MainViewModel mViewModel;
    private MainFragmentBinding binding;
    private Context mContext;

    public static MainFragment newInstance() {
        return new MainFragment();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = MainFragmentBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() != null)
            mViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        setupViews();
        setupObservers();
        mViewModel.refreshData();
    }

    private void setupViews() {

        binding.boardView.setMoveListener(this);
        binding.newGameBtn.setOnClickListener(v -> {
            mViewModel.newGame(false);
        });

    }

    private void setupObservers() {

        mViewModel.getBoard().observe(getViewLifecycleOwner(), this::boardStateUpdate);

        mViewModel.getBTurnLiveData().observe(getViewLifecycleOwner(), this::updateTurn);
    }

    private void boardStateUpdate(Square[][] board) {
        if (board != null)
            boardChanged(board);
    }

    private void updateTurn(Boolean is_black_turn) {
        int drawable_id = is_black_turn ? R.drawable.black_king_ic : R.drawable.white_king_ic;
        binding.turnIv.setImageDrawable(
                ContextCompat.getDrawable(mContext, drawable_id)
        );
    }

    private void boardChanged(Square[][] board) {
        binding.boardView.setBoard(board);
    }

    @Override
    public void tileClicked(Point destination) {
        TilePickResult click_result = mViewModel.tileClicked(destination);
        switch (click_result) {
            case INVALID_MOVE:
                Toast.makeText(mContext, "Invalid Move ):", Toast.LENGTH_SHORT).show();
                break;
            case SOLDIER_PICK:
                Toast.makeText(mContext, destination + " Picked", Toast.LENGTH_SHORT).show();
                break;
            case WIN:
                Toast.makeText(mContext, "!~!~!~!WIN!~!~!~!", Toast.LENGTH_SHORT).show();
                break;
            default:
                mViewModel.refreshData();
                mViewModel.agentMove();

        }
    }
}