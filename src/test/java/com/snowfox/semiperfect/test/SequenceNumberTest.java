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

import java.util.Random;


/** A JUnit test. **/


public class SequenceNumberTest extends TestCase
 {


 /**

  * Constructor for test case.

  */

 public SequenceNumberTest(String name)

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

  TestSuite suite = new TestSuite(SequenceNumberTest.class);

  return suite;

  }


 /**
  * Unit test for semiperfect...
  */


 public void testWithStrings()

  {
   ISemiPerfect sp = SemiPerfectFactory.construct(100000,true);


   sp.setWritable(true);
   sp.setStringsRetained(true);
   IndexKey key = new IndexKey(ISemiPerfect.NOT_FOUND,ISemiPerfect.NOT_FOUND);
   IndexKey key2 = new IndexKey(ISemiPerfect.NOT_FOUND,ISemiPerfect.NOT_FOUND);

   int limit = 90000;
   long start = sp.getCurrentSequenceNumber();
   assertEquals(SequenceGenerator.START_VALUE,start);
   String string;
   Random random = new Random(19);
   int num;
   for (int k = 0; k <= limit; k++)
   {
    num = random.nextInt();
    string = "string_" + k + "_" + String.valueOf(num);
    sp.enter(string,key);
    assertEquals(k,key.getSequenceNumber());
    sp.enter(string,key2);
    assertEquals(k,key2.getSequenceNumber());
   }

   long end = sp.getCurrentSequenceNumber();
   assertEquals(limit,end);
  }


  public void testWithoutStrings()

  {
   ISemiPerfect sp = SemiPerfectFactory.construct(25000,false);


   sp.setWritable(true);
   sp.setStringsRetained(false);
   IndexKey key = new IndexKey(ISemiPerfect.NOT_FOUND,ISemiPerfect.NOT_FOUND);
   IndexKey key2 = new IndexKey(ISemiPerfect.NOT_FOUND,ISemiPerfect.NOT_FOUND);

   int limit = 1000;
   long start = sp.getCurrentSequenceNumber();
   assertEquals(SequenceGenerator.START_VALUE,start);
   String string;
   int count = 0;
   int chunk = 100;
   for (int k = 0; k <= limit; k++)
   {
    if (count >= chunk)
    {
     chunk = 0;
     System.out.println(k);
    }
    string = "string_" + k;
    sp.enter(string,key);
    assertEquals(k,key.getSequenceNumber());
    sp.enter(string,key2);
    assertEquals(k,key2.getSequenceNumber());
   }

   long end = sp.getCurrentSequenceNumber();
   assertEquals(limit,end);







  }


 }
