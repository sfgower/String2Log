package com.snowfox.semiperfect;

import java.util.ArrayList;
import java.util.Iterator;

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


/** Construct a snow semiperfect index.
 * @author Stefan Gower.
 */

// todo - convert into the preferred approach of having
// a factory instance...

public class SemiPerfectFactory {

    public static final boolean RETAIN_STRINGS_DEFAULT = false;

    private static boolean retainStrings = retainStrings = RETAIN_STRINGS_DEFAULT;
    private static double DEFAULT_BUFFER = 0.25d;
    private static int CUT_OFF = 2048;
    private static int DIVISOR = 4;
    public static double BUFFER_FACTOR = DEFAULT_BUFFER;


    /** Construct a semiperfect index, configuring
     *  the internal subindices using the dimension data. **/

    public static ISemiPerfect construct(int[] dimensions) {
        return new SemiPerfect(dimensions,RETAIN_STRINGS_DEFAULT);
    }


    /**
     * Construct an index with the given dimensions.
     * The retainStrings flag indicates whether the
     * index will retain strings or not.
     * @param dimensions
     * @param retainStrings
     * @return semiperfect index
     */

    public static ISemiPerfect construct(int[] dimensions, boolean retainStrings) {
        return new SemiPerfect(dimensions, retainStrings);
    }

    public static ISemiPerfect construct(long capacity, boolean retainStrings) {
        long additionalCapacity = (long) (BUFFER_FACTOR * capacity);
        long adjustedCapacity = capacity + additionalCapacity;
        int dim[] = estimateDimensions(adjustedCapacity);
        return construct(dim, retainStrings);
    }

    /**
     * Construct a semi-semiperfect index based on
     * its expected maximum capacity.
     *
     * @param capacity the maximum number of strings expected
     * @return semiperfect index
     */

    public static ISemiPerfect construct(long capacity) {
        return construct(capacity, false);
    }


    /**
     * Using heuristics, estimate the number of
     * component indices needed, along with the size
     * of each component.
     * @param count
     * @return array of sizes
     */

    public static int[] estimateDimensions(long count) {
        ArrayList list = new ArrayList();
        int currentArraySize = (int) (count + count);
        while (currentArraySize >= CUT_OFF) {
            list.add(new Integer(currentArraySize));
            currentArraySize = (int) currentArraySize / DIVISOR;
        }
        int dimensions[] = new int[list.size()];
        int j = 0;
        Iterator scan = list.iterator();
        while (scan.hasNext()) {
            Integer someInteger = (Integer) scan.next();
            dimensions[j++] = someInteger.intValue();
        }
        return dimensions;
    }

    /**
     * Estimate the total array capacity.
     * @param dimensions
     * @return capacity
     */

    public static int estimateTotalArrayCapacity(int dimensions[]) {
        int sum = 0;
        for (int k = 0; k < dimensions.length; k++)
            sum = sum + dimensions[k];
        return sum;
    }

    static public void setBuffer(double percent) {
        BUFFER_FACTOR = percent;
    }

    static public double getBuffer() {
        return BUFFER_FACTOR;
    }


    static public void setCutoff(int cutoff) {
        CUT_OFF = cutoff;
    }

    static public int getCutoff() {
        return CUT_OFF;
    }

    static public int getDivisor() {
        return DIVISOR;
    }

    static public void setDivisor(int divisor) {
        DIVISOR = divisor;
    }

    public static boolean isRetainStrings() {
        return retainStrings;
    }

    public static void setRetainStrings(boolean retainStrings) {
        SemiPerfectFactory.retainStrings = retainStrings;
    }
}