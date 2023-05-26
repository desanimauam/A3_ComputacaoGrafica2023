package aula02.cena;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;
import aula02.input.KeyBoard;
import java.awt.Dimension;
import java.awt.Toolkit;

public class Renderer {
    private static GLWindow window = null;
    public static int screenWidth = 0;  //1280
    public static int screenHeight = 0; //960 

    // Cria a janela de renderização do JOGL
    public static void init() {
        GLProfile.initSingleton();
        GLProfile profile = GLProfile.get(GLProfile.GL2);
        GLCapabilities caps = new GLCapabilities(profile);
        window = GLWindow.create(caps);
        
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension d = tk.getScreenSize();

        // Configura a janela para tela cheia
//        window.setFullscreen(true);

        screenWidth = d.width;
        screenHeight = d.height;

        window.setSize(screenWidth, screenHeight);

        Cena cena = new Cena();

        window.addGLEventListener(cena); // adiciona a Cena à Janela  
        // Habilita o teclado: cena
        window.addKeyListener(new KeyBoard(cena));

        FPSAnimator animator = new FPSAnimator(window, 60);
        animator.start(); // inicia o loop de animação

        // Encerra a aplicação adequadamente
        window.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDestroyNotify(WindowEvent e) {
                animator.stop();
                System.exit(0);
            }
        });

        window.setVisible(true);
    }

    public static void main(String[] args) {
        init();
    }
}
