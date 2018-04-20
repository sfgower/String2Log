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
import com.snowfox.semiperfect.SemiPerfectFactory;
import com.snowfox.semiperfect.SemiPerfectMetrics;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;


/** A JUnit test. **/


public class MediumTest extends TestCase
 {
  public static final long MILLION = 1000000;
  public static final long COUNT = 6;
  public static final long LARGE_NUMBER = COUNT * MILLION;
  public static final double ACCEPTABLE_AVERAGE_LEVEL = 2.5;


 /**

  * Constructor for test case.

  */

 public MediumTest(String name)

  {

  super(name);

  }


 /**

  * Entry point for running the

  * test from the command line.

  */


 public static void main(String[] args)

  {

  junit.textui.TestRunner.run(suite());

  }


 /** Set up the test. **/


 protected void setUp()

  {

  // do setup here...

  }


 /** Tear down the test. **/


 protected void tearDown()

  {

  // tear down the test here...

  }


 /** Run test test suite. **/


 public static Test suite()
  {
  TestSuite suite = new TestSuite(MediumTest.class);

  return suite;

  }


 /** Run a basic test. **/





 public void testBig()
  {
  System.out.println("entering test creation test.");
  final long sinSize = 250000;

  SemiPerfectFactory.setDivisor(4);
  ISemiPerfect semiperfect = SemiPerfectFactory.construct(sinSize);
  semiperfect.setStringsRetained(false);

  if (semiperfect == null)
   fail("Semiperfect index creation failed. Returned null");

  try
   {
   long counter = 0;
   final long CHUNK = 100000;
   long j = 0;

   String s = null;
   int level = -1;
   int limit = (int)sinSize;
   while (counter < limit)
    {
    j++;
    if (j > CHUNK)
     {
     j = 0;



     System.out.println("Strings     = " + counter);
     System.out.println("count="+counter);
     }

     // A check that the index can be probed to determine if a matching entry
     // exists or not.
     //
     // Note: oddly enough, the semiperfect index did not allow for this!
     // So there was no way to actually just check that a string in the
     // index without a side-effect!

     long currentSequenceNumber = semiperfect.getCurrentSequenceNumber();
     assertFalse(semiperfect.hasString("xxxyyyzzzwwww"));
     long currentSequenceNumber2 = semiperfect.getCurrentSequenceNumber();
     assertEquals(currentSequenceNumber,currentSequenceNumber2);



    // The string is entered twice.
    // The numeric key returned by the first call
    // should be identical to the numeric key returned
    // by the second call.
    //
    // The first call
    //



    s = String.valueOf(Long.toHexString(counter));

    int key = semiperfect.enter(s);
    assertTrue(key != ISemiPerfect.NOT_FOUND);
    int key2 = semiperfect.enter(s);
    assertTrue(key2 != ISemiPerfect.NOT_FOUND);
    assertTrue(key == key2);
    counter++;
    }



   System.out.println("String entered at end == " + counter);
   System.out.println("Entries in index="+semiperfect.getNumberOfEntries());
   }
  catch (OutOfMemoryError e)
   {
   e.printStackTrace();  //To change body of catch statement use Options | File Templates.
   fail("NO MEMORY LEFT. " + e.toString());
   }

  SemiPerfectMetrics metrics = semiperfect.getMetrics();
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
  //SnowTrace.println("access = " + access);

  double averageLevel = metrics.getAverageLevel();
  System.out.println("average level="+averageLevel);
  int retention = metrics.getNumberOfRetainedStrings();
  System.out.println("retained strings=" + retention);
  assertEquals(0,retention);

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

  int j = (int)sinSize + 1;
  semiperfect.setWritable(false);
  String stringThatShouldNotBeFoundInTheIndex = null;
  for (; j < (5 * sinSize); j ++ )
   {
    stringThatShouldNotBeFoundInTheIndex = new Integer(j).toString();
    if (-1 != semiperfect.enter(stringThatShouldNotBeFoundInTheIndex))
     {
      //SnowTrace.println("Eeks! Found unfound thing: " + stringThatShouldNotBeFoundInTheIndex);
      fail("Found " + stringThatShouldNotBeFoundInTheIndex + " but should *not* have found it.");
     }

   }

 }







 }
