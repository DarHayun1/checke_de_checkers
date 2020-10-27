package com.dar.shay.checkedecheckers.datamodels;

import android.util.Log;
import android.util.Pair;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

class AlphaBetaResult {
    public double minmax_value;
    public int total_number_of_nodes;
    public boolean is_optimal_value;
    Pair<Point, Point> move;

    AlphaBetaResult(double minmax_value, int total_number_of_nodes, boolean is_optimal_value, Pair<Point, Point> move) {
        this.minmax_value = minmax_value;
        this.total_number_of_nodes = total_number_of_nodes;
        this.is_optimal_value = is_optimal_value;
        this.move = move;
    }
}


public class AlphaBeta {
    private static final int DEPTH = 3;
    double weight = 0.5;

    AlphaBeta(double weight) {
        this.weight = weight;
    }

    //computes the time estimation of the next iteration
    // based on leaves of the last iteration
    // and last iteration time
    // our estimation is that each leaf on average will evolve to 3 possible states

    /**
     * computes the time estimation of the next iteration
     * based on leaves of the last iteration
     * and last iteration time
     * our estimation is that each leaf on average will evolve to 3 possible states
     *
     * @param number_of_nodes
     * @param last_iteration_time
     * @return
     */
    double nextIterationTimeEstimation(int number_of_nodes, double last_iteration_time) {
        //return 3*leaves*last_iteration_time;
        double time_per_node = last_iteration_time / number_of_nodes;
        int next_iteration_nodes_estimation = number_of_nodes * 3;
        return time_per_node * next_iteration_nodes_estimation;
        //double next_iteration_time = time_per_node * next_iteration_nodes_estimation;
        // return 3 * next_iteration_time;
    }

    AlphaBetaResult getMove(int time_limit, Game game) {

        Instant start = Instant.now();
        int depth = DEPTH;
        AlphaBetaResult moveAlphaBeta = pickAMove(game, depth);
        Log.d("pickAMove", moveAlphaBeta.toString());
        double last_iteration_time = ((double) Duration.between(start, Instant.now()).toMillis()) / 1000;
        double next_iteration = nextIterationTimeEstimation(moveAlphaBeta.total_number_of_nodes, last_iteration_time) * 2;
        boolean is_optimal = false;
        double time_until_now = ((double) Duration.between(start, Instant.now()).toMillis()) / 1000;
        Log.i("TimeCheck00", " " + time_until_now + " " + next_iteration + " " + time_limit);
        while (time_until_now + next_iteration < time_limit &&
                !is_optimal) {
            Log.i("TimeCheck", " " + time_until_now + " " + next_iteration + " " + time_limit);
            depth += 2;
            Instant iteration_start_time = Instant.now();
            moveAlphaBeta = pickAMove(game, depth);
            last_iteration_time = ((double) Duration.between(iteration_start_time, Instant.now()).toMillis()) / 1000;
            next_iteration = nextIterationTimeEstimation(moveAlphaBeta.total_number_of_nodes, last_iteration_time) * 2;
            time_until_now = ((double) Duration.between(start, Instant.now()).toMillis()) / 1000;
            is_optimal = moveAlphaBeta.is_optimal_value;
            Log.i("pickAMove", "while || " + moveAlphaBeta.move);
        }
        return moveAlphaBeta;

    }


    private AlphaBetaResult pickAMove(Game game, int depth) {
        Log.d("pickAMove", "" + depth);
        double best_minimax_val = -10;
        int best_leaves = 0;
        boolean is_optimal_val = false;
        ArrayList<Pair<Point, Point>> possible_moves = game.getOptionalMoves();
        //TODO: מחזיר נל לפעמים, מה קורה במצב של סוף משחק?
        Pair<Point, Point> best_move = possible_moves.get(0);
        for (Pair<Point, Point> move : possible_moves) {
            int target_x = (move.first.x + move.second.x) / 2;
            int target_y = (move.first.y + move.second.y) / 2;
            Square target = game.board[target_x][target_y];
            Log.w("Checke", "for move " + move.toString());
            if (game.move(move.first, move.second) == TilePickResult.INVALID_MOVE) continue;
            AlphaBetaResult move_result = rbAlphaBeta(game, depth, -10, 10);
            Log.w("Checke", "for after move()");
            setPlayerBack(game, move, target);
            if (move_result.minmax_value > best_minimax_val) {
                is_optimal_val = move_result.is_optimal_value;
                best_minimax_val = move_result.minmax_value;
                best_move = new Pair<>(move.first, move.second);
                best_leaves = move_result.total_number_of_nodes;
            }
        }
        return new AlphaBetaResult(best_minimax_val, best_leaves, is_optimal_val, best_move);

    }

    AlphaBetaResult CheckIfSomeoneIsStuck(boolean is_black_turn, boolean is_player_stuck, boolean is_rival_stuck) {
        if (is_black_turn) {
            if (is_player_stuck && !is_rival_stuck) {
                //the player can't move but the rival can.
                //player lost
                return new AlphaBetaResult(-1, 1, true, null);
            } else if (is_player_stuck) {
                return new AlphaBetaResult(0, 1, true, null);
            }
        } else {// white team turn ( rival turn )
            if (is_rival_stuck && !is_player_stuck) {
                return new AlphaBetaResult(1, 1, true, null);
            } else if (is_rival_stuck) {
                return new AlphaBetaResult(0, 1, true, null);
            }
        }
        //RbAlphaBeta dont need those values
        //RbAlphaBeta will ignore them because its not a Goal state
        //wich one of the players is stuck
        double ignore1 = 0;
        int ignore2 = 0;
        return new AlphaBetaResult(ignore1, ignore2, false, null);
    }

    AlphaBetaResult rbAlphaBeta(Game game, int depth, double alpha, double beta) {

        boolean is_player_stuck = game.isPlayerStuck(game.isBlackTurn());
        boolean is_rival_stuck = game.isPlayerStuck(!game.isBlackTurn());
        double cur_max = -10; // cur_max set to negative infinity, -10 is less then the minimum possible value
        double cur_min = 10; //starting from infinity number
        Pair<Point, Point> last_move = null;
        boolean cur_optimal = false;
        int total_nodes_num = 0;

        // check whether the board in the current state is a goal state
        AlphaBetaResult is_someone_stuck = CheckIfSomeoneIsStuck(game.isBlackTurn(), is_player_stuck, is_rival_stuck);
        if (is_someone_stuck.is_optimal_value)
            return is_someone_stuck;
        //no one is stuck at this state
        if (depth == 0) {//depth limit is reached here - return the heuristic value

            return this.heuristicFunction(game);
        }
        //here starts the calculation
        //this condition checks if this calculation is for black turn - our agent
        //or white turn - rival
        //if the condition is true : maximizing values over the possible moves
        //o.w its minimizing the values over the possible moves
        for (Pair<Point, Point> move : game.getOptionalMoves()) {
            Square origin_sq = game.board[move.first.x][move.first.y];
            int target_x = (move.first.x + move.second.x) / 2;
            int target_y = (move.first.y + move.second.y) / 2;
            Square target = game.board[target_x][target_y];
            if (game.move(move.first, move.second) == TilePickResult.INVALID_MOVE) continue;
            AlphaBetaResult alphabeta_res = rbAlphaBeta(game, depth - 1, alpha, beta);
            setPlayerBack(game, move, target);
            total_nodes_num += alphabeta_res.total_number_of_nodes;

            if (game.isBlackTurn()) { // maximizer

                cur_max = Math.max(alphabeta_res.minmax_value, cur_max);
                if (cur_max == alphabeta_res.minmax_value) {
                    cur_optimal = alphabeta_res.is_optimal_value;
                    last_move = move;
                }


                alpha = Math.max(cur_max, alpha);

                //cutting brach
                //if this condition is true the algorith cuts this branch
                if (cur_max >= beta) {
                    cur_max = 10;// a score representing the infinity number
                    // making the algorithm ignore this nodes because of alphabeta rule
                    break;
                }
            } else { //minimizer
                cur_min = Math.min(alphabeta_res.minmax_value, cur_min);
                if (cur_min == alphabeta_res.minmax_value) {
                    cur_optimal = alphabeta_res.is_optimal_value;
                }

                beta = Math.min(cur_min, beta);

                //cutting brach
                //if this condition is true the algorith cuts this branch
                if (cur_min <= alpha) {
                    cur_min = -10;// a score representing the infinity number
                    // making the algorithm ignore this nodes because of alphabeta rule
                    break;
                }
            }
        }
//            for (int i = 0; i < Game.BOARD_SIZE; i++) {
//                for (int j = 0; j < Game.BOARD_SIZE; j++) {
//                    Point origin_loc = new Point(i, j);
//                    Square origin_square = game.board[i][j];
//                    boolean is_king = origin_square == Square.BLACK_KING || origin_square == Square.WHITE_KING;
//
//                    if (is_king && game.hasAvailableKingMove(origin_loc, game.isBlackTurn()) ||
//                            !is_king && game.hasAvailableSoldierMove(origin_loc, game.isBlackTurn())) {
//                        ArrayList<Point> possible_moves = origin_loc.getOptionalPoints();
//                        for (int idx = 0; idx < possible_moves.size(); idx++) {
//                            if (game.validateMove(origin_loc, possible_moves.get(idx), game.isBlackTurn()) != TilePickResult.INVALID_MOVE) {
//                                AlphaBetaResult alphabeta_res = rbAlphaBeta(game, depth - 1, alpha, beta);
//                                setPlayerBack(game, origin_square, origin_loc);
//                                last_move = new Pair<>(origin_loc, possible_moves.get(idx));
//                                total_nodes_num += alphabeta_res.total_number_of_nodes;
//                                cur_max = Math.max(alphabeta_res.minmax_value, cur_max);
//                                if (cur_max == alphabeta_res.minmax_value) {
//                                    cur_optimal = alphabeta_res.is_optimal_value;
//                                }
//
//                                alpha = Math.max(cur_max, alpha);
//
//                                //cutting brach
//                                //if this condition is true the algorith cuts this branch
//                                if (cur_max >= beta) {
//                                    cur_max = 10;// a score representing the infinity number
//                                    // making the algorithm ignore this nodes because of alphabeta rule
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
        double minmax_val = (game.isBlackTurn()) ? cur_max : cur_min;
        return new AlphaBetaResult(minmax_val, total_nodes_num, cur_optimal, last_move);

//        } else { //this node is minimizer
//
//            for (int i = 0; i < Game.BOARD_SIZE; i++) {
//                for (int j = 0; j < Game.BOARD_SIZE; j++) {
//                    Point origin_loc = new Point(i, j);
//                    Square origin_square = game.board[i][j];
//                    if (game.hasAvailableSoldierMove(origin_loc, game.isBlackTurn())) { //this node is maximizer
//                        ArrayList<Point> possible_moves = origin_loc.getOptionalPoints();
//                        for (int idx = 0; i < possible_moves.size(); idx++) {
//                            if (game.validateMove(origin_loc, possible_moves.get(idx), game.isBlackTurn()) != TilePickResult.INVALID_MOVE) {
//                                AlphaBetaResult alphabeta_res = rbAlphaBeta(game, depth - 1, alpha, beta);
//                                setPlayerBack(game, origin_square, origin_loc);
//
//                                total_nodes_num += alphabeta_res.total_number_of_nodes;
//                                last_move = new Pair<>(origin_loc, possible_moves.get(idx));
//                                cur_min = Math.min(alphabeta_res.minmax_value, cur_min);
//                                if (cur_min == alphabeta_res.minmax_value) {
//                                    cur_optimal = alphabeta_res.is_optimal_value;
//                                }
//
//                                beta = Math.min(cur_min, beta);
//
//                                //cutting brach
//                                //if this condition is true the algorith cuts this branch
//                                if (cur_min <= alpha) {
//                                    cur_min = -10;// a score representing the infinity number
//                                    // making the algorithm ignore this nodes because of alphabeta rule
//                                    break;
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//            return new AlphaBetaResult(cur_max, total_nodes_num, cur_optimal, last_move);
//        }
    }


    //backtracking the steps for RbAlphaBeta agent
    private void setPlayerBack(Game game, Pair<Point, Point> move, Square target) {
        game.board[move.first.x][move.first.y] = game.board[move.second.x][move.second.y];
        game.board[move.second.x][move.second.y] = Square.BLACK_SQUARE;
        //If the move was eat
        if (Math.abs(Math.abs(move.first.x) - Math.abs(move.second.x)) > 1) {
            int target_x = (move.first.x + move.second.x) / 2;
            int target_y = (move.first.y + move.second.y) / 2;
            game.board[target_x][target_y] = target;
        }

        game.switchTurn();

    }


    private AlphaBetaResult heuristicFunction(Game game) {
        //calculte the heuristic function
        //based on the turn & the board
        double diff_soldiers_heuristic_value = calcDiffSoldiers(game);
        double diff_valuable_soldiers_heuristic_value = CalcDiffValSoldiers(game);
        double heuristic_value = weight * diff_soldiers_heuristic_value + (1 - weight) * diff_valuable_soldiers_heuristic_value;
        return new AlphaBetaResult(heuristic_value, 1, false, null);
    }

    private double calcDiffSoldiers(Game game) {
        int white_soldiers = 0;
        int black_soldiers = 0;
        for (int i = 0; i < Game.BOARD_SIZE; i++) {
            for (int j = 0; j < Game.BOARD_SIZE; j++) {
                if (game.board[i][j] == Square.BLACK_SOLDIER)
                    black_soldiers++;
                if (game.board[i][j] == Square.WHITE_SOLDIER)
                    white_soldiers++;
            }
        }
        int max_diff_possible = 12;
        if (game.isBlackTurn())
            return (double) (black_soldiers - white_soldiers) / max_diff_possible;
        else
            return (double) (white_soldiers - black_soldiers) / max_diff_possible;
    }

    private double CalcDiffValSoldiers(Game game) {
        int black_valuables = 0;
        int white_valuables = 0;
        for (int i = 0; i < Game.BOARD_SIZE; i++) {
            for (int j = 0; j < Game.BOARD_SIZE; j++) {
                if (game.board[i][j] == Square.BLACK_KING)
                    black_valuables++;
                if (game.board[i][j] == Square.WHITE_KING)
                    white_valuables++;
                //note that the next if is trying to findout
                //if the soldier on the left/right side of the board
                //then it has valuable qualities
                if (j == 0 || j == 7 && game.board[i][j] == Square.BLACK_SOLDIER)
                    black_valuables++;
                if (j == 0 || j == 7 && game.board[i][j] == Square.WHITE_SOLDIER)
                    white_valuables++;
            }
        }
        int max_diff_possible = 12;
        if (game.isBlackTurn())
            return (double) (black_valuables - white_valuables) / max_diff_possible;
        else
            return (double) (white_valuables - black_valuables) / max_diff_possible;

    }
}