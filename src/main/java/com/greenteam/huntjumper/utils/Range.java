package com.greenteam.huntjumper.utils;

/**
 * User: GreenTea Date: 29.01.12 Time: 16:31
 */
public class Range
{
   public final float start;
   public final boolean includeStart;

   public final float end;
   public final boolean includeEnd;

   public Range(float start, boolean includeStart, float end, boolean includeEnd)
   {
      if (start > end)
      {
         throw new IllegalArgumentException("start should be <= then end in Range");
      }

      this.start = start;
      this.includeStart = includeStart;

      this.end = end;
      this.includeEnd = includeEnd;
   }

   public Range(float start, float end)
   {
      this(start, true, end, true);
   }

   public boolean contains(float value)
   {
      boolean moreThenStart = includeStart ? (value > start || Utils.equals(start, value)) :
              (value > start && !Utils.equals(start, value));
      return moreThenStart && (includeEnd ? (value < end || Utils.equals(end, value)) :
              (value < end || !Utils.equals(end, value)));
   }

   public boolean contains(Range other)
   {
      return contains(other.start) && contains(other.end);
   }
   
   public Range intersection(Range other)
   {
      boolean containsStart1 = contains(other.start);
      boolean containsEnd1 = contains(other.end);

      if (containsStart1 && containsEnd1)
      {
         return other;
      }

      boolean containsStart2 = other.contains(start);
      boolean containsEnd2 = other.contains(end);

      if (containsStart2 && containsEnd2)
      {
         return this;
      }

      if (!containsStart1 && !containsEnd1 && !containsStart2 && !containsEnd2)
      {
         return null;
      }

      float resStart = 0;
      boolean resIncludeStart = false;
      float resEnd = 0;
      boolean resIncludeEnd = false;

      if (containsStart1)
      {
         resStart = other.start;
         resIncludeStart = other.includeStart;

         if (containsStart2)
         {
            resIncludeStart = other.includeStart && includeStart;
         }
      }
      else if (containsStart2)
      {
         resStart = start;
         resIncludeStart = includeStart;
      }

      if (containsEnd1)
      {
         resEnd = other.end;
         resIncludeEnd = other.includeEnd;

         if (containsEnd2)
         {
            resIncludeEnd = other.includeEnd && includeEnd;
         }
      }
      else if (containsEnd2)
      {
         resEnd = end;
         resIncludeEnd = includeEnd;
      }



      return new Range(resStart, resIncludeStart, resEnd, resIncludeEnd);
   }
}
