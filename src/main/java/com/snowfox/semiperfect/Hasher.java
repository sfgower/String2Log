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

/** Hash a string. **/

public class Hasher implements IStringHashCoder, java.io.Serializable
 {
 public static int LEFT_DEFAULT = 3;
 public static int RIGHT_DEFAULT = 28;

 private int left = LEFT_DEFAULT;
 private int right = RIGHT_DEFAULT;
 private int rangeSize = 0;
 private int constant = 0;

 /** Generate a hash.
  @param str the string to be hashed
  @param numBuckets the number of buckets in the hash
  @param constant a constant between 1 and 255 inclusive.

  **/

 public static int hash(String str,
                        int numBuckets,
                        int constant,
                        int leftShift,
                        int rightShift)
  {

  int len = str.length();
  int hash = 0;
  char[] buffer = str.toCharArray();

  int c = 0;
  for (int i = 0; i < len; i++)
   {
   c = buffer[i] + constant;
   hash = ((hash << leftShift) + (hash >> rightShift) + c);
   }

  int sum = str.hashCode() + hash;

  int h = Math.abs((sum % numBuckets));
  if ((h < 0) || (h >= numBuckets))
   throw new Error("Hash failure: " + str + " " + numBuckets + " " + constant);
  return h;
  }

 /**
  * onstruct a hash with the given
  * range size and the given constant.
  ***/

 public Hasher(int indexSize, int constant)
  {
  this(indexSize,constant,LEFT_DEFAULT,RIGHT_DEFAULT);
  }

 public Hasher(int indexSize,int constant,int left,int right)
 {
  this.rangeSize = indexSize;
  this.constant = constant;
  this.left = left;
  this.right = right;
 }
 /**
  * Hash a string.
  * @param string
  * @return hash
  */

 public int hash(String string)
  {
  return hash(string, rangeSize, constant,left,right);
  }



    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }
 }