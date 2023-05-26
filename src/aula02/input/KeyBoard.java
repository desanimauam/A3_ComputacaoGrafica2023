package aula02.input;

import aula02.cena.Cena;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

public class KeyBoard implements KeyListener {
    private Cena cena;
    private final static int setaCima = 150;
    private final static int setaBaixo = 152;
    private final static int setaEsquerda = 149;
    private final static int setaDireita = 151;

    public KeyBoard(Cena cena) {
        this.cena = cena;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println("Key pressed: " + e.getKeyCode());
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);

        switch (e.getKeyCode()) {
            case setaDireita:
                System.out.println("Direita no eixo X");
                if (cena.getEixoX() < 1.6f) {
                    cena.setEixoX(cena.getEixoX() + 0.05f);
                    System.out.println(cena.getEixoX());
                } else
                    cena.setEixoX(cena.getEixoX() + 0.0f);
                break;
            case setaEsquerda:
                System.out.println("Esquerda no eixo X");
                if (cena.getEixoX() > -1.6f) {
                    cena.setEixoX(cena.getEixoX() - 0.05f);
                    System.out.println(cena.getEixoX());
                } else
                    cena.setEixoX(cena.getEixoX() + 0.0f);
                break;
            case setaCima:
                System.out.println("Subindo no eixo Y");
                cena.setEixoY(cena.getEixoY() + 0.05f);
                break;
            case setaBaixo:
                System.out.println("Descendo no eixo Y");
                cena.setEixoY(cena.getEixoY() - 0.05f);
                break;
                
                case KeyEvent.VK_SPACE:
            System.out.println("Espaço pressionado");
            cena.isBallMoving = true; // Iniciar o movimento da bola
            break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
