package pong.cena;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.awt.TextRenderer;
import java.awt.Color;
import java.awt.Font;
import pong.textura.Textura;

public class Cena implements GLEventListener {
    
    // Variáveis globais
    private float xMin, xMax, yMin, yMax, zMin, zMax;
    private GLU glu;
    private int lives = 5; // Quantidade padrão de vidas
    private TextRenderer textRenderer; // Mostrar texto no SRU
    
    // Variáveis tela
    private String screen = "initial";
    
    // Variáveis de cores
    private float ballColorRed = 1.0f;
    private float ballColorGreen = 1.0f;
    private float ballColorBlue = 0.6f;
    
    // Variáveis barra
    private float axisX;
    private float axisY;
    private String direction = "neutro";
    
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
    
    // Variáveis do obstáculo
    private final float obstaclePositionXMin = 0.4f;
    private final float obstaclePositionXMax = 1.4f;
    private final float obstaclePositionYMin = 0.3f;
    private final float obstaclePositionYMax = 1f;
    
    //Referencia para classe Textura
    private Textura textura = null;
    //Quantidade de Texturas a ser carregada
    private int totalTextura = 1;
    //Constantes para identificar as imagens
    private float limite;
    public static final String FACE1 = "image/mario.png";
    private int filtro = GL2.GL_LINEAR;
    private int wrap = GL2.GL_REPEAT;
    private int modo = GL2.GL_DECAL;
    private int indice;
    

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        // Dados iniciais da cena
        glu = new GLU();

        // Estabelece as coordenadas do SRU (Sistema de Referencia do Universo)
        xMin = yMin = zMin = -1f;
        xMax = yMax = zMax = 1f;
        
        // Texto  
        textRenderer = new TextRenderer(new Font("Arial", Font.BOLD, 32));
        
        limite = 1;
        //Cria uma instancia da Classe Textura indicando a quantidade de texturas
        textura = new Textura(totalTextura);
        
        gl.glEnable(GL2.GL_LIGHTING);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        // Obtem o contexto OpenGL
        GL2 gl = drawable.getGL().getGL2();
        configuraDisplay(gl);
        
        if (getScreen()== "initial"){
            desenhaTexto(gl,660, pong.cena.Renderer.screenHeight-300, Color.MAGENTA, "BEM-VINDO!");
            desenhaTexto(gl,520, pong.cena.Renderer.screenHeight-450, Color.WHITE, "Pressine ESPAÇO para iniciar");
            desenhaTexto(gl,570, pong.cena.Renderer.screenHeight-500, Color.red, "Pressine ESC para sair");
            desenhaTexto(gl,430, pong.cena.Renderer.screenHeight-800, Color.YELLOW, "Pressine i para abrir as instruções do jogo");
        }
        
        if(getScreen()=="instruction"){
            desenhaTexto(gl,660, pong.cena.Renderer.screenHeight-100, Color.MAGENTA, "COMO JOGAR:");
            desenhaTexto(gl,100, pong.cena.Renderer.screenHeight-300, Color.white, "Utilize as setas do teclados para mover a barra para ESQUERDA ou DIREITA");
            desenhaTexto(gl,100, pong.cena.Renderer.screenHeight-350, Color.white, "Pressione o ESPAÇO para soltar a bolinha");
            desenhaTexto(gl,100, pong.cena.Renderer.screenHeight-400, Color.white, "Pressine P para pausar");
            desenhaTexto(gl,100, pong.cena.Renderer.screenHeight-450, Color.white, "Pressine T para reiniciar");
            desenhaTexto(gl,100, pong.cena.Renderer.screenHeight-500, Color.white, "Pressine ESC para sair");
        }
        
        if(getScreen()=="game"){
            iluminacaoAmbiente(gl, ballPositionX, ballPositionY);
        
            // Roda a atualização se o jogador ainda possui vidas
            if (this.getLives() > 0) {
                // Atualiza a posição da bola e verifica as colisões
                update();
            } else { // encerra o jogo caso as vidas tenham acabado
                this.stopGame();
            }

            // Mostra o placar na tela
            desenhaTexto(gl,20, pong.cena.Renderer.screenHeight-100, Color.GREEN, "Placar: " + getScore());

            // Mostra a fase na tela
            desenhaTexto(gl,20, pong.cena.Renderer.screenHeight-150, Color.WHITE, "Fase: " + getFase());

            // Mostra a quantidade de vidas na tela
            desenhaTexto(gl,20, pong.cena.Renderer.screenHeight-50, Color.YELLOW, "Vidas restantes: ");

            // Mostra as instrucoes na tela
            desenhaTexto(gl,1050, pong.cena.Renderer.screenHeight-50, Color.WHITE, "Iniciar/voltar: espaço | Pausa: p");
            desenhaTexto(gl,1050, pong.cena.Renderer.screenHeight-100, Color.WHITE, "Parar: t | Instruções: i");

            //Inclusão de representação das vidas por bolinhas
            if(getLives() > 0){
                int i;
                double cX = -1.2, cY = 1.75;
                for (i=0; i< getLives(); i++){
                    Circulo circulo = new Circulo(ballSize);
                    circulo.draw2(gl, cX, cY);
                    cX = cX + 0.1;
                    gl.glPopMatrix();
                }
            }

            // Desenhar a bola
            gl.glPushMatrix();
            gl.glColor3f(ballColorRed, ballColorGreen, ballColorBlue); // Começa amarelo-claro por padrão
            gl.glTranslatef(ballPositionX, ballPositionY, 0);

            // Desenha o círculo
            Circulo circulo = new Circulo(ballSize);
            circulo.draw(gl);
            gl.glPopMatrix();

            // Desenha o retangulo
            gl.glPushMatrix();
                gl.glTranslatef(axisX, -1.8f, 0);
                gl.glColor3f(1, 1, 0);
                gl.glBegin(GL2.GL_QUADS);
                gl.glVertex2f(-0.2f, -0.1f);
                gl.glVertex2f(0.2f, -0.1f);
                gl.glVertex2f(0.2f, 0);
                gl.glVertex2f(-0.2f, 0);
                gl.glEnd();
            gl.glPopMatrix();



            if(getFase() == 2){

                // Desenha um cubo no qual a textura eh aplicada
                //não é geração de textura automática
                textura.setAutomatica(false);

                //configura os filtros
                textura.setFiltro(filtro);
                textura.setModo(modo);
                textura.setWrap(wrap);  

                //cria a textura indicando o local da imagem e o índice
                textura.gerarTextura(gl, FACE1, 0);

                // Desenha o retangulo
                gl.glPushMatrix();
                    gl.glColor3f(1, 1, 1);
                    gl.glBegin(GL2.GL_QUADS);
                    gl.glTexCoord2f(0.0f, 0.0f);    gl.glVertex2f(obstaclePositionXMin, obstaclePositionYMin);
                    gl.glTexCoord2f(limite, 0.0f);  gl.glVertex2f(obstaclePositionXMax, obstaclePositionYMin);
                    gl.glTexCoord2f(limite, limite);    gl.glVertex2f(obstaclePositionXMax, obstaclePositionYMax);
                    gl.glTexCoord2f(0.0f, limite);  gl.glVertex2f(obstaclePositionXMin, obstaclePositionYMax);
                    gl.glEnd();
                gl.glPopMatrix();

                 //desabilita a textura indicando o índice
                textura.desabilitarTextura(gl, 0);
            }
        }
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
                
                // Altera a cor da bola para azul-claro
                ballColorRed = 0.65f;
                ballColorGreen = 0.65f;
                ballColorBlue = 1.0f;
            }

            if (ballPositionY + ballSize > yMax || ballPositionY - ballSize < yMin) {
                // Inverte a direção da bola no eixo Y
                ballVelocityY *= -1;
                
                // Altera a cor da bola para verde-claro
                ballColorRed = 0.65f;
                ballColorGreen = 1.0f;
                ballColorBlue = 0.65f;
            }

            // Verifica colisão com o retângulo amarelo
            if (ballPositionX - ballSize <= axisX + 0.2f && ballPositionX + ballSize >= axisX - 0.2f && ballPositionY - ballSize <= -1.8f) {
                
                // Verifica se o sentido do movimento da barra e com a bola
                if (direction.equals("direita") && ballVelocityX < 0) {
                //  Inverte a direção da bola no eixo X com uma pequena mudança de rota
                    ballVelocityX = -1 * ballVelocityX + (float) Math.random() * 0.003f;
                } else if (direction.equals("esquerda") && ballVelocityX > 0) {
                    ballVelocityX = -1 * ballVelocityX + (float) Math.random() * 0.001f;
                }
                
                // Inverte a direção da bola no eixo Y após a colisão com uma pequena mudança de rota
                ballVelocityY = -1 * ballVelocityY + (float) Math.random() * 0.003f;
                
                // Altera a cor da bola para rosa
                ballColorRed = 1.0f;
                ballColorGreen = 0.65f;
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

            // Verificações de colisão com o obstáculo
            // Verifica colisão com o lado esquerdo do obstáculo
            if (getFase() == 2 &&
                ballPositionX - ballSize <= obstaclePositionXMin &&
                ballPositionX + ballSize >= obstaclePositionXMin &&
                ballPositionY + ballSize >= obstaclePositionYMin &&
                ballPositionY - ballSize <= obstaclePositionYMax) {
                // Inverte a direção da bola no eixo X
                ballVelocityX *= -1;

                // Altera a cor da bola para azul-claro
                ballColorRed = 0.65f;
                ballColorGreen = 0.65f;
                ballColorBlue = 1.0f;
            }

            // Verifica colisão com o lado direito do obstáculo
            if (getFase() == 2 &&
                ballPositionX + ballSize >= obstaclePositionXMax &&
                ballPositionX - ballSize <= obstaclePositionXMax &&
                ballPositionY + ballSize >= obstaclePositionYMin &&
                ballPositionY - ballSize <= obstaclePositionYMax) {
                // Inverte a direção da bola no eixo X
                ballVelocityX *= -1;

                // Altera a cor da bola para azul-claro
                ballColorRed = 0.65f;
                ballColorGreen = 0.65f;
                ballColorBlue = 1.0f;
            }

            // Verifica colisão com a parte inferior do obstáculo
            if (getFase() == 2 &&
                ballPositionY - ballSize <= obstaclePositionYMin &&
                ballPositionY + ballSize >= obstaclePositionYMin &&
                ballPositionX + ballSize >= obstaclePositionXMin &&
                ballPositionX - ballSize <= obstaclePositionXMax) {
                // Inverte a direção da bola no eixo Y
                ballVelocityY *= -1;

                // Altera a cor da bola para verde-claro
                ballColorRed = 0.65f;
                ballColorGreen = 1.0f;
                ballColorBlue = 0.65f;
            }

            // Verifica colisão com a parte superior do obstáculo
            if (getFase() == 2 &&
                ballPositionY + ballSize >= obstaclePositionYMax &&
                ballPositionY - ballSize <= obstaclePositionYMax &&
                ballPositionX + ballSize >= obstaclePositionXMin &&
                ballPositionX - ballSize <= obstaclePositionXMax) {
                // Inverte a direção da bola no eixo Y
                ballVelocityY *= -1;

                // Altera a cor da bola para verde-claro
                ballColorRed = 0.65f;
                ballColorGreen = 1.0f;
                ballColorBlue = 0.65f;
            }

            else {

                // Caso a bola não toque no retângulo amarelo, retorna ao centro da tela
                if (ballPositionY - ballSize < yMin) {
                    ballPositionX = 0;
                    ballPositionY = 0;
                    isBallMoving = false; // Para o movimento da bola até que o espaço seja teclado
                    
                    // Retorna a cor da bola para amarelo claro
                    ballColorRed = 1.0f;
                    ballColorGreen = 1.0f;
                    ballColorBlue = 0.65f;
                    
                    // Retira vidas
                    this.livesLeft();
                    System.out.println(this.getLives()); // Printa no console a quantidade de vidas restantes
                }
            }
        }
    }
    
    public void stopGame() {
            ballPositionX = 0;
            ballPositionY = 0;
            isBallMoving = false; // Para o movimento da bola até que o espaço seja teclado
                    
            // Retorna a cor da bola para amarelo claro
            ballColorRed = 1.0f;
            ballColorGreen = 1.0f;
            ballColorBlue = 0.65f;
            
            this.setLives(5);
            this.setScore(0);
            this.setFase(1);
            this.update();
            
            // Retorna a velocidade original
            ballVelocityX = 0.02f;
            ballVelocityY = 0.02f;
    }
    
    public void iluminacaoAmbiente(GL2 gl, float ballPositionX, float ballPositionY) {
        float luzAmbiente[] = { 1.0f, 0.0f, 0.0f, 0.5f }; //cor
        float posicaoLuz[] = {ballPositionX, ballPositionY, 1.0f, 1.0f}; //pontual

        // define parametros de luz de número 0 (zero)
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, luzAmbiente, 0);
        gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, posicaoLuz, 0);

        gl.glEnable(GL2.GL_COLOR_MATERIAL);

        // habilita o uso da iluminação na cena
        gl.glEnable(GL2.GL_LIGHTING);
        // habilita a luz de número 0
        gl.glEnable(GL2.GL_LIGHT0);
        // Especifica o Modelo de tonalização a ser utilizado
        // GL_FLAT -> modelo de tonalização flat
        // GL_SMOOTH -> modelo de tonalização GOURAUD (default)
        gl.glShadeModel(GL2.GL_SMOOTH);
    }
    
    // Função para retirar as vidas do jogador
    private int livesLeft() {
        this.setLives((this.getLives()-1));
        
        return this.getLives();
    }
     
    public int marcarPontuacao(){
        return this.score += 20;
    }
    
    public void setScore(int score) {
        this.score = score;
    }
    
    public int getScore(){
        return score;
    }
    
    public void mudarLevel(){
        ballVelocityX *= 1.8f;
        ballVelocityY *= 1.8f;
        setFase(2);
    }
    
    public float getAxisX() {
        return axisX;
    }

    public void setAxisX(float axisX) {
        this.axisX = axisX;
    }

    public float getAxisY() {
        return axisX;
    }

    public void setAxisY(float axisX) {
        this.axisX = axisX;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }
    
    public void setBarDirection(String direction){
        this.direction = direction;
        System.out.println(this.direction);
    }
    
    public String getBarDirection(){
        return direction;
    }
    
    public int getFase(){
        return level;
    }

    public void setFase(int level){
        this.level = level;
    }
    
    public String getScreen(){
        return screen;
    }
    
    public void setScreen(String screen){
        this.screen = screen;
    }
    
     // Mostrar texto na tela   
    public void desenhaTexto(GL2 gl, int xPosicao, int yPosicao, Color cor, String frase){         
        gl.glPolygonMode(GL2.GL_FRONT_AND_BACK, GL2.GL_FILL);
        //Retorna a largura e altura da janela
        textRenderer.beginRendering(pong.cena.Renderer.screenWidth, pong.cena.Renderer.screenHeight);       
        textRenderer.setColor(cor);
        textRenderer.draw(frase, xPosicao, yPosicao);
        textRenderer.endRendering();
    }
}