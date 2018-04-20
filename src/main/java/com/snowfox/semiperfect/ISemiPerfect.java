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


/**
 * This is the interface of a semiperfect index.
 * The whole purpose of the semiperfect index
 * is to map a string to an absolute position
 * in some numeric range.
 * <P>
 * This mapping allows a string to be converted
 * into a number. This number acts both as a compact
 * identifier. For example, a document can be, in this
 * way, converted into nothing more than a sequence
 * of numbers.
 * <P>
 * This conversion also allows for faster runtime operations.
 * Why?
 * <P>
 * Some text processing programs may be able to execute much
 * more efficiently by first converting text into numbers.
 * In such cases, this index can be very helpful in respect
 * in both memory consumption and processing speed.
 *
 @author Stefan Gower.

 **/

// Issues
//
//   - the compututation for the index sizes probably needs adjustment
//   - the index *still* retains strings.
//
//     In principle, the retention of single string
//     is just still wrong. We really won't want to the index
//     to retain any strings at runtime. So we need an alternate
//     way to recognize strings when no match is found
//     in the other subindices.... so the search for a match
//     finally reaches the last index.
//
//     At present, this last subindex is special because it contains
//     strings. A lower number, to be sure. But there are strings there.
//
//     This re-implementation of the last index
//     can be done through use of checksums again.
//     That is, we can create a map which contains entries
//     with checksums. Here, if an entry already exists, no
//     problem... we just add the entry to the list.
//
//   So here we still have our original sieve-based approach.
//   Each subindex acts as a sieve.
//
//   If a search for a match fails, it falls through.
//   But a lot of strings are caught...
//
//   And that is why the index is quite efficient.
//
//   So here is a recap: if an entry matches, we convert the string
//   into a number quickly. If it does not, the search just falls
//   through to the next subindex. Eventually, the search can reach
//   the final subindex which has flexible capacity. That is,
//   this last subindex can always grow and take more and more entries.
//   But this last index is also slower, as its implementation
//   involves a flexibily sized map,
//   rather than an array of numbers.
//

public interface ISemiPerfect {

    int NOT_FOUND = -1;
    int FOUND_FLOOR = 0;


    /**
     * Get a flag that indicates whether
     * the index is writable, or is in read-only
     * mode.
     *
     * @return boolean
     */

    boolean getWritable();


    /**
     * Set a flag that determines whether
     * the index is writable or in read-only
     * mode.
     * @param flag
     */

    void setWritable(boolean flag);

    /** Enter a string into the semiperfect index. If the
     indexes are writable, this action may update
     the semiperfect index.
     <P>
     Be careful. If the data structures
     are read-only, no changes will be made
     to the index. Updates will be ignored.
     <P>
     If the string is successfully indexed, the
     result will contain in the semi-key returned.
     The key will be updated to
     indicate the position of the string.
     So if you want to know that the string
     was indexed, clear the key, and then enter
     the string. Test the key on return, and see
     if the key now has data in it.

     **/

    int enter(String string);




    /** Hash a string into a semikey.
     * @param string
     * @param semiKey
     * @return
     */

    IndexKey enter(String string, IndexKey semiKey);


    /**
     * Returns true if the string is in the index;
     * if it is not, no entry is created and
     * false is returned.
     * @param string
     * @return boolean
     */

    boolean hasString(String string);

    /**
     * Compute the number of entries in the
     * semiperfect index.
     *
     **/

    int getNumberOfEntries();

    /** Get the string associated with a key. **/
    /**
     * Get the string associated with the key;
     * strings can only be retrieved if the index
     * retains strings.
     * @param key
     * @return string
     */

    String getString(int key);

    /**
     * Returns true the entered strings are retained
     * in the index.
     */

    boolean areStringsRetained();

    /**
     * Set the flag that determines if
     * strings are retained.
     */

    void setStringsRetained(boolean flag);

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

    /**
     * Return metrics on the index.
     * @return metrics
     */

    SemiPerfectMetrics getMetrics();


    /**
     * Get the current sequence number.
     * @return current sequence number
     */

    long getCurrentSequenceNumber();


    /**
     * Returns the position associated with
     * a string.
     * <P>
     * If this string does have a position,
     * -1 is returned.
     *
     * @param string
     * @return position
     */


    long getPosition(String string);


}
