package com.snowfox.semiperfect;

/*

Copyright 2017 [Stefan Gower]

        Licensed under the Apache License, Version 2.0 (the "License");
        you may not use this file except in compliance with the License.
        You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

        Unless required by applicable law or agreed to in writing, software
        distributed under the License is distributed on an "AS IS" BASIS,
        WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        See the License for the specific language governing permissions and
        limitations under the License.
*/

/**
 * Provides metrics on a semiperfect
 * index.
 */

public class SemiPerfectMetrics {
    private double averageLevel = 0;
    int retainedStrings = 0;
    private String report = null;

    public SemiPerfectMetrics(SemiPerfect sp) {
        averageLevel = computeAverageLevel(sp);
        retainedStrings = sp.numberOfRetainedStrings();
        initReport(sp);
    }

    /**
     * Get the average level of an index entry.
     * @return average level
     */

    public double getAverageLevel() {
        return averageLevel;
    }

    /**
     * Get the number of retained strings.
     * @return number of strings retained in the index
     */

    public int getNumberOfRetainedStrings() {
        return retainedStrings;
    }

    /**
     * Compute the average level of
     * an index entry.
     * @param sp
     * @return average level
     */

    public double computeAverageLevel(SemiPerfect sp) {
        int numberOfLevels = sp.getNumberOfLevels();
        int totalEntries = 0;
        int totalAdjustedWeight = 0;
        int entriesAtLevel;
        int adjustedWeight;

        for (int k = 0; k < numberOfLevels;  k++) {
            entriesAtLevel = sp.getSubIndex(k).getNumberOfEntries();
            totalEntries = totalEntries + entriesAtLevel;

            // The weight of an entry is always multiplied
            // by its level. But we have to add one to the
            // the level because Java array levels start at zero.

            adjustedWeight = entriesAtLevel * (k + 1);

            totalAdjustedWeight = totalAdjustedWeight + adjustedWeight;
        }


        // avoid a divide by zero error if the index is empty...

        if (totalEntries == 0)
          return 0;

        // Now we simply divide the total number of entries

        double d = ((double) totalAdjustedWeight) / ((double) totalEntries);
        return d;
    }

    public void initReport(ISemiPerfect pin)
    {
     SemiPerfect p = (SemiPerfect)pin;
     StringBuffer sb = new StringBuffer();


     int entriesEntered = pin.getNumberOfEntries();
     sb.append("Total entries="+entriesEntered);
     sb.append("\n");
     sb.append("Distributions");
     sb.append("\n");

  for (int k = 0; k <  p.getNumberOfLevels() ; k++)
  {
   IComponentIndex comp = p.getSubIndex(k);
   sb.append("Level#"+ (k + 1));
   sb.append("\n");
   sb.append(" ");
   sb.append("entries="+comp.getNumberOfEntries());
   sb.append("\n");
   sb.append(" ");
   sb.append("index size="+comp.getIndexSize());
   sb.append("\n");
   sb.append(" ");
   double usage = 0;
   if (comp.getNumberOfEntries() != 0)
      usage = ((double)comp.getNumberOfEntries()) /
              ((double)comp.getIndexSize()) ;
   sb.append("utilization="+usage);
   sb.append("\n");
   sb.append(" ");
   sb.append("strings entered at this level=");
   sb.append(entriesEntered);
   sb.append("\n");
   double percentConsumed =  ((double)comp.getNumberOfEntries()) /
           ((double)entriesEntered);
   sb.append(" ");
   sb.append("percent consumed="+percentConsumed);
   sb.append("\n");
   entriesEntered = entriesEntered - comp.getNumberOfEntries();
  }
   report = sb.toString();
    }

   /**
    * Get a report describing the state of the semiperfect index
    * and its component levels.
    * @return string
    */
   public String getReport()
   {
    return report;
   }


}
