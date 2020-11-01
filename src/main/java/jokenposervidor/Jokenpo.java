package jokenposervidor;

import java.util.Arrays;
import java.util.Random;

public class Jokenpo {
    private String jogadas[] = {"pedra", "papel", "tesoura"};
    
    public boolean validarJogada(String jogada) {
        boolean contains = Arrays.asList(jogadas).contains(jogada.toLowerCase());
        return contains;
    }
    
    public String retornarVencedor (String player1, String player2, String jogadaPlayer1, String jogadaPlayer2) {
        String vencedor = "empate";
       
        if (!jogadaPlayer1.toLowerCase().equals(jogadaPlayer2.toLowerCase())) { 
            if (jogadaPlayer1.toLowerCase().equals("pedra") && jogadaPlayer2.toLowerCase().equals("tesoura")) {
                vencedor = player1;
            } else if (jogadaPlayer1.toLowerCase().equals("tesoura") && jogadaPlayer2.toLowerCase().equals("papel")) {
                vencedor = player1;
            } else if (jogadaPlayer1.toLowerCase().equals("papel") && jogadaPlayer2.toLowerCase().equals("pedra")) {
                vencedor = player1;
            } else {
                vencedor = player2;
            }
        }
        
        return vencedor;
    }
    
    public String retornarJogadaCpu () {
        int index = new Random().nextInt(this.jogadas.length);
        return this.jogadas[index];
    }
    
}
