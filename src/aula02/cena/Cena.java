package aula02.cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.gl2.GLUT;

public class Cena implements GLEventListener {
    
    // Variáveis globais
    private float xMin, xMax, yMin, yMax, zMin, zMax;
    private GLU glu;
    private float eixoX;
    private float eixoY;
    private int lives = 5;
    
    // Variáveis da bola
    private float ballPositionX = 0;
    private float ballPositionY = 0;
    private float ballVelocityX = 0.02f;
    private float ballVelocityY = 0.02f;
    private final float ballSize = 0.05f;
    private GLUT glut;
    public boolean isBallMoving = false;

    @Override
    public void init(GLAutoDrawable drawable) {
        // Dados iniciais da cena
        glu = new GLU();
        glut = new GLUT();

        // Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -1f;
        xMax = yMax = zMax = 1f;
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

            String vidasText = "Vidas restantes: " + getLives(); // Exibe a quantidade de vidas
            for (char c : vidasText.toCharArray()) {
                glut.glutBitmapCharacter(GLUT.BITMAP_HELVETICA_18, c);
            }

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
    
}