import szte.mi.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MyPlayerRndChs implements szte.mi.Player {
    private enum SlotType {
        Empty,
        White,
        Black
    }

    private SlotType[][] board = new SlotType[8][8];
    private SlotType playerColor;
    private SlotType opponentColor;
    private int turn;
    private int order;
    private long t;
    private Random rnd;

    private static final Move[] DIRECTIONS = new Move[]{
            new Move(1, 0),
            new Move(1, 1),
            new Move(0, 1),
            new Move(-1, 1),
            new Move(-1, 0),
            new Move(-1, -1),
            new Move(0, -1),
            new Move(1, -1),
    };

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
        if (order == 0) {
            playerColor = SlotType.Black;
            opponentColor = SlotType.White;
        } else {
            playerColor = SlotType.White;
            opponentColor = SlotType.Black;
        }
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
        List<Move> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Move move = new Move(i, j);
                if (board[i][j] == SlotType.Empty && isPossibleMove(move)) {
                    possibleMoves.add(move);
                }
            }
        }

        // No possible move
        if (possibleMoves.isEmpty()) return null;

        /// Find best move TODO: Improve
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

    // Initializes Board
    private void initializeBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = SlotType.Empty;
            }
        }
        board[3][3] = SlotType.White;
        board[4][4] = SlotType.White;
        board[3][4] = SlotType.Black;
        board[4][3] = SlotType.Black;
    }

    // Returns true or false if the given move is a possible move or not
    private boolean isPossibleMove(Move move) {
        for (Move direction : DIRECTIONS) {
            if (findInDirection(move, direction, playerColor)) {
                return true;
            }
        }

        return false;
    }

    // Is corner move
    private boolean isCorner(Move move) {
        return ((move.x == 0 && move.y == 0) || (move.x == 7 && move.y == 7) || (move.x == 7 && move.y == 0) || (move.x == 0 && move.y == 7));
    }

    // Is edge move
    private boolean isEdge(Move move) {
        return ((move.x >= 2 && move.x <= 5) || (move.y >= 2 && move.y <= 5));
    }

    // Is center move
    private boolean isCenter(Move move) {
        return (move.x < 6 && move.y < 6 && move.x >= 2 && move.y >= 2);
    }

    // Returns true if it finds opponentColor in the given direction
    private boolean findInDirection(Move start, Move direction, SlotType target) {
        Move current = new Move(start.x + direction.x, start.y + direction.y);
        boolean lookingForReverted = true;

        while (current.x < 8 && current.y < 8 && current.x >= 0 && current.y >= 0) {
            SlotType currentSlot = board[current.x][current.y];

            if (lookingForReverted) {
                if (currentSlot == target || currentSlot == SlotType.Empty) return false;
                lookingForReverted = false;
            } else {
                if (currentSlot == SlotType.Empty) return false;
                if (currentSlot == target) return true;
            }

            current = new Move(current.x + direction.x, current.y + direction.y);
        }

        return false;
    }

    // Updates board after opponent's move
    private void updateBoard(Move move, SlotType flipperColor) {
        for (Move direction : DIRECTIONS) {
            if (findInDirection(move, direction, flipperColor)) {
                flipInDirection(move, direction, flipperColor);
            }
        }
    }

    // Flips slots in given direction
    private void flipInDirection(Move start, Move direction, SlotType flipperColor) {
        Move current = new Move(start.x + direction.x, start.y + direction.y);

        while (current.x < 8 && current.y < 8 && current.x >= 0 && current.y >= 0) {
            SlotType currentSlot = board[current.x][current.y];

            if (currentSlot == flipperColor) return;
            board[current.x][current.y] = flipperColor;

            current = new Move(current.x + direction.x, current.y + direction.y);
        }
    }

    // Prints board
    private void printBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                switch (board[i][j]) {
                    case Empty:
                        System.out.print("_ ");
                        break;
                    case White:
                        System.out.print("W ");
                        break;
                    case Black:
                        System.out.print("B ");
                        break;
                }
            }
            System.out.print("\n");
        }
    }
}
