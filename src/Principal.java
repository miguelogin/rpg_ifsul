import java.awt.Color;
import java.awt.Toolkit;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;

/* miguelogin */

public class Principal extends javax.swing.JFrame {

    ImageIcon hunter = new ImageIcon("src/resources/hunter.png");
    ImageIcon paladino = new ImageIcon("src/resources/paladino.png");
    ImageIcon mago = new ImageIcon("src/resources/mago.png");
    ImageIcon dragao = new ImageIcon("src/resources/dragao.gif");
    ImageIcon ogro = new ImageIcon("src/resources/ogro.png");
    ImageIcon bruxo = new ImageIcon("src/resources/bruxo.gif");
    
    String classe, vida_txt, boss_name, vida_boss_txt, hora, vez_de, special_desc, special_boss, classe_save;
    int opcao = 0, vida, vida_atual, ataque, special, boss_life, ataque_boss, vida_atual_boss, ataque_atual, ataque_atual_boss, special_running = 0, 
            count_special = 0, specals_used=0, save_game=0, contador_clicks=0, level=0;
    static int auto_save=1, auto_music=1, bug=0;
    boolean boss_death, player_death;
    
    Calendar c = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

    
    public Principal() {
        hora = sdf.format(c.getTime());
        setIcon();
        initComponents();
    }
    
    public void atributos_combate() {
       
        vez_de = "player";
        switch (classe) {
            case "cacadora":
                vida = 1800;
                ataque = 80;
                special = 1;
                player.setIcon(hunter);
                special_desc = "Stun";
                break;
            case "paladino":
                vida = 2200;
                ataque = 100;
                special = 2;
                player.setIcon(paladino);
                special_desc = "Crític";
                break;
            case "mago":
                vida = 1000;
                ataque = 50;
                special = 3;
                player.setIcon(mago);
                special_desc = "Heal";
                break;
        }
        
        switch (boss_name) {
            case "Dragao":
                boss_life = 400;
                ataque_boss = 50;
                boss_icon.setIcon(dragao);
                break;
            case "Ogro":
                boss_life = 300;
                ataque_boss = 50;
                boss_icon.setIcon(ogro);
                break;
            case "Bruxo":
                boss_life = 1000;
                ataque_boss = 150;
                boss_icon.setIcon(bruxo);
                break;
        }
        
        life.setStringPainted(true);
        life.setMinimum(0);
        life.setMaximum(vida);
        life.setForeground(Color.black);
        life.setValue(vida);
        vida_atual = vida;
        vida_txt = (vida_atual + (" / "+vida));
        life.setString(vida_txt);
        
        boss_bar.setStringPainted(true);
        boss_bar.setMinimum(0);
        boss_bar.setMaximum(boss_life);
        boss_bar.setForeground(Color.black);
        boss_bar.setValue(boss_life);
        vida_atual_boss = boss_life;
        vida_boss_txt = (vida_atual_boss + (" / "+boss_life));
        boss_bar.setString(vida_boss_txt);

        //ESCREVA OS ATRIBUTOS DE COMABTE
        dano_text_player.setText("Dano de Ataque (Máx) = " +ataque);
        special_text_player.setText("Special = " +special_desc);
        
        dano_text_boss.setText("Dano de Ataque (Máx) = "+ataque_boss);
    }

    public void special(){
        if (special == 1)                   //STUN DA CAÇADORA   
        {
            hora = sdf.format(c.getTime());
            relatorio_de_combate.append(hora+" | Você stunou o "+boss_name+" por um round\n");
            special_running = 1;
            combate();
        }else if(special == 2){             //CRÍTICO DO PALADINO
            special_running =  2;
            combate();
        }else if(special == 3){             //CURA DO MAGO
            vida_atual = vida_atual + 400;
            
            if(vida_atual > vida)
                vida_atual = vida;
            hora = sdf.format(c.getTime());
            relatorio_de_combate.append(hora+" | Você curou 400 pontos de vida\n");
            vez_de = "boss";
            special_running = 3;
            combate();
        }
        specals_used = specals_used + 1;
    }

    public void combate() {
        
        ataque_atual = (int) (Math.random()*(ataque));
        ataque_atual_boss = (int) (Math.random()*(ataque_boss));
            
        
        //REALIZA O SPECIAL DO PALADINO
        if (special_running == 2){
            ataque_atual = ataque * 2;
            special_running = 0;
        }
            

            
        //FECHA A REALIZAÇÃO DE CURA DO MAGO
        if (special_running == 3){
            special_running = 0;
        }
            
        //REALIZA A JOGADA DO PLAYER
        if (vez_de.equals("player")){
            vida_atual_boss = vida_atual_boss - ataque_atual;           //ATACA O BOSS
            vida_boss_txt = (vida_atual_boss + (" / "+boss_life));
            boss_bar.setString(vida_boss_txt);                          //MUDA A VIDA DO BOSS
            
            hora = sdf.format(c.getTime());
            relatorio_de_combate.append(hora+" | Você causou " + ataque_atual+ " de dano ao "+boss_name+"\n");
                //atacar.setEnabled(false);
                //special_button.setEnabled(false);
            checa_death();
            vez_de = "boss";
                
            // STUN DO CAÇADOR
            if (special_running == 1){
                vez_de = "player";
                special_running = 0;
            }
        }

        //REALIZA A JOGADA DO BOSS
        if (vez_de.equals("boss")){
            vida_atual = vida_atual - ataque_atual_boss; //ATACA O PLAYER
            vida_txt = (vida_atual + (" / "+vida));     
            life.setString(vida_txt);                   //MUDA A VIDA DO PLAYER
            
            hora = sdf.format(c.getTime());
            relatorio_de_combate.append(hora+" | "+boss_name+" causou " + ataque_atual_boss+ " de dano em você \n");
            checa_death();
            vez_de = "player";
        }  
        
        special_button.setEnabled(false);
        //A CADA 5 RODADAS OUTRO SPECIAL É CONCEDIDO
        //O SEGUINTE IF CONTA O NÚMERO DE VEZES JOGADAS PARA PODER ATIVAR O BOTÃO SPECIAL
        total_de_specials.setText("SPECIALS = 0");
        if (count_special > 0){
            special_button.setEnabled(true);
            total_de_specials.setText("SPECIALS = "+(count_special));
        }
        if(count_special < 1){
            special_button.setEnabled(false);
            
        }
        
        life.setValue(vida_atual);
        boss_bar.setValue(vida_atual_boss);
    }
    
    public void checa_death(){
        if ((boss_name.equals("Dragao") || boss_name.equals("Ogro")))
        {
            if (vida_atual_boss <= 0)
            {
                boss_death = true;
                combate.setVisible(false);
                if(save_game == 1){
                    count_special = 0;
                    contador_clicks = 0;
                    specals_used = 0;
                    dialogo_2.setLocationRelativeTo(null);
                    level = 4;
                    salva_jogo();
                    dialogo_2.setVisible(true);
                }

            }
            else if (vida_atual <= 0)
            {
                player_death = true;
                combate.setVisible(false);
                count_special = 0;
                contador_clicks = 0;
                specals_used = 0;
                game_over.setLocationRelativeTo(null);
                game_over.setVisible(true);

            }
        }
        else
        {
            if (vida_atual_boss <= 0)
            {
                boss_death = true;
                combate.setVisible(false);
                if(save_game == 1){
                    count_special = 0;
                    contador_clicks = 0;
                    specals_used = 0;
                    fiim_2.setLocationRelativeTo(null);
                    level = 6;
                    salva_jogo();
                    fiim_2.setVisible(true);
                }

            }
            else if (vida_atual <= 0)
            {
                player_death = true;
                combate.setVisible(false);
                count_special = 0;
                contador_clicks = 0;
                specals_used = 0;
                game_over.setLocationRelativeTo(null);
                game_over.setVisible(true);

            }
        }
                

    }
    
    ////////////////////////AQUI!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public void carrega_jogo (){
        try{
            BufferedReader leitor_buffer = new BufferedReader (new FileReader ("src/save.txt"));
            while(leitor_buffer.ready()){
                String linha = leitor_buffer.readLine(); // lê até a última linha
                auto_save = Integer.parseInt(linha);
            }
            leitor_buffer.close();
        }catch(Exception ex){}
        try{
            BufferedReader leitor_buffer = new BufferedReader (new FileReader ("src/classe.txt"));
            while(leitor_buffer.ready()){
                classe = leitor_buffer.readLine(); // lê até a última linha
            }
            leitor_buffer.close();
        }catch(Exception ex){}
        System.out.println(auto_save);
            if (auto_save == 1){                                                    //SAVE 1 COM A CLASSE DE CAÇADOR
                this.setVisible(false);
                dialogo_1.setLocationRelativeTo(null);
                dialogo_1.setVisible(true);
            }else if (auto_save == 2){                                              //SAVE 2 COM A CLASSE DE PALADINO
                this.setVisible(false);
                dialogo_1.setLocationRelativeTo(null);
                dialogo_1.setVisible(true);
            }else if (auto_save == 3){                                              //SAVE 3 COM A CLASSE DE PALADINO
                this.setVisible(false);
                dialogo_1.setLocationRelativeTo(null);
                dialogo_1.setVisible(true);
            }else if (auto_save == 4){
                count_special = 0;
                contador_clicks = 0;
                specals_used = 0;
                this.setVisible(false);
                dialogo_2.setLocationRelativeTo(null);
                dialogo_2.setVisible(true);
            }else if (auto_save == 5){
                this.setVisible(false);
                dialogo_3.setLocationRelativeTo(null);
                dialogo_3.setVisible(true);
            }else if (auto_save == 6){
                count_special = 0;
                contador_clicks = 0;
                specals_used = 0;
                fiim_2.setLocationRelativeTo(null);
                fiim_2.setVisible(true);
            }
    }
    
    public void salva_jogo (){
        //ESCREVE NO ARQUIVO
        
        try{
            BufferedReader leitor_buffer = new BufferedReader (new FileReader ("src/config_save.txt"));
            while(leitor_buffer.ready()){
                String linha = leitor_buffer.readLine(); // lê até a última linha
                auto_save = Integer.parseInt(linha);
            }
            leitor_buffer.close();
        }catch(Exception ex){}
        if (auto_save == 1){
            if (bug == 1){
                System.out.println("bug");
                bug = 0;
            }else{
                try{
                    FileWriter arquivo = new FileWriter(new File("src/save.txt"), false ); //FALSE SOBESCREVE O ARQUIVO
                    PrintWriter escrever = new PrintWriter(arquivo);
                    escrever.write(Integer.toString(level));
                    arquivo.close();
                }catch(Exception ex){}
                
                auto_save = 0;
            }
        }else{
            auto_save = 1;
        }
    }
    
    public static void toca_musica() {
        try {
            File file = new File("src/zelda.wav");
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(file));
            clip.start();
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {}
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        dialogo_1 = new javax.swing.JFrame();
        jLabel4 = new javax.swing.JLabel();
        secreta = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        escalar = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        dialogo_1_2 = new javax.swing.JFrame();
        jLabel7 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JSeparator();
        jButton7 = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        dialogo_1_1 = new javax.swing.JFrame();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jSeparator4 = new javax.swing.JSeparator();
        jButton8 = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        combate = new javax.swing.JFrame();
        player = new javax.swing.JLabel();
        life = new javax.swing.JProgressBar();
        atacar = new javax.swing.JButton();
        special_button = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        boss_icon = new javax.swing.JLabel();
        boss_bar = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        relatorio_de_combate = new javax.swing.JTextArea();
        dano_text_player = new javax.swing.JLabel();
        special_text_player = new javax.swing.JLabel();
        dano_text_boss = new javax.swing.JLabel();
        total_de_specials = new javax.swing.JLabel();
        game_over = new javax.swing.JFrame();
        jLabel8 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        dialogo_2 = new javax.swing.JFrame();
        jLabel18 = new javax.swing.JLabel();
        secreta1 = new javax.swing.JButton();
        jLabel19 = new javax.swing.JLabel();
        jSeparator6 = new javax.swing.JSeparator();
        escalar1 = new javax.swing.JButton();
        jLabel14 = new javax.swing.JLabel();
        dialogo_3 = new javax.swing.JFrame();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jSeparator7 = new javax.swing.JSeparator();
        escalar2 = new javax.swing.JButton();
        jLabel22 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        fim_1 = new javax.swing.JFrame();
        secreta3 = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jSeparator8 = new javax.swing.JSeparator();
        jLabel27 = new javax.swing.JLabel();
        fiim_2 = new javax.swing.JFrame();
        jLabel24 = new javax.swing.JLabel();
        secreta4 = new javax.swing.JButton();
        jLabel26 = new javax.swing.JLabel();
        jSeparator9 = new javax.swing.JSeparator();
        escalar3 = new javax.swing.JButton();
        jLabel30 = new javax.swing.JLabel();
        frim_2_2 = new javax.swing.JFrame();
        jLabel31 = new javax.swing.JLabel();
        secreta5 = new javax.swing.JButton();
        jLabel32 = new javax.swing.JLabel();
        jSeparator10 = new javax.swing.JSeparator();
        jLabel33 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLabel15 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();

        dialogo_1.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        dialogo_1.setResizable(false);
        dialogo_1.setSize(new java.awt.Dimension(836, 580));

        jLabel4.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel4.setText("artefato, são de ele está na Montanha do Norte. Você está agora, a frente da Montanha do Norte, o que você deseja fazer?");

        secreta.setText("PROCURAR PASSAGEM SECRETA");
        secreta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secretaActionPerformed(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel6.setText("O rei lhe convocou para recuperar um atefato, há muito tempo perdido, por seus ancentrais. As últimas notícias do paradeiro do");

        escalar.setText("ESCALAR");
        escalar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escalarActionPerformed(evt);
            }
        });

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/montanha.png"))); // NOI18N

        javax.swing.GroupLayout dialogo_1Layout = new javax.swing.GroupLayout(dialogo_1.getContentPane());
        dialogo_1.getContentPane().setLayout(dialogo_1Layout);
        dialogo_1Layout.setHorizontalGroup(
            dialogo_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogo_1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogo_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogo_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSeparator1)
                        .addComponent(jLabel6)
                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogo_1Layout.createSequentialGroup()
                            .addComponent(escalar, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(secreta, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        dialogo_1Layout.setVerticalGroup(
            dialogo_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogo_1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addGap(16, 16, 16)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogo_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(secreta, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(escalar, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(51, Short.MAX_VALUE))
        );

        dialogo_1_2.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        dialogo_1_2.setResizable(false);
        dialogo_1_2.setSize(new java.awt.Dimension(820, 600));

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel7.setText("não gosta de teleféricos...");

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel9.setText("Você encontrou um teleférico que leva ao topo da montanha. Mas durante a viagem você econtra um DRAGÃO, parece que ele");

        jButton7.setText("LUTAR!");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/telef.png"))); // NOI18N

        javax.swing.GroupLayout dialogo_1_2Layout = new javax.swing.GroupLayout(dialogo_1_2.getContentPane());
        dialogo_1_2.getContentPane().setLayout(dialogo_1_2Layout);
        dialogo_1_2Layout.setHorizontalGroup(
            dialogo_1_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogo_1_2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogo_1_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogo_1_2Layout.createSequentialGroup()
                        .addGroup(dialogo_1_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(dialogo_1_2Layout.createSequentialGroup()
                        .addGroup(dialogo_1_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSeparator2)
                            .addGroup(dialogo_1_2Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel5))
                            .addComponent(jButton7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        dialogo_1_2Layout.setVerticalGroup(
            dialogo_1_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogo_1_2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(18, 18, 18)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        dialogo_1_1.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        dialogo_1_1.setResizable(false);
        dialogo_1_1.setSize(new java.awt.Dimension(820, 600));

        jLabel10.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel10.setText("um OGRO, parece que ele não está para conversa.");

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setText("Você encontrou uma porta no topo da montanha. Ela estava destrancada. Ao entrar você encontrou ");

        jButton8.setText("LUTAR!");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jLabel13.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/porta.png"))); // NOI18N

        javax.swing.GroupLayout dialogo_1_1Layout = new javax.swing.GroupLayout(dialogo_1_1.getContentPane());
        dialogo_1_1.getContentPane().setLayout(dialogo_1_1Layout);
        dialogo_1_1Layout.setHorizontalGroup(
            dialogo_1_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogo_1_1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogo_1_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13)
                    .addGroup(dialogo_1_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSeparator4)
                        .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        dialogo_1_1Layout.setVerticalGroup(
            dialogo_1_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogo_1_1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jSeparator4, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        combate.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        combate.setResizable(false);
        combate.setSize(new java.awt.Dimension(689, 510));

        player.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/mago.png"))); // NOI18N

        atacar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/attack_icon.png"))); // NOI18N
        atacar.setText("ATACAR");
        atacar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                atacarActionPerformed(evt);
            }
        });

        special_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/special_attack_icon.png"))); // NOI18N
        special_button.setText("SPECIAL");
        special_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                special_buttonActionPerformed(evt);
            }
        });

        boss_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ogro.png"))); // NOI18N

        relatorio_de_combate.setEditable(false);
        relatorio_de_combate.setColumns(20);
        relatorio_de_combate.setRows(5);
        jScrollPane1.setViewportView(relatorio_de_combate);

        dano_text_player.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dano_text_player.setText("Dano de Ataque (Máx) = 00");

        special_text_player.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        special_text_player.setText("Special = +100 de cura");

        dano_text_boss.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dano_text_boss.setText("Dano de Ataque (Máx) = 000");

        total_de_specials.setText("SPECIALS = 0");

        javax.swing.GroupLayout combateLayout = new javax.swing.GroupLayout(combate.getContentPane());
        combate.getContentPane().setLayout(combateLayout);
        combateLayout.setHorizontalGroup(
            combateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(combateLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(combateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(combateLayout.createSequentialGroup()
                        .addComponent(life, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(boss_bar, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(combateLayout.createSequentialGroup()
                        .addComponent(player)
                        .addGroup(combateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(combateLayout.createSequentialGroup()
                                .addGap(251, 251, 251)
                                .addComponent(jLabel2)
                                .addGap(36, 36, 36))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, combateLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(combateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, combateLayout.createSequentialGroup()
                                        .addGroup(combateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(atacar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(special_button))
                                        .addGap(86, 86, 86))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, combateLayout.createSequentialGroup()
                                        .addComponent(total_de_specials)
                                        .addGap(108, 108, 108)))))
                        .addComponent(boss_icon)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(combateLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(combateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dano_text_player, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(special_text_player, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(dano_text_boss)
                .addGap(34, 34, 34))
            .addGroup(combateLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        combateLayout.setVerticalGroup(
            combateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(combateLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(combateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(life, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(boss_bar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(6, 6, 6)
                .addGroup(combateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(player)
                    .addComponent(boss_icon)
                    .addGroup(combateLayout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel2)
                        .addGap(90, 90, 90)
                        .addComponent(atacar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(special_button)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(total_de_specials, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(combateLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(dano_text_player)
                    .addComponent(dano_text_boss))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(special_text_player)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        game_over.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        game_over.setMinimumSize(new java.awt.Dimension(536, 370));
        game_over.setResizable(false);
        game_over.setSize(new java.awt.Dimension(536, 370));
        game_over.getContentPane().setLayout(null);
        game_over.getContentPane().add(jLabel8);
        jLabel8.setBounds(444, 463, 0, 0);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tumblr_static_gameover.png"))); // NOI18N
        game_over.getContentPane().add(jLabel1);
        jLabel1.setBounds(0, 10, 1800, 250);

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel12.setText("GOSTARIA DE RENICIAR A AVENTURA?");
        game_over.getContentPane().add(jLabel12);
        jLabel12.setBounds(150, 250, 240, 17);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/x_x.gif"))); // NOI18N
        jButton3.setText("NÃO");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        game_over.getContentPane().add(jButton3);
        jButton3.setBounds(280, 280, 130, 50);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/ok_ok.gif"))); // NOI18N
        jButton4.setText("SIM");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        game_over.getContentPane().add(jButton4);
        jButton4.setBounds(130, 280, 130, 50);

        dialogo_2.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        dialogo_2.setResizable(false);
        dialogo_2.setSize(new java.awt.Dimension(820, 600));

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel18.setText("a montanha começa a tremer. O que você faz?");

        secreta1.setText("LER AS INCRIÇÕES");
        secreta1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secreta1ActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel19.setText("Após derrotar a criatura você econtra um objeto estranho, com incrições marcadas nele. Ao pega-lô o ");

        escalar1.setText("SAIR DA CAVERNA PELO CAMINHO QUE VIM");
        escalar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escalar1ActionPerformed(evt);
            }
        });

        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/artefact.png"))); // NOI18N

        javax.swing.GroupLayout dialogo_2Layout = new javax.swing.GroupLayout(dialogo_2.getContentPane());
        dialogo_2.getContentPane().setLayout(dialogo_2Layout);
        dialogo_2Layout.setHorizontalGroup(
            dialogo_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogo_2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogo_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogo_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jSeparator6)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogo_2Layout.createSequentialGroup()
                            .addComponent(escalar1, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(secreta1, javax.swing.GroupLayout.PREFERRED_SIZE, 397, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel14))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogo_2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(dialogo_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel19)
                    .addComponent(jLabel18))
                .addContainerGap())
        );
        dialogo_2Layout.setVerticalGroup(
            dialogo_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogo_2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel14)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addComponent(jSeparator6, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dialogo_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(secreta1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(escalar1, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        dialogo_3.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        dialogo_3.setResizable(false);
        dialogo_3.setSize(new java.awt.Dimension(824, 645));

        jLabel20.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel20.setText("funcionava como um 'lâmpada' para um gênio, mas que só poderia ser manuseada por sangue do seu ");

        jLabel21.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel21.setText("O artefato na verdade era uma prisão. Onde o bruxo mais poderoso havia sido aprisionado. O artefato");

        escalar2.setText("DEFENDER O MUNDO DO PODEROSO BRUXO");
        escalar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escalar2ActionPerformed(evt);
            }
        });

        jLabel22.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel22.setText("sangue. O bruxo agora liberto de suas amarras irá escravizar o resto da humanidade.");

        jLabel17.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/evil_mage.png"))); // NOI18N

        javax.swing.GroupLayout dialogo_3Layout = new javax.swing.GroupLayout(dialogo_3.getContentPane());
        dialogo_3.getContentPane().setLayout(dialogo_3Layout);
        dialogo_3Layout.setHorizontalGroup(
            dialogo_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogo_3Layout.createSequentialGroup()
                .addContainerGap(36, Short.MAX_VALUE)
                .addGroup(dialogo_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22)
                    .addComponent(jLabel21)
                    .addComponent(jLabel20)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 809, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dialogo_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator7, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(escalar2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 805, Short.MAX_VALUE))))
        );
        dialogo_3Layout.setVerticalGroup(
            dialogo_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogo_3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 7, Short.MAX_VALUE)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22)
                .addGap(39, 39, 39)
                .addComponent(jSeparator7, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(escalar2, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        fim_1.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        fim_1.setResizable(false);
        fim_1.setSize(new java.awt.Dimension(820, 600));

        secreta3.setText("FIM");
        secreta3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secreta3ActionPerformed(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel25.setText("Você consegue sair da montanha. Devolve o artefato para o rei e recebe sua recompensa em dinheiro.");

        jLabel27.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/gold.png"))); // NOI18N

        javax.swing.GroupLayout fim_1Layout = new javax.swing.GroupLayout(fim_1.getContentPane());
        fim_1.getContentPane().setLayout(fim_1Layout);
        fim_1Layout.setHorizontalGroup(
            fim_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fim_1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(fim_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel25, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator8, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(secreta3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(17, 17, 17))
            .addGroup(fim_1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        fim_1Layout.setVerticalGroup(
            fim_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fim_1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator8, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(secreta3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        fiim_2.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        fiim_2.setResizable(false);
        fiim_2.setSize(new java.awt.Dimension(820, 600));

        jLabel24.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel24.setText("algum. O que você fará com o artefato?");

        secreta4.setText("O DEIXAR PERDIDO. COMO DEVERIA TER CONTINUADO");
        secreta4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secreta4ActionPerformed(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel26.setText("Você derrotou o poderoso bruxo. Agora aprisionado novamente em sua 'jaula' ele não causará mal ");

        escalar3.setText("DEVOLVER PARA O REI COMO ORDENADO");
        escalar3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                escalar3ActionPerformed(evt);
            }
        });

        jLabel30.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/artefact.png"))); // NOI18N

        javax.swing.GroupLayout fiim_2Layout = new javax.swing.GroupLayout(fiim_2.getContentPane());
        fiim_2.getContentPane().setLayout(fiim_2Layout);
        fiim_2Layout.setHorizontalGroup(
            fiim_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fiim_2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(fiim_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(fiim_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jLabel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fiim_2Layout.createSequentialGroup()
                            .addComponent(escalar3, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(secreta4, javax.swing.GroupLayout.PREFERRED_SIZE, 370, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(39, 39, 39))
                        .addComponent(jSeparator9))
                    .addComponent(jLabel30)
                    .addComponent(jLabel26))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        fiim_2Layout.setVerticalGroup(
            fiim_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fiim_2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator9, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(fiim_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(escalar3, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(secreta4, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        frim_2_2.setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        frim_2_2.setResizable(false);
        frim_2_2.setSize(new java.awt.Dimension(826, 610));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel31.setText("nunca ouvirá.");

        secreta5.setText("FIM");
        secreta5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                secreta5ActionPerformed(evt);
            }
        });

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setText("Um belo por do sol o recebe na saída da montanha. Uma recompensa para o herói do qual o mundo ");

        jLabel33.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/sunset.png"))); // NOI18N

        javax.swing.GroupLayout frim_2_2Layout = new javax.swing.GroupLayout(frim_2_2.getContentPane());
        frim_2_2.getContentPane().setLayout(frim_2_2Layout);
        frim_2_2Layout.setHorizontalGroup(
            frim_2_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frim_2_2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(frim_2_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel32)
                    .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 816, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel33)
                    .addGroup(frim_2_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jSeparator10, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(secreta5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE))))
        );
        frim_2_2Layout.setVerticalGroup(
            frim_2_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, frim_2_2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel33)
                .addGap(18, 18, 18)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel31)
                .addGap(18, 18, 18)
                .addComponent(jSeparator10, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(secreta5, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("RPG");
        setResizable(false);
        setSize(new java.awt.Dimension(569, 490));
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jButton6.setText("Carregar jogo");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton6, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 280, 140, -1));

        jRadioButton1.setBackground(new java.awt.Color(255, 255, 255));
        jRadioButton1.setText("Salvamento automático");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jRadioButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 160, -1));

        jLabel15.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel15.setText("Caçadora");
        getContentPane().add(jLabel15, new org.netbeans.lib.awtextra.AbsoluteConstraints(67, 324, 62, -1));

        jLabel40.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel40.setText("Special: Heal");
        getContentPane().add(jLabel40, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 408, -1, -1));

        jLabel41.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel41.setText("Ataque: 100");
        getContentPane().add(jLabel41, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 359, 68, -1));

        jLabel42.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel42.setText("Ataque: 80");
        getContentPane().add(jLabel42, new org.netbeans.lib.awtextra.AbsoluteConstraints(67, 359, 62, -1));

        jLabel43.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel43.setText("Vida: 1800");
        getContentPane().add(jLabel43, new org.netbeans.lib.awtextra.AbsoluteConstraints(67, 384, 62, -1));

        jLabel44.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel44.setText("Vida: 2200");
        getContentPane().add(jLabel44, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 384, 68, -1));

        jLabel45.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel45.setText("Special: Stun");
        getContentPane().add(jLabel45, new org.netbeans.lib.awtextra.AbsoluteConstraints(67, 407, -1, -1));

        jLabel46.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel46.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel46.setText("Mago");
        getContentPane().add(jLabel46, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 324, 61, -1));

        jLabel47.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel47.setText("Special: Crítico");
        getContentPane().add(jLabel47, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 409, -1, -1));

        jLabel48.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel48.setText("Ataque: 50");
        getContentPane().add(jLabel48, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 356, 70, -1));

        jLabel49.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel49.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel49.setText("Paladino");
        getContentPane().add(jLabel49, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 324, 68, -1));

        jLabel50.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel50.setText("Vida: 1000");
        getContentPane().add(jLabel50, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 381, 61, -1));

        jLabel16.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/RPG.png"))); // NOI18N
        getContentPane().add(jLabel16, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, -1, -1));

        jButton1.setText("Selecionar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 450, 179, 60));

        jButton2.setText("Selecionar");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 450, 179, 60));

        jButton5.setText("Selecionar");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton5, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 450, 179, 60));

        jLabel28.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/color2.png"))); // NOI18N
        getContentPane().add(jLabel28, new org.netbeans.lib.awtextra.AbsoluteConstraints(-1440, -680, 1610, 700));

        jLabel29.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/castle.png"))); // NOI18N
        jLabel29.setText("jLabel29");
        getContentPane().add(jLabel29, new org.netbeans.lib.awtextra.AbsoluteConstraints(-110, -90, 770, 400));

        jLabel34.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/color.png"))); // NOI18N
        getContentPane().add(jLabel34, new org.netbeans.lib.awtextra.AbsoluteConstraints(-70, 440, -1, -1));

        setSize(new java.awt.Dimension(585, 544));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //DEFINI CAÇADORA
        classe = "cacadora";
        this.setVisible(false);
        save_game = 1;
        dialogo_1.setLocationRelativeTo(null);
        level = 1;
        salva_jogo();
        dialogo_1.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        //DEFINE PALADINO
        classe = "paladino";
        this.setVisible(false);
        save_game = 1;
        dialogo_1.setLocationRelativeTo(null);
        level = 1;
        salva_jogo();
        dialogo_1.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        //DEFINE MAGO
        classe = "mago";
        this.setVisible(false);
        save_game = 1;
        dialogo_1.setLocationRelativeTo(null);
        level = 1;
        salva_jogo();
        dialogo_1.setVisible(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void escalarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escalarActionPerformed
        //ESCALA A MONTANHA
        opcao = 1;
        dialogo_1.setVisible(false);
        special_button.setEnabled(false);
        dialogo_1_1.setLocationRelativeTo(null);
        level = 2;
        salva_jogo();
        dialogo_1_1.setVisible(true);
    }//GEN-LAST:event_escalarActionPerformed

    private void secretaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secretaActionPerformed
        //PASSAGEM SECRETA PARA A MONTANHA
        opcao = 2;
        dialogo_1.setVisible(false);
        special_button.setEnabled(false);
        dialogo_1_2.setLocationRelativeTo(null);
        level = 3;
        salva_jogo();
        dialogo_1_2.setVisible(true);
    }//GEN-LAST:event_secretaActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        //ATACAR FROM DRAGÃO
        combate.setLocationRelativeTo(null);
        combate.setVisible(true);
        dialogo_1_2.setVisible(false);
        boss_name = "Dragao";
        atributos_combate();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        //ATACAR FROM OGRO
        combate.setLocationRelativeTo(null);
        combate.setVisible(true);
        dialogo_1_1.setVisible(false);
        boss_name = "Ogro";
        atributos_combate();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void atacarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_atacarActionPerformed
        //ATACAR !!!!!!
        contador_clicks = contador_clicks + 1;
        if(contador_clicks%5==0){
            count_special = (contador_clicks/5) - specals_used;
        }
        combate();
    }//GEN-LAST:event_atacarActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        count_special = 0;
        contador_clicks = 0;
        specals_used = 0;
        game_over.setVisible(false);
        this.setVisible(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void special_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_special_buttonActionPerformed
        count_special = count_special - 1;
        special();
    }//GEN-LAST:event_special_buttonActionPerformed

    private void secreta1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secreta1ActionPerformed
        dialogo_2.setVisible(false);
        dialogo_3.setLocationRelativeTo(null);
        level = 5;
        salva_jogo();
        dialogo_3.setVisible(true);
    }//GEN-LAST:event_secreta1ActionPerformed

    private void escalar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escalar1ActionPerformed
        dialogo_2.setVisible(false);
        fim_1.setLocationRelativeTo(null);
        fim_1.setVisible(true);
    }//GEN-LAST:event_escalar1ActionPerformed

    private void escalar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escalar2ActionPerformed
        //PODEROSO BRUXO
        combate.setLocationRelativeTo(null);
        combate.setVisible(true);
        dialogo_3.setVisible(false);
        boss_name = "Bruxo";
        atributos_combate();
    }//GEN-LAST:event_escalar2ActionPerformed

    private void secreta3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secreta3ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_secreta3ActionPerformed

    private void secreta4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secreta4ActionPerformed
        fiim_2.setVisible(false);
        frim_2_2.setLocationRelativeTo(null);
        frim_2_2.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_secreta4ActionPerformed

    private void escalar3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_escalar3ActionPerformed
        fiim_2.setVisible(false);
        fim_1.setLocationRelativeTo(null);
        fim_1.setVisible(true);// TODO add your handling code here:
    }//GEN-LAST:event_escalar3ActionPerformed

    private void secreta5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_secreta5ActionPerformed
        System.exit(0);        // TODO add your handling code here:
    }//GEN-LAST:event_secreta5ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        //AUTO SAVE BUTTON
        try{
            BufferedReader leitor_buffer = new BufferedReader (new FileReader ("src/config_save.txt"));
            while(leitor_buffer.ready()){
                String linha = leitor_buffer.readLine(); // lê até a última linha
                auto_save = Integer.parseInt(linha);
            }
            leitor_buffer.close();
        }catch(Exception ex){}
        if (auto_save == 1){
            if (bug == 1){
                System.out.println("bug");
                bug = 0;
            }else{
                auto_save = 0;
                System.out.println("|0");
            }
            auto_save = 0;
        }else{
            auto_save = 1;
            System.out.println("-1");
        }
        try{
            FileWriter arquivo = new FileWriter(new File("src/config_save.txt"), false ); //FALSE SOBESCREVE O ARQUIVO
            PrintWriter escrever = new PrintWriter(arquivo);
            escrever.write(Integer.toString(auto_save));
            arquivo.close();
        }catch(Exception ex){}
        System.out.println("final do arquivo > "+auto_save);
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        carrega_jogo();
    }//GEN-LAST:event_jButton6ActionPerformed

    /**
     * @param args the command line arguments
     */
    
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
                int auto_save=0;
                //SALVAMENTO AUTO
                try{
                    BufferedReader leitor_buffer = new BufferedReader (new FileReader ("src/config_save.txt"));
                    while(leitor_buffer.ready()){
                        String linha = leitor_buffer.readLine(); // lê até a última linha
                        auto_save = Integer.parseInt(linha);
                    }
                    leitor_buffer.close();
                }catch(Exception ex){}
                if (auto_save == 1){
                    jRadioButton1.setSelected(true);
                    bug = 1;
                }
                else
                {
                    jRadioButton1.setSelected(false);

                }
                
                //AUDIO 
                toca_musica();
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton atacar;
    private javax.swing.JProgressBar boss_bar;
    private javax.swing.JLabel boss_icon;
    private javax.swing.JFrame combate;
    private javax.swing.JLabel dano_text_boss;
    private javax.swing.JLabel dano_text_player;
    private javax.swing.JFrame dialogo_1;
    private javax.swing.JFrame dialogo_1_1;
    private javax.swing.JFrame dialogo_1_2;
    private javax.swing.JFrame dialogo_2;
    private javax.swing.JFrame dialogo_3;
    private javax.swing.JButton escalar;
    private javax.swing.JButton escalar1;
    private javax.swing.JButton escalar2;
    private javax.swing.JButton escalar3;
    private javax.swing.JFrame fiim_2;
    private javax.swing.JFrame fim_1;
    private javax.swing.JFrame frim_2_2;
    private javax.swing.JFrame game_over;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    public static javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JProgressBar life;
    private javax.swing.JLabel player;
    private javax.swing.JTextArea relatorio_de_combate;
    private javax.swing.JButton secreta;
    private javax.swing.JButton secreta1;
    private javax.swing.JButton secreta3;
    private javax.swing.JButton secreta4;
    private javax.swing.JButton secreta5;
    private javax.swing.JButton special_button;
    private javax.swing.JLabel special_text_player;
    private javax.swing.JLabel total_de_specials;
    // End of variables declaration//GEN-END:variables
   
    private void setIcon() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("resources/icon.png")));
    }  
}


