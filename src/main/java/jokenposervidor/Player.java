package jokenposervidor;

import java.io.IOException;
import java.net.Socket;

public class Player extends Thread {

    Socket player;
    Comunicacao comunicacao;
    String nomeJogador = null;
    String modoJogo = null;
    String jogada = null;
    boolean jogarNovamente = true;
    String escolhaJogador = null;
    private int countVitorias = 0;
    private int countEmpates = 0;
    private int countDerrotas = 0;
    
    public Player(Socket socket) throws IOException {
        this.player = socket;
        this.comunicacao = new Comunicacao(socket);
        this.start();
    }

    public void setCountVitorias(int countVitorias) {
        this.countVitorias = this.getCountVitorias() + 1;
    }

    public void setCountEmpates(int countEmpates) {
        this.countEmpates = this.getCountEmpates() + 1;
    }

    public void setCountDerrotas(int countDerrotas) {
        this.countDerrotas = this.getCountDerrotas() + 1;
    }

    public void setPlayerName (String name) {
        this.nomeJogador = name;
    }

    public int getCountVitorias() {
        return countVitorias;
    }

    public int getCountEmpates() {
        return countEmpates;
    }

    public int getCountDerrotas() {
        return countDerrotas;
    }

}
