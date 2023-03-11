import java.util.ArrayList;  
import java.util.Arrays;

public class MoveChooser {

    // Static evaluation values which correspond to their respective spaces on the board
    private static int evalTable[][] = { 
        {120, -20, 20, 5, 5, 20, -20, 120},
        {-20, -40, -5, -5, -5, -5, -40, -20},
        {20, -5, 15, 3, 3, 15, -5, 20},
        {5, -5, 3, 3, 3, 3, -5, 5},
        {5, -5, 3, 3, 3, 3, -5, 5},
        {20, -5, 15, 3, 3, 15, -5, 20},
        {-20, -40, -5, -5, -5, -5, -40, -20},
        {120, -20, 20, 5, 5, 20, -20, 120},
    };

    // Calculate the static evaluation of a given state (in maximising player's perspective)
    public static int getStaticEval(BoardState boardState){
        int currentEval = 0;
        for(int i= 0; i < 8; i++){
            for(int j= 0; j < 8; j++){
                if(boardState.getContents(i, j) == 1){
                    currentEval += evalTable[i][j];
                }
                if(boardState.getContents(i, j) == -1){
                    currentEval -= evalTable[i][j];
                }                  
            }
        }
        return currentEval;
    }
    // Minimax algorithm with alpha beta pruning
    public static int minimax(BoardState boardState, int depth, int alpha, int beta){
        ArrayList<Move> legalMoves = boardState.getLegalMoves();
        if(depth == 0 || boardState.gameOver() == true){
            return getStaticEval(boardState);
        }
        // Calculations made assuming that maximising player has to move
        if(boardState.colour == 1){
            int maxEval = Integer.MIN_VALUE; // placeholder for negative infinity
            if(legalMoves.isEmpty()){ // if there are no legal moves then just swap the turns and calculate minimax from there
                boardState.colour *= -1;
                int currentEval = minimax(boardState, depth-1, alpha, beta);
                maxEval = Math.max(maxEval, currentEval);
                alpha = Math.max(alpha, currentEval);
            }
            else{
                for(Move move: legalMoves){
                    BoardState boardStateNext = boardState.deepCopy();
                    boardStateNext.makeLegalMove(move.x, move.y);
                    int currentEval = minimax(boardStateNext, depth-1, alpha, beta);
                    maxEval = Math.max(maxEval, currentEval);
                    alpha = Math.max(alpha, currentEval);
                    if(alpha >= beta) // stop processing the current state when alpha becomes greater than or equal to beta (pruning)
                        break;
                }
            }
            return maxEval;
        }
        // Calculations made assuming that minimising player has to move
        else{
            int minEval = Integer.MAX_VALUE; // placeholder for positive infinity
            if(legalMoves.isEmpty()){ // if there are no legal moves then just swap the turns and calculate minimax from there
                boardState.colour *= -1; 
                int currentEval = minimax(boardState, depth-1, alpha, beta);
                minEval = Math.max(minEval, currentEval);
                beta = Math.max(beta, currentEval);
            }
            else{ 
                for(Move move: legalMoves){
                    BoardState boardStateNext = boardState.deepCopy();
                    boardStateNext.makeLegalMove(move.x, move.y);
                    int currentEval = minimax(boardStateNext, depth-1, alpha, beta);
                    minEval = Math.min(minEval, currentEval);
                    beta = Math.min(beta, currentEval);
                    if(alpha >= beta) // stop processing the current state when alpha becomes greater than or equal to beta (pruning)
                        break;
                }
            }
            return minEval;
        }
    }
    // Choice of move is made by comparing all minimax values of the possible moves available and selecting the best move for white
    public static Move chooseMove(BoardState boardState){
        int searchDepth= Othello.searchDepth;
        ArrayList<Move> legalMoves = boardState.getLegalMoves();
        if(legalMoves.isEmpty())
            return null;
        Move bestMove = null;
        int bestMoveValue = Integer.MIN_VALUE; // placeholder for negative infinity
        for(Move move: legalMoves){
            BoardState boardStateNext = boardState.deepCopy();
            boardStateNext.makeLegalMove(move.x, move.y);
            int moveValue = minimax(boardStateNext, searchDepth-1, Integer.MIN_VALUE, Integer.MAX_VALUE);
            if(moveValue > bestMoveValue){ // simply sift out the weakest moves, leaving with the best move on top
                bestMove = move;
                bestMoveValue = moveValue;
            }
        }
        return bestMove;
    }
}
