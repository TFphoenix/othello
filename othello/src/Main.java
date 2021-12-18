import szte.mi.Move;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        MyPlayerRndChs p;
        Random rnd = new Random();

        MyPlayerRndChs p1 = new MyPlayerRndChs();
        MyPlayerRndChs p2 = new MyPlayerRndChs();
        p1.init(0, 0, rnd);
        p2.init(1, 0, rnd);

        // P1 - Black ; P2 - White
        Move m1 = p1.nextMove(null, 0, 0);
        Move m2 = p2.nextMove(m1, 0, 0);
        Move m3 = p1.nextMove(m2, 0, 0);
        Move m4 = p2.nextMove(m3, 0, 0);
        Move m5 = p1.nextMove(m4, 0, 0);
        Move m6 = p2.nextMove(m5, 0, 0);
        Move m7 = p1.nextMove(m6, 0, 0);
        Move m8 = p2.nextMove(m7, 0, 0);
        Move m9 = p1.nextMove(m8, 0, 0);

        // MyPlayer - Black ; Me - White
//        p1.nextMove(null, 0, 0);
//        p1.nextMove(new Move(4, 2), 0, 0);
//        p1.nextMove(new Move(2, 4), 0, 0);
//        p1.nextMove(new Move(3, 2), 0, 0);
//        p1.nextMove(new Move(1, 1), 0, 0);
    }
}
