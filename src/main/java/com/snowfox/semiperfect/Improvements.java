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
 * Improvements to the generational index?
 *
 * Use the initial hash at level N to create
 * a hash for level N + 1 simply by modding
 * the N-level hash by the size of N + 1 index.
 * <P>
 * This approach will save the runtime cost of a hash function creation.
 * A minor saving.
 * <P>
 * It also is unclear if there is any real savings
 * in creating a new 2nd hash at each level.
 * Is this is a real benefit or merely a faux
 * benefit? Unclear.
 * <P>
 * It would also be good to measure the average level
 * reached for lookups. This would give a better
 * idea of the runtime effectiveness of the hash.
 * Certainly one can compute the average lookup
 * when the index is "trained", but this lookup
 * average could be quite different than using
 * the index operationally.
 * <P>
 * In particular, if the strings are entered in the
 * order of their operational frequency, with more frequent
 * strings entered first, it would seem like that
 * the average lookup would be greatly reduced,
 * as if there is a collision, the "loser" strings
 * would be the less frequent strings, and therefore
 * would be pushed down into lower-levels.
 * <P>
 * Consider that the index is effectively trained
 * by being exposed to strings. If a string with
 * greater natural frequency (in some population
 * of documents etc) is entered before another,
 * it will have a chance to grab an entry in
 * the index prior to another string.
 * <P>
 * So if string S1 and S2 collide, one can still minimize
 * the number of collisions by entering the higher
 * frequency string first. For example, if the string S1
 * will be encountered 1000 times operationally while
 * the string S2 is only encountered 100 times, then
 * the order of training is quite important. If string
 * S1 is entered first, there will be 900 collisions.
 * If string S2 is entered first, there will be 100
 * collisions.
 * <P>
 * Hence if can know "in advance" the frequency of terms,
 * it is best to train the index by order of frequency.
 * <P>
 * It is, however, unclear how much runtime performance
 * this would gain in practice, in the sense of
 * overall runtime categorization costs.
 */
public class Improvements {
}
