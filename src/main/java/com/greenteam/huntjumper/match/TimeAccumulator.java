package com.greenteam.huntjumper.match;

/**
 * Slices time in same pieces for different CPUs
 */
public class TimeAccumulator
{
   public static final int CYCLE_LENGTH = 10;
   private int totalCyclesCount = 0;
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

   public int update(int delta)
   {
      accumulator += delta;
      int cycles = accumulator / cycleLength;
      totalCyclesCount += cycles;
      accumulator %= cycleLength;
      return cycles;
   }

   public int getCycleLength()
   {
      return cycleLength;
   }
   
   public int getTotalTimeInMilliseconds()
   {
      return totalCyclesCount * cycleLength + accumulator;
   }

   public int getAccumulatorValue()
   {
      return accumulator;
   }

   public void reset()
   {
      totalCyclesCount = 0;
      accumulator = 0;
   }
}
