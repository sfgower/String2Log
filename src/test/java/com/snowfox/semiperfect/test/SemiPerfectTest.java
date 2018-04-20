package com.snowfox.semiperfect.test;

/**
 * Snowfox Software. All rights reserved. 2001, 2002, 2003.

 * User: Stefan Gower
 * Date: Jun 3, 2003
 * Time: 3:51:59 PM

 */


import com.snowfox.semiperfect.ISemiPerfect;
import com.snowfox.semiperfect.SemiPerfect;
import com.snowfox.semiperfect.SemiPerfectFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//import com.snowfox.server.store.structuredfile.test.auxiliary.RandomWordGenerator;


/** A JUnit test. **/


public class SemiPerfectTest extends TestCase {


    public static int HUMONGOUS = 10000000;
    public static int MILLION = 1000000;
    public static int VERY_BIG = MILLION;
    public static int BIG = 300000;
    public static int TEN_THOUSAND = 10000;

    /**

     * Constructor for test case.

     */

    public SemiPerfectTest(String name) {

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

        TestSuite suite = new TestSuite(SemiPerfectTest.class);

        return suite;

    }


    /** Run a basic test. **/


    public void testBasicWithoutRetaintingStrings()
            throws Exception {

        SemiPerfectFactory.setRetainStrings(false);
        exec(VERY_BIG);
    }

    public void testIndexWithRetainedStrings() {
        SemiPerfectFactory.setRetainStrings(true);
        exec(BIG);
    }


    /**
     * Here we first train the index with a million strings,
     * and then we toss one hundred millions strings at it
     * that it should *NOT* recognize. If it recognizes any
     * such string, it is an error.
     * <P>
     * This test pivots on a simple idea. We have a random
     * word generator which can be configured to generate
     * strings within different length ranges (think min and max
     * string length).
     * <P>
     * By adjusting these random word generators, we can make
     * sure that the strings of one will never intersect with
     * the other, and it is this fact that allows us to generate
     * a lot of random strings that should not be recognized.
     */

    public void testBigFeed() {
        SemiPerfectFactory.setRetainStrings(false);
        int testSize = 1 * MILLION;
        ISemiPerfect sp = SemiPerfectFactory.construct(new int[]{(int) (testSize * 1.2), 50000, 500});

        RandomWordGenerator wordGenerator = new RandomWordGenerator();
        wordGenerator.setMinLen(2);
        wordGenerator.setMaxLen(8);


        String word = null;
        for (int k = 0; k < testSize; k++) {
            word = wordGenerator.randomWord();

            sp.enter(word);

        }

        sp.setWritable(false);

        // This random word generator will always generate words
        // of greater length than the first random word generator,
        // so none of these random words should be recognized.

        wordGenerator = new RandomWordGenerator();
        wordGenerator.setMinLen(9);
        wordGenerator.setMaxLen(20);

        int attempts = testSize * 100;
        long p = 0;
        for (int k = 0; k < attempts; k++) {
            word = wordGenerator.randomWord();

            p = sp.enter(word);
            assertTrue(p < 0);
        }


    }

    public void exec(int testSize) {


        List recog = new ArrayList(testSize);
        ISemiPerfect sp = SemiPerfectFactory.construct(new int[]{(int) (testSize * 1.2), 50000, 500});
        SemiPerfect spImpl = (SemiPerfect) sp;

        int levels = spImpl.getNumberOfLevels();
        //spImpl.getLastIndex().setRetention(true);
        System.out.println("levels = " + levels);

        RandomWordGenerator wordGenerator = new RandomWordGenerator();
        wordGenerator.setMinLen(2);
        wordGenerator.setMaxLen(12);


        String word = null;
        for (int k = 0; k < testSize; k++) {
            word = wordGenerator.randomWord();

            sp.enter(word);
            recog.add(word);
        }

        sp.setWritable(false);


        int failures = 0;
        int p = 0;
        int q = 0;
        for (int k = 0; k < testSize; k++) {
            word = (String) recog.get(k);
            p = sp.enter(word);
            if (p < 0)
                failures++;
            q = sp.enter(word);
            assertEquals(p, q);
        }

        assertEquals(0, failures);

        int size = sp.getNumberOfEntries();
        System.out.println("Number of random word generation attempts: " + testSize);
        System.out.println("size = " + size);

        assertTrue((size > (testSize / 2)));


        Collections.sort(recog);


        System.out.println("first feed");
        feed(sp, 10000, 2, 3, recog);
        System.out.println("second feed");
        feed(sp, 10000, 3, 4, recog);
        System.out.println("third feed");
        feed(sp, 100000, 4, 5, recog);
        System.out.println("fourth feed");
        feed(sp, 50 * MILLION, 3, 10, recog);


    }

    public boolean isKnown(List recog, String word) {

        int p = Collections.binarySearch(recog, word);
        return p > -1;
    }

    /**
     * We should only recognize words that we became
     * part of the index when it accepting new strings.
     * Now the index has been 'closed', so no new strings
     * shoould be accepted. The index should only
     * accept strings that it accepted before.
     *
     * @param sp
     * @param limit
     * @param minLen
     * @param maxLen
     * @param recog
     */

    public void feed(ISemiPerfect sp,
                     int limit,
                     int minLen,
                     int maxLen,
                     List recog) {

        RandomWordGenerator wordGenerator = new RandomWordGenerator();
        wordGenerator.setMinLen(minLen);
        wordGenerator.setMaxLen(maxLen);

        String word = null;
        int chunkSize = 50000;
        int m = 0;

        long n;
        long s;
        for (int j = 0; j < limit; j++) {
            word = wordGenerator.randomWord();
            n = sp.enter(word);
            if (n > 0) {
                if (!isKnown(recog, word)) {
                    fail("index has recognized *unknown* word: " + word);
                }
            }
            if (m == chunkSize) {
                m = 0;
                System.out.println("processed " + j);
            }
            m++;
        }

    }
}

class RandomWordGenerator {

    public static final int DEFAULT_MIN_LEN = 2;
    public static final int DEFAULT_MAX_LEN = 25;

    public static String CHARS[] =
            {"a", "b", "c", "d", "e", "f", "g", "h", "i",
                    "k", "l", "m", "n", "o", "p", "q", "r",
                    "s", "t", "u", "v", "w", "x", "y", "z"};

    public static final int CHARS_LEN = CHARS.length;
    public static final int CHARS_LEN_MINUS_ONE = CHARS_LEN - 1;

    private Random r = new Random(77);
    private int minLen = DEFAULT_MIN_LEN;
    private int maxLen = DEFAULT_MAX_LEN;

    public RandomWordGenerator() {
    }

    public RandomWordGenerator(int seed) {
        this.r = new Random(seed);
    }



    public String randomWord() {
        int len = randomLength();
        StringBuffer sb = new StringBuffer();
        for (int k = 0; k < len; k++)
            sb.append(this.randomChar());
        return sb.toString();
    }

    public int randomLength() {
        int len = r.nextInt(maxLen);
        if (len < minLen)
            return minLen;
        else
            return len;
    }

    public String randomChar() {

        int k = r.nextInt(CHARS_LEN_MINUS_ONE);
        return CHARS[k];
    }

    public int getMaxLen() {
        return maxLen;
    }

    public void setMaxLen(int maxLen) {
        this.maxLen = maxLen;
    }

    public int getMinLen() {
        return minLen;
    }

    public void setMinLen(int minLen) {
        this.minLen = minLen;
    }

}