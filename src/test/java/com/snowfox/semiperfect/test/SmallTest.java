package com.snowfox.semiperfect.test;

/**
 * 
 *
 * User: Stefan Gower
 * Date: Jul 16, 2003
 * Time: 3:40:09 AM
 *
 */


import com.snowfox.semiperfect.ISemiPerfect;
import com.snowfox.semiperfect.SemiPerfectFactory;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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


public class SmallTest extends TestCase
 {


 /**

  * Constructor for test case.

  */

 public SmallTest(String name)

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

  TestSuite suite = new TestSuite(SmallTest.class);

  return suite;

  }


 /**
  * Unit test for semiperfect...
  */


 public void test()

  {
   ISemiPerfect sp = SemiPerfectFactory.construct(100000,true);


   sp.setWritable(true);
   sp.setStringsRetained(true);
   assertTrue(sp.getWritable());
   String entry = "abc";
   assertEquals(0,sp.getNumberOfEntries());
   int key = sp.enter(entry);
   assertFalse(key == ISemiPerfect.NOT_FOUND);
   assertEquals(1,sp.getNumberOfEntries());
   String echo = sp.getString(key);
   assertNotNull(echo);
   assertEquals(echo,entry);
   String entry2 = "def";
   key = sp.enter(entry2);
   assertFalse(key == ISemiPerfect.NOT_FOUND);
   echo = sp.getString(key);
   assertNotNull(echo);
   assertEquals(echo,entry2);
   assertEquals(2,sp.getNumberOfEntries());
   sp.setWritable(false);
   String entry3 = "ghi";
   key = sp.enter(entry3);
   assertEquals(ISemiPerfect.NOT_FOUND,key);
   assertEquals(2,sp.getNumberOfEntries());
   sp.setWritable(true);
   key =sp.enter(entry3);
   assertFalse(ISemiPerfect.NOT_FOUND==key);
   assertEquals(3,sp.getNumberOfEntries());
   String string3 = sp.getString(key);
   assertNotNull(string3);
   assertEquals(entry3,string3);






  }




 }
