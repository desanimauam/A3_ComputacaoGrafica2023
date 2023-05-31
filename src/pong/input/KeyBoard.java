package pong.input;

import pong.cena.Cena;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class KeyBoard implements KeyListener {
    private Cena cena;
    private final static int left = 149;
    private final static int right = 151;
    private final static int pause = 80;
    private static boolean isPaused = false;
    private final static int stop = 84;
    private final static int instruction = 73;

    public KeyBoard(Cena cena) {
        this.cena = cena;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        System.out.println("Key pressed: " + e.getKeyCode());

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);

        switch (e.getKeyCode()) {
            
            case right:
                if (!isPaused) {
                    System.out.println("Direita no eixo X");
                    if (cena.getAxisX() < 1.6f) {
                        cena.setAxisX(cena.getAxisX() + 0.05f);
                        System.out.println(cena.getAxisX());
                    } else
                        cena.setAxisX(cena.getAxisX() + 0.0f);
                    cena.setBarDirection("direita");
                }
                break;
                
            case left:
                if (!isPaused) {
                    System.out.println("Esquerda no eixo X");
                    if (cena.getAxisX() > -1.6f) {
                        cena.setAxisX(cena.getAxisX() - 0.05f);
                        System.out.println(cena.getAxisX());
                    } else
                        cena.setAxisX(cena.getAxisX() + 0.0f);
                    cena.setBarDirection("esquerda");
                }
                break;
                
            case KeyEvent.VK_SPACE:
                System.out.println("Espaço pressionado");
                if(cena.getScreen() == "initial" || cena.getScreen() == "instruction"){
                    cena.setScreen("game");
                }
                else {
                    cena.isBallMoving = true; // Iniciar o movimento da bola
                    isPaused = false;
                }
                    break;

            case pause:
                System.out.println("Pausado");
                cena.isBallMoving = false; // Para a bola
                isPaused = true;
                break;
                
            case stop:
                System.out.println("Jogo encerrado pelo usuario");
                cena.stopGame();
                break;
                
            case instruction:
                System.out.println("Instruções");
                cena.setScreen("instruction");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
