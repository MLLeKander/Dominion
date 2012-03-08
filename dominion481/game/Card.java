package dominion481.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public enum Card {
	//Kingdom Cards
	
	/*
	 * Cellar
	 * + 1 Action
	 * Discard any number of cards. +1 Card per card discarded.
	 */
   Cellar(2) {
      void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         List<Card> discards = player.cellar();
         for (Card discard : discards) {
            player.discard(discard);
         }
      
         for (int i = 0; i < discards.size(); i++) {
            player.draw();
         }
      }
   },
	/*
	 * Chapel
	 * Trash up to 4 cards from hand.
	 */
	Chapel(2) {
	   void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
	      List<Card> trashes = player.chapel();
	      for (Card trash : trashes) {
	         if (!player.hand.remove(trash)) {
	            throw new IllegalArgumentException("Trash " + trash + " is not in hand");
	         }
	      }
	   }
	},
   /*
    * Moat
    * +2 Cards
    * Reacts to protect against attacks
    */
   Moat(2) {
      void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.draw();
         player.draw();
      }
   },
   /*
    * Chancellor
    * +(2)
    * You may put your deck into your discard
    */
   Chancellor(3) {
      void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.coin += 2;
         
         if (player.chancellor()) {
            player.discard.addAll(player.deck);
            player.deck = new LinkedList<Card>();
         }
      }
   },
   /*
    * Village
    * +1 Card
    * +2 Actions
    */
   Village(3) {
      void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.actions += 2;
         player.draw();
      }
   },
   /*
    * Woodcutter
    * +(2)
    * +1 Buy
    */
   Woodcutter(3) {
      void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.coin += 2;
         player.buys += 1;
      }
   },
   /*
    * Workshop
    * Gain any card costing up to 4
    */
   Workshop(3) {
      void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         Card gain = player.workshop();
         if (gain != null) {
            if (gain.cost <= 4) {
               player.gain(gain);
            }
            else {
               throw new IllegalArgumentException("Cannot workshop " + gain);
            }
         }
      }
   },
   /*
    * Bureaucrat
    * Gain a silver and place it atop of your deck
    * All others must place a VP card from hand atop their deck (if possible)
    */
   Bureaucrat(4, true) {
      @Override
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         if (game.boardMap.get(Silver) > 0)
            player.gain(Silver);
         player.deck.add(0, Silver);
         player.discard.remove(Silver);
         
         for (DominionPlayer p : game.players)
            if (p != player) {
               boolean hasVictoryCard = false;
               for (Card c : p.hand)
                  hasVictoryCard |= c.type == Type.TREASURE;
               
               if (hasVictoryCard) {
                  Card toPutback = p.bureaucrat();
                  if (!p.hand.remove(toPutback))
                     throw new IllegalArgumentException("Cannot bureaucrat "+toPutback);
                  p.deck.add(0, toPutback);
                  toPutback.reveal(player, game);
               }
               else {
                  for (Card c : p.hand)
                     c.reveal(player, game);
               }
            }
      }
   },
   /*
    * Feast
    * Trash this. Gain a card costing up to 5
    */
   Feast(4) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.inPlay.remove(this);
         
         Card gain = player.feast();
         if (gain != null) {
            if (gain.cost <= 5) {
               player.gain(gain);
            }
            else {
               throw new IllegalArgumentException("Cannot feast " + gain);
            }
         }
      }
   },
   /*
    * Gardens
    * VP Value is deck size / 10, rounded down
    */
   GARDENS(Type.VICTORY, 4, 0, 0) {
      public int getVp(DominionPlayer player) {
         return (player.deck.size() + player.discard.size()) / 10;
      }
   },
   /*
    * Militia
    * +(2)
    * Each other player discards down to 3 cards in their hand
    */
   Militia(4, true) {
      @Override
      void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.coin += 2;
         
         for (DominionPlayer p : game.players)
            if (p != player) {
               if (p.hand.size() <= 3)
                  continue;
               List<Card> toDiscard = p.militia();
               if (p.hand.size() - toDiscard.size() > 3)
                  throw new IllegalArgumentException("Must discard down to 3 cards");
               for (Card c : toDiscard) {
                  if (!p.hand.remove(c))
                     throw new IllegalArgumentException(c+" not in hand");
                  game.reveal(p, c);
                  p.discard.add(c);
               }
            }
      }
   },
   /*
    * Moneylender
    * Trash a copper from your hand. If you do, +(3)
    */
   Moneylender(4) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         if (player.hand.remove(Card.Copper)) {
            player.coin += 3;
         }
      }
   },
   /*
    * Remodel
    * Trash a card from your hand. Gain a card costing up to 2 more
    */
   Remodel(4) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         if (player.hand.size() == 0) {
            return;
         }
         
         Card[] remodel = player.remodel();
         Card trash = remodel[0];
         Card gain = remodel[1];
         
         if (!player.hand.remove(trash)) {
            throw new IllegalArgumentException("Trash " + trash + " is not in hand");
         }
         
         if (gain != null) {
            if (gain.cost <= trash.getCost() + 2) {
               player.gain(gain);
            }
            else {
               throw new IllegalArgumentException("Cannot remodel " + trash + " to "+ gain);
            }
         }
      } 
   },
   /*
    * Smithy
    * +3 Cards
    */
   Smithy(4) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.draw();
         player.draw();
         player.draw();
      }
   },
   /*
    * Spy
    * +1 Card
    * +1 Action
    * Every player reveals the top card from their deck. You decide whether to put it back or to discard it.
    */
   Spy(4, true) {
      @Override
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.draw();
         player.actions++;
         
         for (DominionPlayer p : game.players) {
            Card topCard = p.deck.remove();
            
            if (player.spyDiscard(topCard, p))
               p.discard.add(topCard);
            else
               p.deck.add(0, topCard);
         }
      }
   },
   /*
    * Thief
    * Each other player reveals the top 2 cards of their deck
    * You may trash a treasure card of these two, and you choose if you want to gain this card
    * The other revealed cards are discarded
    */
   Theif(4, true) {
      @Override
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         for (DominionPlayer p : game.players)
            if (p != player) {
               Card toTrash = null, a = p.deck.remove(), b = p.deck.remove();
               game.reveal(p, a);
               game.reveal(p, b);
               if (a.type == Type.TREASURE)
                  toTrash = a;
               if (b.type == Type.TREASURE)
                  toTrash = toTrash == null ? b : a.getCost() > b.getCost() ? a : b;
               if (toTrash != null) {
                  game.trash(player, toTrash);
                  if (player.theifGain(toTrash))
                     player.gain(toTrash);
               }
            }
      }
   },
   ThroneRoom(4) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         Card card = player.throneRoom();
         
         if (card != null) {
            player.actions += 1;
            player.playAction(card);
            card.play(player, game);
         }
      }
   },
   /*
    * Council Room
    * +4 Cards
    * +1 Buys
    * Every other player draws a card
    */
   CouncilRoom(5) {
      @Override
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.draw();
         player.draw();
         player.draw();
         player.draw();
         player.buys++;
         
         for (DominionPlayer p : game.players)
            if (p != player)
               p.draw();
      }
   },
   /*
    * Festival
    * +2 Actions
    * +1 Buy
    * +(2)
    */
   Festival(5) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.actions += 2;
         player.buys += 1;
         player.coin += 2;
      }
   },
   /*
    * Festival
    * +2 Actions
    * +1 Buy
    * +(2)
    */
   Laboratory(5) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.actions += 1;
         player.draw();
         player.draw();
      }
   },
   /*
    * Library
    * Draw until you have 7 cards
    * You may set aside and later discard any actions drawn in this manner
    */
   Library(5) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         List<Card> setAside = new ArrayList<Card>();
         
         while (player.hand.size() < 7) {
            Card draw = player.draw();
            if (draw == null) {
               break;
            }
            
            if (draw.type == Type.ACTION) {
               if (player.libraryDiscard(draw)) {
                  setAside.add(draw);
                  player.hand.remove(draw);
               }
            }
         }
         
         player.discard.addAll(setAside);
      }
   },
   /*
    * Market
    * +1 Card, +1 Action, +1 Buy, +(1)
    */
   Market(5) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.coin += 1;
         player.actions += 1;
         player.buys += 1;
         player.draw();
      }
   },
   /*
    * Mine
    * Trash a treasure from hand. Gain a treasure costing up to 3 more... in hand
    */
   Mine(5) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         if (Card.filter(player.hand, Type.TREASURE).size() == 0) {
            return;
         }
         
         Card[] remodel = player.mine();
         Card trash = remodel[0];
         Card gain = remodel[1];
         
         if (trash.type != Type.TREASURE) {
            throw new IllegalArgumentException("Cannot mine " + trash);
         }
         
         if (!player.hand.remove(trash)) {
            throw new IllegalArgumentException("Trash " + trash + " is not in hand");
         }
         
         if (gain != null) {
            if (gain.cost <= trash.getCost() + 3 && gain.type == Type.TREASURE) {
               //TODO Clean this up?
               player.gain(gain);
               player.discard.remove(gain);
               player.hand.add(gain);
            }
            else {
               throw new IllegalArgumentException("Cannot mine " + trash + " to "+ gain);
            }
         }
      } 
   },
   Witch(5, true) {
      @Override
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         player.draw();
         player.draw();
         
         for (DominionPlayer p : game.players)
            if (p != player && game.boardMap.get(Curse) > 0)
               p.gain(Curse);
      }
   },
   /*
    * Adventurer
    * Reveal cards from your deck until you find two treasures.
    * Add the treasures to your hand. Discard the revealed cards.
    */
   Adventurer(6) {
      public void play(DominionPlayer player, Dominion game) {
         super.play(player, game);
         int found = 0;
         List<Card> setAside = new ArrayList<Card>();
         
         while (found < 2) {
            Card card = player.draw();
            if (card == null) {
               break;
            }
            else if (card.type == Type.TREASURE) {
               found++;
            }
            else {
               setAside.add(card);
               player.hand.remove(card);
            }
         }
         
         player.discard.addAll(setAside);
      }
   },
   
   //Base treasure cards
   GOLD(Type.TREASURE, 6, 0, 3),
   SILVER(Type.TREASURE, 3, 0, 2),
   COPPER(Type.TREASURE, 0, 0, 1),
   
  //Base victory cards
   Province(Type.VICTORY, 8, 6, 0),
   Duchy(Type.VICTORY, 5, 1, 0),
   Estate(Type.VICTORY, 2, 3, 0),
   Curse(Type.VICTORY, 0, -1, 0)
	;
	public enum Type {
		ACTION("\033[36m"), TREASURE("\033[33m"), VICTORY("\033[32m");
		
		public final String colorCode;
		private Type(String color) { colorCode = color; }
	}
	
	private final int cost, vp, treasureValue;
	private final boolean attack;
	public final Type type;
	
	public int getVp(DominionPlayer player) { return vp; }
	public int getCost() { return cost; }
	public int getTreasureValue() { return treasureValue; }
	public boolean isAttack() { return attack; }
	
	public String getColorName(){ return type.colorCode+this+"\033[0m"; }
	
	void play(DominionPlayer player, Dominion game) {
	   if (type == Type.ACTION)
	      game.notifyAll("cardPlayed "+player+" "+toString());
	}
	
	void reveal(DominionPlayer player, Dominion game) {
	   game.notifyAll("cardRevealed "+player+" "+toString());
	}
	
	private Card(int cost) {
	   this(cost, false);
	}
	
	private Card(int cost, boolean attack) {
	   this(Type.ACTION, cost, 0, 0, attack);
	}
	
	private Card(Type type, int cost, int vp, int treasureValue) {
		this(type, cost, vp, treasureValue, false);
	}
	
	private Card(Type type, int cost, int vp, int treasureValue, boolean attack) {
	   this.cost = cost;
      this.vp = vp;
      this.treasureValue = treasureValue;
      this.type = type;
      this.attack = attack;
	}
   
   public static List<Card> filter(Iterable<Card> cards, Type target) {
      List<Card> out = new ArrayList<Card>();
      for (Card c : cards)
         if (c.type == target)
            out.add(c);
      return out;
   }
   
   public static List<Card> filter(Iterable<Card> cards, int min, int max) {
      List<Card> out = new ArrayList<Card>();
      for (Card c : cards)
         if ((min < 0 || min < c.getCost()) && (max < 0 || max > c.getCost()))
            out.add(c);
      return out;
   }
   
   public static Card getCard(String name) {
      name = name.toUpperCase();
      for (Card c : values()) {
         if (c.toString().toUpperCase().startsWith(name)
               || c.getColorName().toUpperCase().startsWith(name)) {
            return c;
         }
      }
      return null;
   }
}
