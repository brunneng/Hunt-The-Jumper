package com.greenteam.huntjumper;

/**
 * Slices time in same pieces for different CPUs
 */
public class TimeAccumulator
{
   public static final int CYCLE_LENGTH = 10;
   private int accumulator = 0;
   
   private int cycleLength;
   public TimeAccumulator()
   {
      cycleLength = CYCLE_LENGTH;
   }

   public TimeAccumulator(int cycleLength)
   {
      this.cycleLength = cycleLength;
   }

   public int cycles(int delta)
   {
      accumulator += delta;
      int cycles = accumulator / cycleLength;
      accumulator %= cycleLength;
      return cycles;
   }

   public int getCycleLength()
   {
      return cycleLength;
   }
}
