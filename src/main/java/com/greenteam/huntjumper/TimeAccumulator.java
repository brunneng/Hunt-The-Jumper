package com.greenteam.huntjumper;

/**
 * Slices time in same pieces for different CPUs
 */

public class TimeAccumulator {
   public static int CYCLE_LENGTH = 10;

   private int accumulator = 0;

   public int cycles(int delta)
   {
      accumulator += delta;
      int cycles = accumulator / CYCLE_LENGTH;
      accumulator %= CYCLE_LENGTH;
      return cycles;   
   }
}
