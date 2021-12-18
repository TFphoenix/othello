package player;

import szte.mi.Move;

public class HumanPlayer extends GeneralPlayer {

    public void init(int order) {
        // Initialize board
        initializeBoard();

        // Determine colors
        determineColors(order);
    }

    public void move(Move move) {
        if (move == null) return;

        board[move.x][move.y] = playerColor;
        updateBoard(move, playerColor);
    }

    public boolean canMove() {
        return !findPossibleMoves().isEmpty();
    }

    public void opponentMoved(Move move) {
        if (move == null) return;

        board[move.x][move.y] = opponentColor;
        updateBoard(move, opponentColor);
    }
}
