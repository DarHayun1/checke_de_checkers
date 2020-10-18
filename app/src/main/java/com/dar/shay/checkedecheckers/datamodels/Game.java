package com.dar.shay.checkedecheckers.datamodels;

import java.util.Arrays;

enum Move{
    STEP,
    EAT,
    INVALID_MOVE
}
class Player{
    boolean is_black=true;
    boolean is_human=true;
    Player(boolean is_black,boolean is_human){
        this.is_black = is_black;
        this.is_human = is_human;
    }
}
public class Game {
    Square[][] board;
    Player black_player;
    Player white_player;

    public Square[][] getBoard() {
        return board;
    }

    @Override
    public String toString() {
        return "Game{" +
                "board=" + Arrays.toString(board) +
                ", black_player=" + black_player +
                ", white_player=" + white_player +
                '}';
    }

    public Game(){
        int BOARD_SIZE = 8;
        board = new Square[BOARD_SIZE][BOARD_SIZE];
        for (int i=0;i<BOARD_SIZE;i = i+2){
            //setting the matrix to default values
            //line 0
            board[0][i] = Square.WHITE_SQUARE;
            board[0][i+1] = Square.WHITE_SOLDIER;
            //line 1
            board[1][i] = Square.WHITE_SOLDIER;
            board[1][i+1] = Square.WHITE_SQUARE;
            //line 2
            board[2][i] = Square.WHITE_SQUARE;
            board[2][i+1] = Square.WHITE_SOLDIER;
            //line 3
            board[3][i] = Square.BLACK_SQUARE;
            board[3][i+1] = Square.WHITE_SQUARE;
            //line 4
            board[4][i] = Square.WHITE_SQUARE;
            board[4][i+1] = Square.BLACK_SQUARE;
            //line 5
            board[5][i] = Square.BLACK_SOLDIER;
            board[5][i+1] = Square.WHITE_SQUARE;
            //line 6
            board[6][i] = Square.WHITE_SQUARE;
            board[6][i+1] = Square.BLACK_SOLDIER;
            //line 7
            board[7][i] = Square.BLACK_SOLDIER;
            board[7][i+1] = Square.WHITE_SQUARE;
        }
    }
    Square getSquare(Point p){
        return board[p.x][p.y];
    }
    void removeSoldier(Point p) {
        //assume's that the square is BLACK_SOLDIER / WHITE_SOLDIER
        board[p.x][p.y] = Square.BLACK_SQUARE;
    }

    public Move move(Point source, Point destination, boolean is_black){
        Move move_status = ValidateMove(source,destination,is_black);
        if(move_status == Move.INVALID_MOVE) return Move.INVALID_MOVE;

        removeSoldier(source);
        if(is_black){
            board[destination.x][destination.y] = Square.BLACK_SOLDIER;
        }else{
            board[destination.x][destination.y] = Square.WHITE_SOLDIER;
        }
        if(move_status == Move.EAT) {
            int target_x = (source.x + destination.x)/2;
            int target_y = (source.y + destination.y)/2;
            removeSoldier(new Point(target_x,target_y));
            return Move.EAT;
        }
        return Move.STEP;
    }

    Move ValidateMove(Point source, Point destination, boolean is_black){
        int diffX = Math.abs(source.x - destination.x);
        int diffY = Math.abs(source.y - destination.y);
        if(diffX != diffY) return Move.INVALID_MOVE;
        if(is_black){
            if(board[source.x][source.y] != Square.BLACK_SOLDIER) return Move.INVALID_MOVE;
        }else{
            if(board[source.x][source.y] != Square.WHITE_SOLDIER) return Move.INVALID_MOVE;
        }
        //diffX == diffY
        if(diffX == 1 && board[destination.x][destination.y] == Square.BLACK_SQUARE) return Move.STEP;
        else if(diffX == 2){
            int target_x = (source.x + destination.x)/2;
            int target_y = (source.y + destination.y)/2;
            if(is_black && board[target_x][target_y] != Square.WHITE_SOLDIER) return Move.INVALID_MOVE;
            if(!is_black && board[target_x][target_y] != Square.BLACK_SOLDIER) return Move.INVALID_MOVE;
            return Move.EAT;
        }else return Move.INVALID_MOVE;

    }
}
/*
*  int diffX,diffY;
        //checking if s is a valid soldier and p is an empty square
        if( s == nullptr || !(this->is_empty_square(p))){
            return false;
        }
        diffY = abs(p.gety()-s->p.gety());
        diffX = abs(p.getx()-s->p.getx());
        //movement is always diagonal
        if(diffX != diffY){
            return false;
        }
        if( diffY == 1){
            return true;
        }
        if(diffY == 2){
            if(diffX == 2){
            int x = (p.getx()+s->p.getx())/2;
            int y = (p.gety()+s->p.gety())/2;
            Coordinate predicate_soldier(x,y);
            return valid_pred(cur_player,p);
            }
        }
        return false;*/