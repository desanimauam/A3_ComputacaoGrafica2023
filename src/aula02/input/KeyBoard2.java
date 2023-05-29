package aula02.input;

import aula02.cena.*;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import java.io.Closeable;

public class KeyBoard2 implements KeyListener {
    private Cena cena;
    private Iniciar iniciar;
    private final static int setaCima = 150;
    private final static int setaBaixo = 152;
    private final static int setaEsquerda = 149;
    private final static int setaDireita = 151;
    private final static int pause = 80;
    private static boolean isPaused = false;
    private final static int stop = 84;
    private final static int start = 73;

    public KeyBoard2(Iniciar iniciar){ this.iniciar = iniciar; }

    @Override
    public void keyPressed(KeyEvent e) {

        System.out.println("Key pressed: " + e.getKeyCode());

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            System.exit(0);


        if (e.getKeyCode() == KeyEvent.VK_SPACE)
            Renderer.init();
            Renderer2.Close();

        if (e.getKeyCode() == start)
            Renderer3.init();
            Renderer2.Close();
    }


    @Override
    public void keyReleased(KeyEvent e) {
    }
}
