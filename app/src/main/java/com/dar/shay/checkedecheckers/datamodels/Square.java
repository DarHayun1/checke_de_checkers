package com.dar.shay.checkedecheckers.datamodels;

public enum Square{
    WHITE_SQUARE,
    BLACK_SQUARE,
    BLACK_SOLDIER,
    WHITE_SOLDIER,
    BLACK_KING,
    WHITE_KING;


    @Override
    public String toString() {
        return this.name();
    }
}
