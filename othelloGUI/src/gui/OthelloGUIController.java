package gui;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.image.ImageView;
import player.HumanPlayer;
import player.MyPlayerRndChs;
import player.SlotType;
import szte.mi.Move;

import java.net.URL;
import java.util.*;

public class OthelloGUIController implements Initializable {

    private static class GameResults {
        enum WinnerType {
            UNKNOWN,
            HUMAN,
            NPC,
            DRAW
        }

        int humanScore = 0;
        int npcScore = 0;
        WinnerType winner = WinnerType.UNKNOWN;
    }

    // GUI members
    public GridPane gridPane;
    private Map<SlotType, Image> slotImages;
    private Image possibleSlotImage;
    private ImageView[][] slots;

    // Logic members
    private MyPlayerRndChs npcPlayer;
    private HumanPlayer humanPlayer;
    private Random rnd;
    private boolean gameFinished;

    public OthelloGUIController() {
        this.rnd = new Random();
        this.npcPlayer = new MyPlayerRndChs();
        this.humanPlayer = new HumanPlayer();
        this.slots = new ImageView[8][8];

        this.slotImages = new HashMap<>();
        this.slotImages.put(SlotType.Empty, new Image("/img/slot_empty.png"));
        this.slotImages.put(SlotType.Black, new Image("/img/slot_black.png"));
        this.slotImages.put(SlotType.White, new Image("/img/slot_white.png"));
        this.possibleSlotImage = new Image("/img/slot_possible.png");
    }

    public void startGame() {
        // initialize logic TODO: Starting player functionality
        humanPlayer.init(0);
        npcPlayer.init(1, 0, rnd);

        // initialize GUI
        initializeBoardGUI();
    }

    public void onSlotClicked(ImageView imageView) {
        // TODO: Remove
        System.out.println(imageView.getId());

        /// Logic
        Move humanPlayerMove = getMoveFromId(imageView.getId());

        // Illegal move
        if (!humanPlayer.isPossibleMove(humanPlayerMove)) {
            System.out.println("Illegal move");// TODO: Find a better way to display the error
            return;
        }

        // Move
        humanPlayerMoves(humanPlayerMove);

        /// GUI
        syncBoardGUI();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize slots
        String slotBase = "#slot_";
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                String slotName = slotBase + i + j;
                ImageView slot = (ImageView) gridPane.lookup(slotName);
                slot.addEventHandler(MouseEvent.MOUSE_CLICKED, new CustomOnClickEventHandler(this));
                slots[i][j] = slot;
            }
        }

        // Start game
        startGame();
    }

    private void humanPlayerMoves(Move move) {
        humanPlayer.move(move);
        Move npcPlayerMove = npcPlayer.nextMove(move, 0, 0);
        humanPlayer.opponentMoved(npcPlayerMove);

        // Check if human player can move
        if (!humanPlayer.canMove()) {
            // If neither player can move => end game
            if (npcPlayerMove == null) {
                endGame();
                return;
            }

            humanPlayerMoves(null);
        }
    }

    private void endGame() {
        // Generate End Game Results
        GameResults results = generateGameResults();

        // End Game Message
        String alertContent = "RESULT: ";
        if (results.winner == GameResults.WinnerType.DRAW) alertContent = "It's a draw\n";
        else alertContent = "Player " + results.winner.toString() + " won the game\n";
        alertContent += "SCORES: Human(" + results.humanScore + "), Npc(" + results.npcScore + ")";

        // End Game Alert Box
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game Ended");
        alert.setContentText(alertContent);
        alert.show();

        // Restart game
        startGame();
    }

    private GameResults generateGameResults() {
        GameResults results = new GameResults();

        var board = humanPlayer.getBoard();
        var humanColor = humanPlayer.getPlayerColor();
        var npcColor = humanPlayer.getOpponentColor();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (board[i][j] == humanColor) results.humanScore++;
                else if (board[i][j] == npcColor) results.npcScore++;
            }
        }

        if (results.humanScore > results.npcScore) results.winner = GameResults.WinnerType.HUMAN;
        else if (results.humanScore < results.npcScore) results.winner = GameResults.WinnerType.NPC;
        else results.winner = GameResults.WinnerType.DRAW;

        return results;
    }

    private void initializeBoardGUI() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                slots[i][j].setImage(slotImages.get(SlotType.Empty));
            }
        }

        slots[3][3].setImage(slotImages.get(SlotType.White));
        slots[4][4].setImage(slotImages.get(SlotType.White));
        slots[3][4].setImage(slotImages.get(SlotType.Black));
        slots[4][3].setImage(slotImages.get(SlotType.Black));

        highlightPossibleMoves();
    }

    private void highlightPossibleMoves() {
        List<Move> possibleMoves = humanPlayer.findPossibleMoves();

        for (Move move : possibleMoves) {
            slots[move.x][move.y].setImage(possibleSlotImage);
        }
    }

    private void syncBoardGUI() {
        var board = humanPlayer.getBoard();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                slots[i][j].setImage(slotImages.get(board[i][j]));
            }
        }

        highlightPossibleMoves();
    }

    private Move getMoveFromId(String id) {
        int underscoreIndex = id.indexOf('_');
        int x = id.charAt(underscoreIndex + 1) - '0';
        int y = id.charAt(underscoreIndex + 2) - '0';
        return new Move(x, y);
    }
}
