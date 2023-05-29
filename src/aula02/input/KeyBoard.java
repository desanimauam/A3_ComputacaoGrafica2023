package aula02.input;

import aula02.cena.Cena;
import aula02.cena.Iniciar;
import aula02.cena.Renderer;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class KeyBoard implements KeyListener {
    private Cena cena;
    private Iniciar iniciar;
    private final static int setaCima = 150;
    private final static int setaBaixo = 152;
    private final static int setaEsquerda = 149;
    private final static int setaDireita = 151;
    private final static int pause = 80;
    private static boolean isPaused = false;
    private final static int stop = 84;

    public KeyBoard(Cena cena) {
        this.cena = cena;
    }
    public KeyBoard(Iniciar iniciar){ this.iniciar = iniciar; }

    @Override
    public void keyPressed(KeyEvent e) {
        int contador = 0, contador2 = 0;

        System.out.println("Key pressed: " + e.getKeyCode());

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);

        switch (e.getKeyCode()) {
            
            case setaDireita:
                if (!isPaused) {
                    System.out.println("Direita no eixo X");
                    if (cena.getEixoX() < 1.6f) {
                        cena.setEixoX(cena.getEixoX() + 0.05f);
                        System.out.println(cena.getEixoX());
                    } else
                        cena.setEixoX(cena.getEixoX() + 0.0f);
                    cena.setSentidoBarra("direita");
                }
                break;
                
            case setaEsquerda:
                if (!isPaused) {
                    System.out.println("Esquerda no eixo X");
                    if (cena.getEixoX() > -1.6f) {
                        cena.setEixoX(cena.getEixoX() - 0.05f);
                        System.out.println(cena.getEixoX());
                    } else
                        cena.setEixoX(cena.getEixoX() + 0.0f);
                    cena.setSentidoBarra("esquerda");
                }
                break;
                
            case KeyEvent.VK_SPACE:
                System.out.println("Espaço pressionado");
                    cena.isBallMoving = true; // Iniciar o movimento da bola
                    isPaused = false;
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
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
