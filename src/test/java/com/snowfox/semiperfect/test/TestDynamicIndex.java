package com.snowfox.semiperfect.test;

/**
 * 
 *
 * User: Stefan Gower
 * Date: Jul 16, 2003
 * Time: 3:40:09 AM
 *
 */


import com.snowfox.semiperfect.DynamicIndex;
import com.snowfox.semiperfect.ISemiPerfect;
import com.snowfox.semiperfect.IndexKey;
import com.snowfox.semiperfect.SequenceGenerator;
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


public class TestDynamicIndex extends TestCase
 {


 /**

  * Constructor for test case.

  */

 public TestDynamicIndex(String name)

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

  TestSuite suite = new TestSuite(TestDynamicIndex.class);

  return suite;

  }


 /**
  * Unit test for a hashing component
  * that maps a string via a hash function.
  * The string is then hashed again, hence
  * a double hash is involved.
  * <P>
  * What somewhat complicates matters is
  * that the value of map entry (key->value)
  * is either a single record or a list.
  * <P>
  * A list is only used if two or more records
  * are inserted using the same key. This is
  * to save space, as otherwise there might
  * be many lists with only a single record
  * in the list... which would not be efficient.
  * <P>
  * October, 2009 Revision: added in assertions
  * for sequence numbers. Now the semiperfect hash
  * will produce not only a unique number a sequential
  * number that will identify the index entry. This is
  * being done to allow significant speedup in other
  * areas, such as structured file handling and runtime
  * categorization.
  */


 public void testBasic()

  {
   SequenceGenerator sgen = new SequenceGenerator();
   DynamicIndex index = new DynamicIndex(1000,0,false);
   index.setRetention(true);
   index.setWritable(true);
   IndexKey key = new IndexKey(ISemiPerfect.NOT_FOUND,ISemiPerfect.NOT_FOUND);
   IndexKey key2 = new IndexKey(ISemiPerfect.NOT_FOUND,ISemiPerfect.NOT_FOUND);
   key = index.enter("abc",key,sgen);
   assertNotNull(key);
   assertEquals(0,key.getSequenceNumber());
   String s = index.getString(key);
   assertEquals("abc",s);
   assertEquals(1,index.getNumberOfEntries());
   key2 = index.enter("abc",key2,sgen);
   assertEquals(key,key2);
   assertEquals(0,key2.getSequenceNumber());

   key = index.enter("def",key,sgen);
   assertNotNull(key);
   assertEquals(1,key.getSequenceNumber());

   s = index.getString(key);
   assertEquals("def",s);
   assertEquals(2,index.getNumberOfEntries());
   key2 = index.enter("def",key2,sgen);
   assertNotNull(key2);
   assertEquals(1,key2.getSequenceNumber());
   assertEquals(key,key2);


    key = index.enter("ghi",key,sgen);
    assertNotNull(key);
    assertEquals(2,key.getSequenceNumber());



  }




 }
