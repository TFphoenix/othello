package player;

import szte.mi.Move;

import java.util.ArrayList;
import java.util.List;

public abstract class GeneralPlayer {
    protected SlotType[][] board = new SlotType[8][8];
    protected SlotType playerColor;
    protected SlotType opponentColor;

    protected static final Move[] DIRECTIONS = new Move[]{
            new Move(1, 0),
            new Move(1, 1),
            new Move(0, 1),
            new Move(-1, 1),
            new Move(-1, 0),
            new Move(-1, -1),
            new Move(0, -1),
            new Move(1, -1),
    };

    /// Getters
    public SlotType[][] getBoard() {
        return board;
    }

    public SlotType getPlayerColor() {
        return playerColor;
    }

    public SlotType getOpponentColor() {
        return opponentColor;
    }

    // Initializes Board
    protected void initializeBoard() {
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

    // Determines colors
    protected void determineColors(int order) {
        if (order == 0) {
            playerColor = SlotType.Black;
            opponentColor = SlotType.White;
        } else {
            playerColor = SlotType.White;
            opponentColor = SlotType.Black;
        }
    }

    public List<Move> findPossibleMoves() {
        List<Move> possibleMoves = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                Move move = new Move(i, j);
                if (isPossibleMove(move)) {
                    possibleMoves.add(move);
                }
            }
        }

        return possibleMoves;
    }

    // Returns true or false if the given move is a possible move or not
    public boolean isPossibleMove(Move move) {
        if (board[move.x][move.y] != SlotType.Empty) return false;

        for (Move direction : DIRECTIONS) {
            if (findInDirection(move, direction, playerColor)) {
                return true;
            }
        }

        return false;
    }

    // Is corner move
    protected boolean isCorner(Move move) {
        return ((move.x == 0 && move.y == 0) || (move.x == 7 && move.y == 7) || (move.x == 7 && move.y == 0) || (move.x == 0 && move.y == 7));
    }

    // Is edge move
    protected boolean isEdge(Move move) {
        return ((move.x >= 2 && move.x <= 5) || (move.y >= 2 && move.y <= 5));
    }

    // Is center move
    protected boolean isCenter(Move move) {
        return (move.x < 6 && move.y < 6 && move.x >= 2 && move.y >= 2);
    }

    // Returns true if it finds opponentColor in the given direction
    protected boolean findInDirection(Move start, Move direction, SlotType target) {
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
    protected void updateBoard(Move move, SlotType flipperColor) {
        for (Move direction : DIRECTIONS) {
            if (findInDirection(move, direction, flipperColor)) {
                flipInDirection(move, direction, flipperColor);
            }
        }
    }

    // Flips slots in given direction
    protected void flipInDirection(Move start, Move direction, SlotType flipperColor) {
        Move current = new Move(start.x + direction.x, start.y + direction.y);

        while (current.x < 8 && current.y < 8 && current.x >= 0 && current.y >= 0) {
            SlotType currentSlot = board[current.x][current.y];

            if (currentSlot == flipperColor) return;
            board[current.x][current.y] = flipperColor;

            current = new Move(current.x + direction.x, current.y + direction.y);
        }
    }

    // Prints board
    protected void printBoard() {
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
