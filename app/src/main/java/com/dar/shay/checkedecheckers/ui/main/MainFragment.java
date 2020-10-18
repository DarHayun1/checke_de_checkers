package com.dar.shay.checkedecheckers.ui.main;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dar.shay.checkedecheckers.MoveListener;
import com.dar.shay.checkedecheckers.R;
import com.dar.shay.checkedecheckers.databinding.MainFragmentBinding;
import com.dar.shay.checkedecheckers.datamodels.Point;
import com.dar.shay.checkedecheckers.datamodels.Square;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    }

    private void setupObservers() {

        mViewModel.getBoard().observe(getViewLifecycleOwner(), new Observer<Square[][]>() {
            @Override
            public void onChanged(Square[][] board) {
                Log.d("Checke", "onChanged + " + board);

                if (board != null)
                 boardChanged(board);
            }
        });
    }

    private void boardChanged(Square[][] board) {
        binding.boardView.setBoard(board);
    }

    @Override
    public void move(Point origin, Point destination) {
        mViewModel.move(origin, destination);
    }
}