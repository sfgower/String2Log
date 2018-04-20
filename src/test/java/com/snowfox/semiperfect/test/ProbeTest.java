package com.snowfox.semiperfect.test;

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



import com.snowfox.semiperfect.ISemiPerfect;
import com.snowfox.semiperfect.IndexKey;
import com.snowfox.semiperfect.SemiPerfectFactory;
import com.snowfox.semiperfect.SequenceGenerator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/** A JUnit test. **/


public class ProbeTest extends TestCase {


    /**

     * Constructor for test case.

     */

    public ProbeTest(String name) {

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

        TestSuite suite = new TestSuite(ProbeTest.class);

        return suite;

    }


    /**
     * Test probing for terms...
     * <P>
     * Originally, entering a term would always return a position
     * in the index. But that was a problem, because in some
     * cases, one wanted to determine if a string was
     * in the index!
     */

    public void testProbingForTerm() {
        ISemiPerfect sp = SemiPerfectFactory.construct(100000, true);
        sp.setWritable(true);
        sp.setStringsRetained(true);
        assertTrue(sp.getWritable());
        String string = "abc";
        IndexKey indexKey = new IndexKey(-1, -1);
        indexKey.setProbe(true);
        assertEquals(0, sp.getNumberOfEntries());
        assertEquals(SequenceGenerator.START_VALUE,sp.getCurrentSequenceNumber());

        sp.enter(string, indexKey);
        assertTrue(indexKey.isInvalid());
        assertEquals(SequenceGenerator.START_VALUE,sp.getCurrentSequenceNumber());

        indexKey.setProbe(false);
        sp.enter(string, indexKey);
        assertFalse(indexKey.isInvalid());
        assertEquals(0,indexKey.getSequenceNumber());
        assertEquals(0,sp.getCurrentSequenceNumber());

        indexKey.setProbe(true);
        sp.enter(string, indexKey);
        assertFalse(indexKey.isInvalid());
        assertEquals(0,indexKey.getSequenceNumber());
        assertEquals(0,sp.getCurrentSequenceNumber());
    }




}
