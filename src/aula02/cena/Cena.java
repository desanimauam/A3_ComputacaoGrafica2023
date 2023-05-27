package aula02.cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Color;
import java.awt.Font;

public class Cena implements GLEventListener {
    
    // Variáveis globais
    private float xMin, xMax, yMin, yMax, zMin, zMax;
    private GLU glu;
    private float eixoX;
    private float eixoY;
    private int lives = 5;
    private TextRenderer textRenderer;
    
    // Variáveis da bola
    private float ballPositionX = 0;
    private float ballPositionY = 0;
    private float ballVelocityX = 0.02f;
    private float ballVelocityY = 0.02f;
    private final float ballSize = 0.05f;
    public boolean isBallMoving = false;
    
    // Variáveis do placar
    private int score = 0;

    @Override
    public void init(GLAutoDrawable drawable) {
        // Dados iniciais da cena
        glu = new GLU();

        // Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -1f;
        xMax = yMax = zMax = 1f;
        
        // Texto 
        textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 32));
        
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        // Obtem o contexto OpenGL
        GL2 gl = drawable.getGL().getGL2();
        configuraDisplay(gl);
        
        // Roda a atualização se o jogador ainda possui vidas
        if (this.getLives() > 0) {
            // Atualiza a posição da bola e verifica as colisões
            update();
        } else { // encerra o jogo caso as vidas tenham acabado
            System.exit(0);
        }
        
        desenhaTexto(gl,20,aula02.cena.Renderer.screenHeight-100, Color.GREEN, "Placar: " + getScore());
        
        // Desenhar a bola
        gl.glPushMatrix();
        gl.glColor3f(1,0, 0);
        gl.glTranslatef(ballPositionX, ballPositionY, 0);
        
        // Desenha o círculo
        Circulo circulo = new Circulo(ballSize);
        circulo.draw(gl);
        gl.glPopMatrix();
        
        // Desenha o retangulo
        gl.glPushMatrix();
            gl.glTranslatef(eixoX, -1.8f, 0);
            gl.glColor3f(1, 1, 0);
            gl.glBegin(GL2.GL_QUADS);
            gl.glVertex2f(-0.2f, -0.1f);
            gl.glVertex2f(0.2f, -0.1f);
            gl.glVertex2f(0.2f, 0);
            gl.glVertex2f(-0.2f, 0);
            gl.glEnd();
        gl.glPopMatrix();
        
        // Mostra a quantidade de vidas na tela
        gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glOrtho(xMin, xMax, yMin, yMax, zMin, zMax);
            gl.glMatrixMode(GL2.GL_MODELVIEW);

            gl.glColor3f(1, 1, 0); // Cor do texto
            gl.glRasterPos2f(-1.8f, 1.8f); // Posição na tela

            desenhaTexto(gl,20,aula02.cena.Renderer.screenHeight-50, Color.YELLOW, "Vidas restantes: " + getLives());

            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glPopMatrix();
            gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();

        cordenadas(gl); // linha vertical e horizontal
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        // Obtenha o contexto gráfico OpenGL
        GL2 gl = drawable.getGL().getGL2();

        // Evite a divisão por zero
        if (height == 0) height = 1;

        // Calcula a proporção da janela (aspect ratio) da nova janela
        float aspect = (float) width / height;

        // Atualize as coordenadas do SRU (Sistema de Referência do Universo) para se adaptarem ao novo tamanho da janela
        xMin = yMin = zMin = -aspect;
        xMax = yMax = zMax = aspect;

        // Atualize o viewport para abranger a janela inteira
        gl.glViewport(0, 0, width, height);

        // Ative a matriz de projeção
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity(); // Carregue a matriz identidade

        // Projeção ortogonal
        gl.glOrtho(xMin, xMax, yMin, yMax, zMin, zMax);

        // Ative a matriz de modelagem
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();

        System.out.println("Reshape: " + width + ", " + height);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {
    }
    
    public void configuraDisplay(GL2 gl){
        gl.glClearColor(0,0,0,0);
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
    }
    
    public void cordenadas(GL2 gl){
        // Desenha a linha horizontal
        gl.glColor3f(1, 1, 1);
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(xMin, 0);
        gl.glVertex2f(xMax, 0);
        gl.glEnd();
        
        // Desenha a linha vertical
        gl.glBegin(GL2.GL_LINES);
        gl.glVertex2f(0, yMin);
        gl.glVertex2f(0, yMax);
        gl.glEnd();
    }
    
    public void update() {
        // Verifica se a bola está em movimento
        if (isBallMoving) {

            // Atualizar a posição da bola
            ballPositionX += ballVelocityX;
            ballPositionY += ballVelocityY;

            // Verifica colisões com as bordas da tela
            if (ballPositionX + ballSize > xMax || ballPositionX - ballSize < xMin) {
                // Inverte a direção da bola no eixo X
                ballVelocityX *= -1;
            }

            if (ballPositionY + ballSize > yMax || ballPositionY - ballSize < yMin) {
                // Inverte a direção da bola no eixo Y
                ballVelocityY *= -1;
            }

            // Verifica colisão com o retângulo amarelo
            if (ballPositionX - ballSize <= eixoX + 0.2f && ballPositionX + ballSize >= eixoX - 0.2f && ballPositionY - ballSize <= -1.8f) {
                // Inverte a direção da bola no eixo Y após a colisão
                ballVelocityY *= -1;
                
                //marcar pontuação
            marcarPontuacao();
            System.out.println(getScore());

                // Ajusta a posição da bola para que não extrapole o SRU
                if (ballPositionY - ballSize < yMin) {
                    ballPositionY = yMin + ballSize;
                }
            }
            else {

                // Caso a bola não toque no retângulo amarelo, retorna ao centro da tela
                if (ballPositionY - ballSize < yMin) {
                    ballPositionX = 0;
                    ballPositionY = 0;
                    isBallMoving = false; // Para o movimento da bola até que o espaço seja teclado
                    this.livesLeft();
                    System.out.println(this.getLives());
                }
            }
        }
    }
    
    private int livesLeft() {
        this.setLives((this.getLives()-1));
        
        return this.getLives();
    }
     
    public int marcarPontuacao(){
        return score += 20;
    }
    
    public int getScore(){
        return score;
    }

    
    public float getEixoX() {
        return eixoX;
    }

    public void setEixoX(float eixoX) {
        this.eixoX = eixoX;
    }

    public float getEixoY() {
        return eixoY;
    }

    public void setEixoY(float eixoY) {
        this.eixoY = eixoY;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    
    public void desenhaTexto(GL2 gl, int xPosicao, int yPosicao, Color cor, String frase){         
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        //Retorna a largura e altura da janela
        textRenderer.beginRendering(aula02.cena.Renderer.screenWidth, aula02.cena.Renderer.screenHeight);       
        textRenderer.setColor(cor);
        textRenderer.draw(frase, xPosicao, yPosicao);
        textRenderer.endRendering();
    }
}