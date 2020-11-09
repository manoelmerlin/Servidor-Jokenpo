package jokenposervidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Servidor {
    static List<Player> jogadores = new ArrayList<Player>();

    public static void main(String[] args) throws InterruptedException {

        ServerSocket servidor = null;

        try {
            servidor = new ServerSocket(8081);
            System.out.println("servidor startado na porta 8081");
            while (true) {
                System.out.println("Aguardando conexão...");
                Socket clienteSocket = servidor.accept();
                System.out.println("Conectado com " + clienteSocket.getInetAddress().getHostAddress());

                Player player = new Player(clienteSocket);
                jogadores.add(player);

                System.out.println(jogadores.size());
                setPlayerGameMode(jogadores);
                setPlayersName(jogadores, "Digite seu nome");
                
                if (jogadores.get(0).modoJogo.equals("1")) {
                    if (jogadores.size() == 2) {
                        if (jogadores.get(0).nomeJogador != null && jogadores.get(1).nomeJogador != null) {
                            jogarContraPlayer(jogadores);
                            while (jogadores.get(0).jogarNovamente && jogadores.get(1).jogarNovamente) {
                                jogarContraPlayer(jogadores);
                            }
                        } else {
                            jogadores.get(jogadores.size()).comunicacao.EnviarMsg("O servidor está cheio digite 10 para sair");
                        }
                    }
                } else {
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

    public static void jogarContraPlayer(List<Player> jogadores) throws IOException, InterruptedException {
        Jokenpo jogo = new Jokenpo();

        fazerJogada();

        while (!jogo.validarJogada(jogadores.get(0).jogada) || !jogo.validarJogada(jogadores.get(1).jogada)) {
            if (!jogo.validarJogada(jogadores.get(0).jogada)) {
                jogadores.get(0).comunicacao.EnviarMsg("Faça sua jogada");
                jogadores.get(0).jogada = jogadores.get(0).comunicacao.ReceberMsg();
            }

            if (!jogo.validarJogada(jogadores.get(1).jogada)) {
                jogadores.get(1).comunicacao.EnviarMsg("Faça sua jogada");
                jogadores.get(1).jogada = jogadores.get(1).comunicacao.ReceberMsg();
            }
        }

        if (jogadores.get(0).jogada != null && jogadores.get(1).jogada != null) {
            String vencedor = jogo.retornarVencedor(jogadores.get(0).nomeJogador, jogadores.get(1).nomeJogador, jogadores.get(0).jogada, jogadores.get(1).jogada);
            contabilizarPartidaPvp(jogadores, vencedor);
            String jogadaPlayerUm = jogadores.get(0).jogada;

            jogadores.get(0).comunicacao.EnviarMsg("Você jogou : " + jogadores.get(0).jogada + " e o " + jogadores.get(1).nomeJogador + " jogou : " + jogadores.get(1).jogada + " *********** O vencedor foi: " + vencedor + " *********** se você deseja jogar novamente digite 1 - Sim ou digite Ou aperte qualquer tecla para sair");
            jogadores.get(1).comunicacao.EnviarMsg("Você jogou : " + jogadores.get(1).jogada + " e o " + jogadores.get(0).nomeJogador + " jogou : " + jogadaPlayerUm + " *********** O vencedor foi: " + vencedor + " *********** se você deseja jogar novamente digite 1 - Sim ou digite Ou aperte qualquer tecla para sair para sair");

            if (jogadores.get(0).comunicacao.ReceberMsg().equals("1") && jogadores.get(1).comunicacao.ReceberMsg().equals("1")) {
                jogadores.get(0).jogada = null;
                jogadores.get(0).jogarNovamente = true;
                jogadores.get(1).jogada = null;
                jogadores.get(1).jogarNovamente = true;
            } else {
                jogadores.get(0).comunicacao.EnviarMsg("Um dos jogadores escolheu sair... Você ganhou = " + jogadores.get(0).getCountVitorias() + " empatou = " + jogadores.get(0).getCountEmpates() + " e perdeu = " + jogadores.get(0).getCountDerrotas() + " Aperte 10 para sair do servidor");
                jogadores.get(1).comunicacao.EnviarMsg("Um dos jogadores escolheu sair... Você ganhou = " + jogadores.get(1).getCountVitorias() + " empatou = " + jogadores.get(1).getCountEmpates() + " e perdeu = " + jogadores.get(1).getCountDerrotas() + " Aperte 10 para sair do servidor");
                jogadores.get(0).comunicacao.ReceberMsg();
                jogadores.get(1).comunicacao.ReceberMsg();
            }
        }
        
    }

    public static void contabilizarPartidaPvp(List<Player> jogadores, String vencedor) {
        if (vencedor.equals(jogadores.get(0).nomeJogador)) {
            setWin(jogadores.get(0));
        } else if (vencedor.equals(jogadores.get(1).nomeJogador)) {
            setWin(jogadores.get(1));
        }

        if (vencedor.equals("empate")) {
            for (int i = 0; i < jogadores.size(); i++) {
                setDraw(jogadores.get(i));
            }
        }

        if (vencedor.equals(jogadores.get(0).nomeJogador)) {
            setLose(jogadores.get(1));
        } else if (vencedor.equals(jogadores.get(1).nomeJogador)) {
            setLose(jogadores.get(0));
        }
    }

    public static void jogarContraCpu(List<Player> jogadores) throws IOException {
        Player jogador = jogadores.get(0);
        Jokenpo jogo = new Jokenpo();

        jogador.comunicacao.EnviarMsg(jogadores.get(0).nomeJogador + " faça sua jogada: pedra, papel ou tesoura");
        jogador.jogada = jogadores.get(0).comunicacao.ReceberMsg();

        while (!jogo.validarJogada(jogadores.get(0).jogada)) {
            jogadores.get(0).comunicacao.EnviarMsg("Faça uma jogada válida entre pedra, papel ou tesoura");
            jogadores.get(0).jogada = jogadores.get(0).comunicacao.ReceberMsg();
        }

        String jogadaCpu = jogo.retornarJogadaCpu();
        String vencedor = jogo.retornarVencedor("CPU", jogador.nomeJogador, jogadaCpu, jogador.jogada);

        jogador.comunicacao.EnviarMsg("Você jogou " + jogador.jogada + " e a CPU jogou " + jogadaCpu + " *********** O vencedor foi : " + vencedor + " *********** Deseja jogar novamente ? 1 - Sim | 10 - Não");
        String jogarNovamente = jogador.comunicacao.ReceberMsg();

        if (vencedor.equals("CPU")) {
            setLose(jogador);
        } else if (vencedor.equals(jogador.nomeJogador)) {
            setWin(jogador);
        } else {
            setDraw(jogador);
        }

        while (!jogarNovamente.equals("1") && !jogarNovamente.equals("10")) {
            jogador.comunicacao.EnviarMsg("Deseja jogar novamente 1 - Sim | 10 - Não");
            jogarNovamente = jogador.comunicacao.ReceberMsg();
        }

        if (jogarNovamente.equals("10")) {
            jogador.comunicacao.EnviarMsg("Você ganhou = " + jogador.getCountVitorias() + ", Empatou = " + jogador.getCountEmpates() + " e perdeu = " + jogador.getCountDerrotas() + " *********** A CPU ganhou = " + jogador.getCountDerrotas() + ", Empatou = " + jogador.getCountEmpates() + " e perdeu = " + jogador.getCountVitorias() + " *********** saindo...");
            jogador.jogarNovamente = false;
        } else {
            jogador.jogarNovamente = true;
        }
    }

    public static void setPlayersName(List<Player> jogadores, String msg) throws IOException {
        for (int i = 0; i < jogadores.size(); i++) {
            if (jogadores.get(i).nomeJogador == null) {
                jogadores.get(i).comunicacao.EnviarMsg(msg);
                jogadores.get(i).nomeJogador = jogadores.get(i).comunicacao.ReceberMsg();
            }
        }
    }

    public static void setPlayerGameMode(List<Player> jogadores) throws IOException {
        if (jogadores.get(0).modoJogo == null) {
            jogadores.get(0).comunicacao.EnviarMsg("Selecione o modo de jogo que deseja jogar em nosso Jokenpo: 1 - Player Vs Player | 2 - Player Vs CPU");
            String modoJogo = jogadores.get(0).comunicacao.ReceberMsg();
            while (!modoJogo.equals("1") && !modoJogo.equals("2")) {
                jogadores.get(0).comunicacao.EnviarMsg("Selecione o modo de jogo que deseja jogar em nosso Jokenpo: 1 - Player Vs Player | 2 - Player Vs CPU");
                modoJogo = jogadores.get(0).comunicacao.ReceberMsg();
            }
            jogadores.get(0).modoJogo = modoJogo;
        }

    }

    public static void fazerJogada () {
        enviarMsgDoisJogadores("Faça uma  jogada valída Pedra | Papel | Tesoura");
        receberMsgDoisJogadores();
    }

    public static void enviarMsgDoisJogadores (String msg) {
        for (int i = 0; i < jogadores.size(); i++) {
            jogadores.get(i).comunicacao.EnviarMsg(msg);
        }
    }

    public static void receberMsgDoisJogadores () {
        try {
            for (int i = 0; i < jogadores.size(); i++) {
                jogadores.get(i).jogada = jogadores.get(i).comunicacao.ReceberMsg();
            }
        } catch (Exception e) {
            e.getMessage();
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