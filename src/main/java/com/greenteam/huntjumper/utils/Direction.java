package com.greenteam.huntjumper.utils;

import java.util.*;


public enum Direction
{
   UP(-1, 0, 'n'),
   RIGHT(0, 1, 'e'),
   DOWN(1, 0, 's'),
   LEFT(0, -1, 'w');

   public static final List<Direction> values;
   static
   {
      values = new ArrayList<Direction>();
      for (Direction d : Direction.values())
      {
         values.add(d);
      }
   }

   private static final Map<Direction, Direction> rightLookup =
           new EnumMap<Direction, Direction>(Direction.class);
   private static final Map<Direction, Direction> leftLookup =
           new EnumMap<Direction, Direction>(Direction.class);
   private static final Map<Direction, Direction> behindLookup =
           new EnumMap<Direction, Direction>(Direction.class);
   private static final Map<Character, Direction> symbolLookup =
           new HashMap<Character, Direction>();

   public static List<Direction> getDirections(int shift)
   {
      List<Direction> directions = new ArrayList<Direction>(4);
      directions.add(UP);
      directions.add(RIGHT);
      directions.add(DOWN);
      directions.add(LEFT);

      shift = shift % 4;
      List<Direction> res = new ArrayList<Direction>(4);
      res.addAll(directions.subList(shift, directions.size()));
      res.addAll(directions.subList(0, shift));
      return res;
   }

   static
   {
      rightLookup.put(UP, RIGHT);
      rightLookup.put(RIGHT, DOWN);
      rightLookup.put(DOWN, LEFT);
      rightLookup.put(LEFT, UP);
      leftLookup.put(UP, LEFT);
      leftLookup.put(LEFT, DOWN);
      leftLookup.put(DOWN, RIGHT);
      leftLookup.put(RIGHT, UP);
      behindLookup.put(UP, DOWN);
      behindLookup.put(DOWN, UP);
      behindLookup.put(RIGHT, LEFT);
      behindLookup.put(LEFT, RIGHT);
      symbolLookup.put(UP.symbol, UP);
      symbolLookup.put(RIGHT.symbol, RIGHT);
      symbolLookup.put(DOWN.symbol, DOWN);
      symbolLookup.put(LEFT.symbol, LEFT);
   }

   public final int dx;
   public final int dy;
   public final char symbol;

   private Direction(int dy, int dx, char symbol)
   {
      this.dy = dy;
      this.dx = dx;
      this.symbol = symbol;
   }

   public Direction left()
   {
      return leftLookup.get(this);
   }

   public Direction right()
   {
      return rightLookup.get(this);
   }

   public Direction behind()
   {
      return behindLookup.get(this);
   }

   public static Direction fromSymbol(char symbol)
   {
      return symbolLookup.get(symbol);
   }
}
