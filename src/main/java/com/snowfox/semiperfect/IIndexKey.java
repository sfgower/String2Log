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

/** A semi-key is named because it is a key
 *  to a semi-perfect hash.
 *
 *  @author Stefan Gower
 */

public interface IIndexKey
 {


 /** Get the position. **/

 int getPosition();

 /** Set the position of the key. **/

 void setPosition(int position);

 /** Get the level of the key. **/

 int getLevel();

 /** Set the level of the key. **/

 void setLevel(int level);

 String toString();

 /** Two keys are equal if they have
  the same level and the same position. **/

 boolean equals(Object other);

 /** Returns true if the snowkey has a leve
  and a position, other than -1. **/

 boolean isValid();

 /** Returns true if the key is invalid;
  that is, its level or position is equal to -1.
  **/

 boolean isInvalid();

 /** Create a copy of this key. **/

 IIndexKey copy();

 /** Set the properties that indicate that a key is invalid. **/

 void invalidate();
 }