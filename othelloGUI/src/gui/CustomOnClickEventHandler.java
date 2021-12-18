package gui;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;

public class CustomOnClickEventHandler implements EventHandler<Event> {
    private OthelloGUIController othelloGUIController;

    CustomOnClickEventHandler(OthelloGUIController othelloGUIController) {
        this.othelloGUIController = othelloGUIController;
    }

    @Override
    public void handle(Event evt) {
        ImageView imageView = (ImageView) evt.getSource();
        othelloGUIController.onSlotClicked(imageView);
    }
}
