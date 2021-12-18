package player;

import szte.mi.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyPlayerRndChs extends GeneralPlayer implements szte.mi.Player {
    private int turn;
    private int order;
    private long t;
    private Random rnd;

    /**
     * Performs initialization depending on the parameters.
     *
     * @param order Defines the order of the players. Value 0 means
     *              this is the first player to move, 1 means second and so on.
     *              For two-player games only values 0 and 1 are possible.
     * @param t     Gives the remaining overall running time of the player in
     *              ms. Initialization is also counted as running time.
     * @param rnd   source of randomness to be used wherever random
     *              numbers are needed
     */
    @Override
    public void init(int order, long t, Random rnd) {
        // Initialize parameters
        this.order = order;
        this.t = t;
        this.rnd = rnd;
        this.turn = -1;

        // Initialize board
        initializeBoard();

        // Determine colors
        determineColors(order);
    }

    /**
     * Calculates the next move of the player in a two player game.
     * It is assumed that the player is stateful and the game is
     * deterministic, so the parameters only
     * give the previous move of the other player and remaining times.
     *
     * @param prevMove  the previous move of the opponent. It can be null,
     *                  which means the opponent has not moved (or this is the first move).
     * @param tOpponent remaining time of the opponent
     * @param t         remaining time for this player
     */
    @Override
    public Move nextMove(Move prevMove, long tOpponent, long t) {
        // Increment turn counter
        turn++;

        //// First move
        if (prevMove == null && turn == 0) {
            Move playerMove = new Move(2, 3);
            board[playerMove.x][playerMove.y] = playerColor;
            updateBoard(playerMove, playerColor);
            return playerMove;
        }

        //// Other moves
        if (prevMove != null) {
            // TODO: Remove
            System.out.println("[MyPlayer] Opponent move: " + prevMove.x + " " + prevMove.y);

            // Register opponent's move
            board[prevMove.x][prevMove.y] = opponentColor;

            // Flip slots accordingly
            updateBoard(prevMove, opponentColor);
        }

        // Find possible moves
        List<Move> possibleMoves = findPossibleMoves();

        // No possible move
        if (possibleMoves.isEmpty()) return null;

        /// Find best move
        Move playerMove = null;

        // Corner
        if (playerMove == null) {
            for (Move move : possibleMoves) {
                if (isCorner(move)) {
                    playerMove = move;
                }
            }
        }
        // Edge
        if (playerMove == null) {
            for (Move move : possibleMoves) {
                if (isEdge(move)) {
                    playerMove = move;
                }
            }
        }
        // Center
        if (playerMove == null) {
            for (Move move : possibleMoves) {
                if (isCenter(move)) {
                    playerMove = move;
                }
            }
        }
        // Random
        if (playerMove == null) {
            int min = 0;
            int max = possibleMoves.size() - 1;
            int rndMove = min + rnd.nextInt(max - min + 1);
            playerMove = possibleMoves.get(rndMove);
        }

        // Move
        board[playerMove.x][playerMove.y] = playerColor;
        updateBoard(playerMove, playerColor);

        // Return move
        return playerMove;
    }

    /// Getters
    public int getTurn() {
        return turn;
    }
}
