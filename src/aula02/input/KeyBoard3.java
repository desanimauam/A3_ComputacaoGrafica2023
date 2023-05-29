package aula02.input;

import aula02.cena.*;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;

import java.io.Closeable;

public class KeyBoard3 implements KeyListener {
    private final Instrucao instrucao;

    public KeyBoard3(Instrucao instrucao) {
        this.instrucao = instrucao;
    }

    @Override
    public void keyPressed(KeyEvent e) {

        System.out.println("Key pressed: " + e.getKeyCode());

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
            Renderer2.init();
            Renderer3.Close();

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}