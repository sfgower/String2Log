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


/** The semi-perfect subindex.
 *  @author Stefan Gower
 */

public interface IComponentIndex
 {
 /** Get the next subindex. **/

 IComponentIndex getNextSubIndex();

 /** Set the next subindex. **/

 void setNextSubIndex(IComponentIndex subIndex);

 /** Enter a string, returning the semikey. **/

 IndexKey enter(String string, IndexKey snowKey, SequenceGenerator sgen);

 /**
  * Make the subIndex writable.
  *
  * **/

 void setWritable(boolean flag);

 /**
  * Return the number
  * of entries in the subindex
  ***/

 int getNumberOfEntries();

 /** Get the next string. **/

 String getString(IndexKey snowKey);

 /** Returns true if the retention
  *  flag is set to true
  ***/

 boolean areStringsRetained();

 /**
  * Set retention flag.
  ***/

 void setRetention(boolean retentionFlag);

 /**
  * Returns true if this is
  * the last subindex in the semiperfect hash.
  * @return boolean
  */

 boolean isLast();

 /**
  * Get the number of items
  * this subindex can hold
  * @return size
  */

 int getIndexSize();

 /**
  * The semiperfect index can retain strings
  * during its construction.
  * <P>
  * It may then be desirable (to save space)
  * to remove all these retained strings.
  * <P>
  * This method clears all such retained strings,
  * thus saving space.
  */

 void clearStrings();
 }