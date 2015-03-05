package nl.uva.td.visual;


public class TDGameWrapper extends Thread {

    @Override
    public void run() {
        Game2048.show();
    }
}
