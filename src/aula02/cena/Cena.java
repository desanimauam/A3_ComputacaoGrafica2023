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
    private int lives = 5; // Quantidade padrão de vidas
    private TextRenderer textRenderer; // Mostrar texto no SRU
    
    // Variáveis de cores
    private float ballColorRed = 1.0f;
    private float ballColorGreen = 0.0f;
    private float ballColorBlue = 0.0f;
    
    // Variáveis barra
    private float eixoX;
    private float eixoY;
    private String sentido = "neutro";
    
    // Variáveis da bola
    private float ballPositionX = 0;
    private float ballPositionY = 0;
    private float ballVelocityX = 0.02f;
    private float ballVelocityY = 0.02f;
    private final float ballSize = 0.05f;
    public boolean isBallMoving = false;
    
    // Variável do placar
    private int score = 0;
    private int level = 1;

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
        
        // Mostra o placar na tela
        desenhaTexto(gl,20,aula02.cena.Renderer.screenHeight-100, Color.GREEN, "Placar: " + getScore());
        
        // Mostra a fase na tela
        desenhaTexto(gl,20,aula02.cena.Renderer.screenHeight-150, Color.WHITE, "Fase: " + getFase());
        
        // Desenhar a bola
        gl.glPushMatrix();
        gl.glColor3f(ballColorRed, ballColorGreen, ballColorBlue); // Começa vermelho por padrão
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
        
        gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glPushMatrix();
            gl.glLoadIdentity();
            gl.glOrtho(xMin, xMax, yMin, yMax, zMin, zMax);
            gl.glMatrixMode(GL2.GL_MODELVIEW);

            gl.glColor3f(1, 1, 0); // Cor do texto
            gl.glRasterPos2f(-1.8f, 1.8f); // Posição na tela
            gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glPopMatrix();
            gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glPopMatrix();

        // Mostra a quantidade de vidas na tela
        desenhaTexto(gl,20,aula02.cena.Renderer.screenHeight-50, Color.YELLOW, "Vidas restantes: " + getLives());

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

        // Atualiza as coordenadas do SRU (Sistema de Referência do Universo) para se adaptarem ao novo tamanho da janela
        xMin = yMin = zMin = -aspect;
        xMax = yMax = zMax = aspect;

        // Atualiza o viewport para abranger a janela inteira
        gl.glViewport(0, 0, width, height);

        // Ativa a matriz de projeção
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity(); // Carregue a matriz identidade

        // Projeção ortogonal
        gl.glOrtho(xMin, xMax, yMin, yMax, zMin, zMax);

        // Ativa a matriz de modelagem
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
                
                // Altera a cor da bola para azul
                ballColorRed = 0.0f;
                ballColorGreen = 0.0f;
                ballColorBlue = 1.0f;
            }

            if (ballPositionY + ballSize > yMax || ballPositionY - ballSize < yMin) {
                // Inverte a direção da bola no eixo Y
                ballVelocityY *= -1;
                
                // Altera a cor da bola para verde
                ballColorRed = 0.0f;
                ballColorGreen = 1.0f;
                ballColorBlue = 0.0f;
            }

            // Verifica colisão com o retângulo amarelo
            if (ballPositionX - ballSize <= eixoX + 0.2f && ballPositionX + ballSize >= eixoX - 0.2f && ballPositionY - ballSize <= -1.8f) {
                //marcar pontuação
                marcarPontuacao();
                System.out.println(getScore());
                
                if (sentido.equals("direita") && ballVelocityX < 0) {
                    ballVelocityX *= -1;
                } else if (sentido.equals("esquerda") && ballVelocityX > 0) {
                    ballVelocityX *= -1;
                }
                
                // Inverte a direção da bola no eixo Y após a colisão
                ballVelocityY *= -1;
                
                // Altera a cor da bola para rosa
                ballColorRed = 1.0f;
                ballColorGreen = 0.0f;
                ballColorBlue = 1.0f;
                
                //marcar pontuação
                marcarPontuacao();
                System.out.println(getScore());
                //mudar de nível = aumentar velocidade
                if(getScore() == 200) {
                    mudarLevel();
                }

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
                    
                    // Retorna a cor da bola para vermelho
                    ballColorRed = 1.0f;
                    ballColorGreen = 0.0f;
                    ballColorBlue = 0.0f;
                    
                    // Retira vidas
                    this.livesLeft();
                    System.out.println(this.getLives()); // Printa no console a quantidade de vidas restantes
                }
            }
        }
    }
    
    // Função para retirar as vidas do jogador
    private int livesLeft() {
        this.setLives((this.getLives()-1));
        
        return this.getLives();
    }
     
    public int marcarPontuacao(){
        return this.score += 20;
    }
    
    public int getScore(){
        return score;
    }
    
    public void mudarLevel(){
        ballVelocityX *= 1.5f;
        ballVelocityY *= 1.5f;
        setFase(2);
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
    
    public void setSentidoBarra(String sentido){
        this.sentido = sentido;
        System.out.println(this.sentido);
    }
    
    public String getSentidoBarra(){
        return sentido;
    }
    
    public int getFase(){
        return level;
    }

    // Mostrar texto na tela
    public void setFase(int level){
        this.level = level;
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