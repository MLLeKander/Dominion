package dominion481.tictactoe;

import java.util.Arrays;
import java.util.List;

import dominion481.server.Game;
import dominion481.server.RemotePlayer;

public class TicTacToe extends Game {
   Character[][] board = new Character[3][3];
   TicTacToePlayer xPlayer = null, oPlayer = null;
   List<TicTacToePlayer> players;
   
   public void addPlayer(TicTacToePlayer p) {
      if (xPlayer == null)
         xPlayer = p;
      else
         oPlayer = p;
   }
   
   @Override
   public void run() {
      players = Arrays.asList(xPlayer, oPlayer);
      play();
   }
   
   public TicTacToePlayer play() {
      for (int i = 0; i <3; i++)
         for (int o = 0; o <3; o++)
            board[i][o] = '.';
      
      char winner;
      while (true) {
         for (TicTacToePlayer p : players) {
            notifyBoard();
            p.takeTurn();
            if ((winner = isGameOver()) != '.')
               return winner == 'X' ? xPlayer : oPlayer;
         }
      }
   }
   
   public void play(TicTacToePlayer p, int x, int y) {
      if (xPlayer != p && oPlayer != p)
         throw new IllegalArgumentException("Unknown player "+p);
      if (x < 0 || x > 2 || y < 0 || y > 2 || board[x][y] != '.')
         throw new IllegalArgumentException("Invalid location ("+x+","+y+")");
      board[x][y] = p == xPlayer ? 'X' : 'O';
   }
   
   public char isGameOver() {
      if (match(board[0][0], board[1][1], board[2][2]) || match(board[0][2], board[1][1], board[2][0]))
         return board[1][1];
      for (int i = 0; i <3; i++)
         if (match(board[i][0],board[i][1],board[i][2]))
            return board[i][0];
         else if (match(board[0][i], board[1][i], board[2][i]))
            return board[0][i];
      return '.';
      
   }
   
   public void notifyBoard() {
      notifyAll("row0",board[0]);
      notifyAll("row1",board[1]);
      notifyAll("row2",board[2]);
   }
   
   public boolean match(char a, char b, char c) {
      return a != '.' && a == b && a == c;
   }
   
   @Override
   public List<RemotePlayer> getRemotePlayers() {
      return Arrays.asList((RemotePlayer)xPlayer, oPlayer);
   }

}
