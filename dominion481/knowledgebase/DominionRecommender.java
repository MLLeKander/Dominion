package dominion481.knowledgebase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * This is a sample class to launch a rule.
 */
public class DominionRecommender {
   public enum KBCardType {
      ACTION,
      TREASURE,
      VICTORY
   }
   
   public enum KBCard {
      COPPER(KBCardType.TREASURE, 0),
      SILVER(KBCardType.TREASURE, 3),
      GOLD(KBCardType.TREASURE, 6),
      ESTATE(KBCardType.VICTORY, 2),
      DUCHY(KBCardType.VICTORY, 5),
      PROVINCE(KBCardType.VICTORY, 8),
      CELLAR(2, false, 1, 0, 0, 0),
      CHAPEL(2, false, 0, 0, 0, 0),
      MOAT(2, false, 0, 0, 2, 0),
      CHANCELLOR(3, false, 0, 0, 0, 2),
      VILLAGE(3, false, 2, 0, 1, 0),
      WOODCUTTER(3, false, 0, 1, 0, 2),
      WORKSHOP(3, false, 0, 0, 0, 0),
      BUREAUCRAT(4, true, 0, 0, 0, 2),
      FEAST(4, false, 0, 0, 0, 0),
      FESTIVAL(5, false, 2, 1, 0, 2),
      GARDENS(KBCardType.VICTORY, 4),
      MILITIA(4, true, 0, 0, 0, 2),
      MONEYLENDER(4, false, 0, 0, 0, 0),
      REMODEL(4, false, 0, 0, 0, 0),
      SMITHY(4, false, 0, 0, 3, 0),
      SPY(4, true, 1, 0, 1, 0),
      THIEF(4, true, 0, 0, 0, 0),
      THRONE_ROOM(4, false, 0, 0, 0, 0),
      COUNCIL_ROOM(5, false, 0, 1, 4, 0),
      LABORATORY(5, false, 1, 0, 2, 0),
      LIBRARY(5, false, 0, 0, 0, 0),
      MARKET(5, false, 1, 1, 1, 1),
      MINE(5, false, 0, 0, 0, 0),
      WITCH(6, true, 0, 0, 2, 0),
      ADVENTURER(6, false, 0, 0, 0, 0)
      ;
      
      private KBCardType type;
      private int cost;
      private boolean attack;
      private int cards;
      private int actions;
      private int buys;
      private int coin;
      
      KBCard(int cost, boolean attack, int actions, int buys, int cards, int coin) {
         this(KBCardType.ACTION, cost);
         this.attack = attack;
         this.cards = cards;
         this.actions = actions;
         this.buys = buys;
         this.coin = coin;
      }
      
      KBCard(KBCardType type, int cost) {
         this.type = type;
         this.cost = cost;
      }
      
      public int getCards() {
         return cards;
      }

      public int getActions() {
         return actions;
      }

      public int getBuys() {
         return buys;
      }

      public int getCoin() {
         return coin;
      }

      public KBCardType getType() {
         return type;
      }
      
      public int getCost() {
         return cost;
      }
      
      public boolean isAttack() {
         return attack;
      }
   }
   
   public static class TableCard {
      private KBCard card;
      private int available;
      public int recommendation;
      
      public TableCard(KBCard card, int available) {
         this.card = card;
         this.available = available;
      }
      
      public KBCard getCard() {
         return card;
      }
      
      public void setCard(KBCard card) {
         this.card = card;
      }
      
      public int getAvailable() {
         return available;
      }
      
      public void setAvailable(int available) {
         this.available = available;
      }

      public int getRecommendation() {
         return recommendation;
      }

      public void setRecommendation(int recommendation) {
         this.recommendation = recommendation;
      }
   }
   
   public static class DeckCard {
      private KBCard card;

      public DeckCard(KBCard card) {
         this.card = card;
      }

      public KBCard getCard() {
         return card;
      }

      public void setCard(KBCard card) {
         this.card = card;
      }
   }
   
   public static class EnemyCard {
      private KBCard card;

      public EnemyCard(KBCard card) {
         this.card = card;
      }

      public KBCard getCard() {
         return card;
      }

      public void setCard(KBCard card) {
         this.card = card;
      }
   }
   
   private static KnowledgeBase kbase;
   
   private StatefulKnowledgeSession session;
   private List<TableCard> cards = new ArrayList<TableCard>();
   
   public DominionRecommender(Map<KBCard, Integer> purchasable) throws Exception {
      session = getKnowledgeBase().newStatefulKnowledgeSession();
      for (Entry<KBCard, Integer> entry : purchasable.entrySet()) {
         TableCard tc = new TableCard(entry.getKey(), entry.getValue());
         session.insert(tc);
         cards.add(tc);
      }
   }
   
   public void addPurchase(KBCard card) {
      session.insert(new DeckCard(card));
   }
   
   public void addEnemyPurchase(KBCard card) {
      session.insert(new EnemyCard(card));
   }
   
   public void printReqs() {
      session.fireAllRules();
      Collections.sort(cards, new Comparator<TableCard>() {
         public int compare(TableCard arg0, TableCard arg1) {
            return arg1.recommendation - arg0.recommendation;
         }
      });
      
      for (TableCard tc : cards) {
         System.out.println(tc.getCard() + ": " + tc.getRecommendation());
      }
   }
   
   public KBCard getRecommendation(int coin) {
      session.fireAllRules();
      Collections.sort(cards, new Comparator<TableCard>() {
         public int compare(TableCard arg0, TableCard arg1) {
            return arg1.recommendation - arg0.recommendation;
         }
      });
      
      for (TableCard card : cards) {
         if (card.getCard().getCost() <= coin && card.getRecommendation() > 0) {
            return card.getCard();
         }
      }
      
      return null;
   }
   
   private static KnowledgeBase getKnowledgeBase() throws Exception {
      if (kbase != null) {
         return kbase;
      }
      
      KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
      kbuilder.add(ResourceFactory.newClassPathResource("dominion481/knowledgebase/DominionRules.drl"),
          ResourceType.DRL);
      KnowledgeBuilderErrors errors = kbuilder.getErrors();
      if (errors.size() > 0) {
        for (KnowledgeBuilderError error : errors) {
          System.err.println(error);
        }
        throw new IllegalArgumentException("Could not parse knowledge.");
      }
      kbase = KnowledgeBaseFactory.newKnowledgeBase();
      kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
      return kbase;
    }
    
    
  
  public static final void main(String[] args) {
    try {
      // load up the knowledge base

      List<KBCard> cards = new ArrayList<KBCard>(Arrays.asList(KBCard.values()));
      cards.remove(KBCard.PROVINCE);
      cards.remove(KBCard.DUCHY);
      cards.remove(KBCard.ESTATE);
      cards.remove(KBCard.COPPER);
      cards.remove(KBCard.SILVER);
      cards.remove(KBCard.GOLD);
      
      Map<KBCard, Integer> tableCards = new HashMap<KBCard, Integer>();
      tableCards.put(KBCard.PROVINCE, 8);
      tableCards.put(KBCard.DUCHY, 8);
      tableCards.put(KBCard.ESTATE, 8);
      tableCards.put(KBCard.GOLD, Integer.MAX_VALUE);
      tableCards.put(KBCard.COPPER, Integer.MAX_VALUE);
      tableCards.put(KBCard.SILVER, Integer.MAX_VALUE);
      
      Random rand = new Random();
      for (int i = 0; i < 10; i++) {
         KBCard card = cards.remove(rand.nextInt(cards.size()));
         System.out.println(card);
         tableCards.put(card, card.getType() == KBCardType.VICTORY ? 12 : 10);
      }
      
      DominionRecommender recommender = new DominionRecommender(tableCards); 
      
      Scanner s = new Scanner(System.in);
      while (true) {
         try {
            System.out.println("What to do?");
            String in = s.nextLine();
            switch (in) {
            case "b":
               System.out.println("What card?");
               KBCard mycard = KBCard.valueOf(s.nextLine().toUpperCase());
               recommender.addPurchase(mycard);
               break;
            case "e":
               System.out.println("What card?");
               KBCard hiscard = KBCard.valueOf(s.nextLine().toUpperCase());
               recommender.addEnemyPurchase(hiscard);
               break;
            case "r":
               System.out.println("What do you have?");
               int coin = Integer.valueOf(s.nextLine());
               System.out.println(recommender.getRecommendation(coin));
               break;
            case "p":
               recommender.printReqs();
            }
         } catch (Throwable t) {
            t.printStackTrace();
         }
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
  }

  

}

