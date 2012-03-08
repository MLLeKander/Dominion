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
      Action,
      Treasure,
      Victory
   }
   
   public enum KBCard {
      Copper(KBCardType.Treasure, 0),
      Silver(KBCardType.Treasure, 3),
      Gold(KBCardType.Treasure, 6),
      Estate(KBCardType.Victory, 2),
      Duchy(KBCardType.Victory, 5),
      Province(KBCardType.Victory, 8),
      Cellar(2, false, 1, 0, 0, 0),
      Chapel(2, false, 0, 0, 0, 0),
      Moat(2, false, 0, 0, 2, 0),
      Chancellor(3, false, 0, 0, 0, 2),
      Village(3, false, 2, 0, 1, 0),
      Woodcutter(3, false, 0, 1, 0, 2),
      Workshop(3, false, 0, 0, 0, 0),
      Bureaucrat(4, true, 0, 0, 0, 2),
      Feast(4, false, 0, 0, 0, 0),
      Festival(5, false, 2, 1, 0, 2),
      Gardens(KBCardType.Victory, 4),
      Militia(4, true, 0, 0, 0, 2),
      Moneylender(4, false, 0, 0, 0, 0),
      Remodel(4, false, 0, 0, 0, 0),
      Smithy(4, false, 0, 0, 3, 0),
      Spy(4, true, 1, 0, 1, 0),
      Thief(4, true, 0, 0, 0, 0),
      ThroneRoom(4, false, 0, 0, 0, 0),
      CouncilRoom(5, false, 0, 1, 4, 0),
      Laboratory(5, false, 1, 0, 2, 0),
      Library(5, false, 0, 0, 0, 0),
      Market(5, false, 1, 1, 1, 1),
      Mine(5, false, 0, 0, 0, 0),
      Witch(6, true, 0, 0, 2, 0),
      Adventurer(6, false, 0, 0, 0, 0)
      ;
      
      private KbcardType type;
      private int cost;
      private boolean attack;
      private int cards;
      private int actions;
      private int buys;
      private int coin;
      
      KBCard(int cost, boolean attack, int actions, int buys, int cards, int coin) {
         this(KBCardType.Action, cost);
         this.attack = attack;
         this.cards = cards;
         this.actions = actions;
         this.buys = buys;
         this.coin = coin;
      }
      
      Kbcard(KBCardType type, int cost) {
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

      public KbcardType getType() {
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
      private Kbcard card;
      private int available;
      public int recommendation;
      
      public TableCard(KBCard card, int available) {
         this.card = card;
         this.available = available;
      }
      
      public Kbcard getCard() {
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
      private Kbcard card;

      public DeckCard(KBCard card) {
         this.card = card;
      }

      public Kbcard getCard() {
         return card;
      }

      public void setCard(KBCard card) {
         this.card = card;
      }
   }
   
   public static class EnemyCard {
      private Kbcard card;

      public EnemyCard(KBCard card) {
         this.card = card;
      }

      public Kbcard getCard() {
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
   
   public Kbcard getRecommendation(int coin) {
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
          ResourceType.Drl);
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
      cards.remove(KBCard.Province);
      cards.remove(KBCard.Duchy);
      cards.remove(KBCard.Estate);
      cards.remove(KBCard.Copper);
      cards.remove(KBCard.Silver);
      cards.remove(KBCard.Gold);
      
      Map<KBCard, Integer> tableCards = new HashMap<KBCard, Integer>();
      tableCards.put(KBCard.Province, 8);
      tableCards.put(KBCard.Duchy, 8);
      tableCards.put(KBCard.Estate, 8);
      tableCards.put(KBCard.Gold, Integer.MAX_VALUE);
      tableCards.put(KBCard.Copper, Integer.MAX_VALUE);
      tableCards.put(KBCard.Silver, Integer.MAX_VALUE);
      
      Random rand = new Random();
      for (int i = 0; i < 10; i++) {
         Kbcard card = cards.remove(rand.nextInt(cards.size()));
         System.out.println(card);
         tableCards.put(card, card.getType() == KBCardType.Victory ? 12 : 10);
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
               Kbcard mycard = KBCard.valueOf(s.nextLine().toUpperCase());
               recommender.addPurchase(mycard);
               break;
            case "e":
               System.out.println("What card?");
               Kbcard hiscard = KBCard.valueOf(s.nextLine().toUpperCase());
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

