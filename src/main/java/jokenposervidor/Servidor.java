package jokenposervidor;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {

    public static void main(String[] args) {

        ServerSocket servidor = null;
        List<Player> jogadores = new ArrayList<Player>();

        try {
            servidor = new ServerSocket(8081);
            System.out.println("servidor startado na porta 8081");
            while (true) {
                System.out.println("Aguardando conexão...");
                Socket clienteSocket = servidor.accept();
                Player player = new Player(clienteSocket);
                jogadores.add(player);
                setPlayerGameMode(jogadores);
                setPlayersName(jogadores, "Digite seu nome");

                if (jogadores.get(0).modoJogo.equals("1")) {
                    if (jogadores.size() == 2) {
                        if (jogadores.get(0).nomeJogador != null && jogadores.get(1).nomeJogador != null) {                 
                            jogarContraPlayer(jogadores);   
                            while (jogadores.get(0).jogarNovamente && jogadores.get(0).jogarNovamente) {
                                jogarContraPlayer(jogadores);
                            }
                        }               
                    }
                } else {
                    jogarContraCpu(jogadores);
                    while (jogadores.get(0).jogarNovamente) {
                        jogarContraCpu(jogadores);
                    }
                }
            }
        } catch (IOException e) {
            try {
                if (servidor != null) {
                    servidor.close();
                }
            } catch (IOException e1) {
            }

            System.err.println("a porta está ocupada ou servidor foi fechado");
        }
    }
    
    public static void jogarContraPlayer (List<Player> jogadores) throws IOException {
        Jokenpo jogo = new Jokenpo();

        jogadores.get(0).comunicacao.EnviarMsg("Você está jogando contra " + jogadores.get(1).nomeJogador + " faça sua jogada");
        jogadores.get(0).jogada = jogadores.get(0).comunicacao.ReceberMsg();

        jogadores.get(1).comunicacao.EnviarMsg("Você está jogando contra " + jogadores.get(0).nomeJogador + " faça sua jogada");
        jogadores.get(1).jogada = jogadores.get(1).comunicacao.ReceberMsg();  
        
        if (jogadores.get(0).jogada != null && jogadores.get(1).jogada != null) {
            String vencedor = jogo.retornarVencedor(jogadores.get(0).nomeJogador, jogadores.get(1).nomeJogador, jogadores.get(0).jogada, jogadores.get(1).jogada);
            
            for (int i = 0; i < jogadores.size(); i ++) {
                jogadores.get(i).comunicacao.EnviarMsg("O vencedor foi: " + vencedor + " você deseja jogar novamente ?");
            }
            
            for (int i = 0; i < jogadores.size(); i ++) {
                if (jogadores.get(i).comunicacao.ReceberMsg().equalsIgnoreCase("sim")) {
                    jogadores.get(i).jogada = null;
                    jogadores.get(i).jogarNovamente = true;
                } else {
                    jogadores.get(i).jogarNovamente = false;
                }            
             }          
        }   
    }
    
    public static void jogarContraCpu(List<Player> jogadores) throws IOException {
        Player jogador = jogadores.get(0);
        jogador.comunicacao.EnviarMsg(jogadores.get(0).nomeJogador + " Faça sua jogada");
        jogador.jogada = jogadores.get(0).comunicacao.ReceberMsg();
        Jokenpo jogo = new Jokenpo();
        String jogadaCpu = jogo.retornarJogadaCpu();
        String vencedor = jogo.retornarVencedor("CPU", jogador.nomeJogador, jogadaCpu, jogador.jogada);
        jogador.comunicacao.EnviarMsg("Você jogou " + jogador.jogada + " e a CPU jogou " + jogadaCpu + " O vencedor foi " + vencedor + " Deseja jogar novamente ? 1 - Sim | 2 - Não");
        String jogarNovamente = jogador.comunicacao.ReceberMsg();
        
        if (vencedor.equals("CPU")) {
            setLose(jogador);
        } else if (vencedor.equals(jogador.nomeJogador)) {
            setWin(jogador);
        } else {
            setDraw(jogador);
        }
        
        while (!jogarNovamente.equals("1") && !jogarNovamente.equals("2")) {
            jogador.comunicacao.EnviarMsg("Deseja jogar novamente 1 - Sim | 2 - Não");
            jogarNovamente = jogador.comunicacao.ReceberMsg();     
        }
        
        if (jogarNovamente.equals("2")) {
            jogador.comunicacao.EnviarMsg("Você ganhou = " + jogador.getCountVitorias() + ", Empatou = " + jogador.getCountEmpates() + " e perdeu = " + jogador.getCountDerrotas() + " ********************* A CPU ganhou = " + jogador.getCountDerrotas() + ", Empatou = " + jogador.getCountEmpates() + " e perdeu = " + jogador.getCountVitorias() + " saindo...");
            jogador.player.close();
        }
        
        jogador.jogarNovamente = true;
    }
    
    
    public static void setPlayersName (List<Player> jogadores, String msg) throws IOException {
        for (int i = 0; i < jogadores.size(); i ++) {
            if (jogadores.get(i).nomeJogador == null) {
                jogadores.get(i).comunicacao.EnviarMsg(msg);
                jogadores.get(i).nomeJogador = jogadores.get(i).comunicacao.ReceberMsg();
            }           
        }
    }
    
     public static void setPlayerGameMode (List<Player> jogadores) throws IOException {
        for (int i = 0; i < jogadores.size(); i ++) {
            if (jogadores.get(i).modoJogo == null) {
                jogadores.get(i).comunicacao.EnviarMsg("Selecione o modo de jogo 1 - PVP | 2 - Player Vs CPU");
                jogadores.get(i).modoJogo = jogadores.get(i).comunicacao.ReceberMsg();
            }
        }
    }
    
    public static void setWin(Player player) {
        player.setCountVitorias(1);
    }
    
    public static void setDraw(Player player) {
        player.setCountEmpates(1);
    }
    
    public static void setLose(Player player) {
        player.setCountDerrotas(1);
    }
}
