package com.snowfox.semiperfect.test;

/**
 * Snowfox Software. All rights reserved. 2001, 2002, 2003.

 * User: Stefan Gower
 * Date: May 27, 2003
 * Time: 1:42:18 PM

 */


import com.snowfox.semiperfect.ISemiPerfect;
import com.snowfox.semiperfect.IndexKey;
import com.snowfox.semiperfect.SemiPerfectFactory;
import com.snowfox.semiperfect.SemiPerfectMetrics;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.Date;


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


/** A JUnit test. **/


public class BigTest extends TestCase {
    public static final long MILLION = 1000000;
    public static final long COUNT = 6;
    public static final long LARGE_NUMBER = 500000;
    public static final double ACCEPTABLE_AVERAGE_LEVEL = 2.5;


    /**

     * Constructor for test case.

     */

    public BigTest(String name) {

        super(name);

    }


    /**

     * Entry point for running the

     * test from the command line.

     */


    public static void main(String[] args) {

        junit.textui.TestRunner.run(suite());

    }


    /** Set up the test. **/


    protected void setUp() {

        // do setup here...

    }


    /** Tear down the test. **/


    protected void tearDown() {

        // tear down the test here...

    }


    /** Run test test suite. **/


    public static Test suite() {
        TestSuite suite = new TestSuite(BigTest.class);

        return suite;

    }


    /** Run a basic test. **/


    public void testBig() {


        long startBig = new Date().getTime();

        SemiPerfectFactory.setDivisor(4);
        ISemiPerfect semiPerfect = SemiPerfectFactory.construct(LARGE_NUMBER);

        System.out.println(semiPerfect.getCurrentSequenceNumber());

        semiPerfect.setStringsRetained(false);


        if (semiPerfect == null)
            fail("Semiperfect index creation failed. Returned null");


        IndexKey myKey = new IndexKey(ISemiPerfect.NOT_FOUND, ISemiPerfect.NOT_FOUND);
        IndexKey myKey2 = new IndexKey(ISemiPerfect.NOT_FOUND, ISemiPerfect.NOT_FOUND);


        long position = -1;
        try {

            final long PROGRESS_REPORTING_INTERVAL = 100000;
            long j = 0;

            String string = null;
            int level = -1;

            while (position <= LARGE_NUMBER) {


                // A string is created and then the string is entered twice.
                // The numeric key returned by the first call
                // should be identical to the numeric key returned
                // by the second call.
                //

                position++;
                string = "term_" + position;
                //System.out.println(string);
                semiPerfect.enter(string, myKey);

                assertEquals(position, myKey.getSequenceNumber());
                assertEquals(position,semiPerfect.getCurrentSequenceNumber());
                semiPerfect.enter(string, myKey2);
                //System.out.println("second key="+myKey2.getCurrentSequenceNumber());

                assertEquals(position, myKey2.getSequenceNumber());

            }


            System.out.println("String entered at end == " + position);
            System.out.println("Entries in index=" + semiPerfect.getNumberOfEntries());
        } catch (OutOfMemoryError e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            fail("NO MEMORY LEFT. " + e.toString());
        }

        long insertionEnd = new Date().getTime();

        // Check that the expected number of entries were allocated.
        // Here we can just check to see if see the expected sequence
        // number.

        long end = semiPerfect.getCurrentSequenceNumber();

        assertEquals(position, end);

        SemiPerfectMetrics metrics = semiPerfect.getMetrics();
        double access = metrics.getAverageLevel();

        //
        // The number of entries in the index should
        // equal the number of strings that were inserted
        // into the index. Of course, it is statistically
        // possible that a string will match against another
        // checksum. But this is statistically unlikely,
        // as two strings must have two identical hashes
        // at the same level in the semiperfect index.
        //

        // assertEquals(LARGE_NUMBER,sp.getNumberOfEntries());


        double averageLevel = metrics.getAverageLevel();
        System.out.println("average level=" + averageLevel);
        int retention = metrics.getNumberOfRetainedStrings();
        System.out.println("retained strings=" + retention);
        assertEquals(0, retention);

        assertTrue(metrics.getAverageLevel() < ACCEPTABLE_AVERAGE_LEVEL);

        System.out.println();
        System.out.println();
        System.out.println(metrics.getReport());



        // Here we convert the index from writable to
        // read-only.
        //
        // Once converted, we now generate a large number of
        // strings that were NEVER entered into the index.
        //
        // Since the strings were never entered into the index,
        // the index should not recognize these strings.
        // When a string is not recognized, a -1 will be returned.
        //
        // So while we cannot prove that the index will never
        // recognize a string that is not in the index, we
        // can accumulate evidence that strongly suggests
        // that the index behaves properly. That is, if it
        // is given strings that it has not consumed, it will
        // not recognize that string. And that is exactly the
        // behavior we want.
        //

        int j = (int) LARGE_NUMBER + 1;
        semiPerfect.setWritable(false);
        String stringThatShouldNotBeFoundInTheIndex = null;
        for (; j < (5 * LARGE_NUMBER); j++) {
            stringThatShouldNotBeFoundInTheIndex = new Integer(j).toString();
            if (-1 != semiPerfect.enter(stringThatShouldNotBeFoundInTheIndex)) {
                fail("Found " + stringThatShouldNotBeFoundInTheIndex + " but should *not* have found it.");
            }

        }

        long bigEnd = new Date().getTime();
        long insertionDuration = insertionEnd - startBig;
        System.out.println("insertion duration: " + insertionDuration);
        long duration = bigEnd - startBig;
        System.out.println("duratation of big run: " + duration);

    }


}
