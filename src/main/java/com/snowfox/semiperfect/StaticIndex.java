package com.snowfox.semiperfect;


import java.util.Arrays;

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
 *
 * A semiperfect index allows a subset of
 * a set of strings to be mapped quickly
 * into a (nearly always) unique numeric value. For example,
 * suppose there were 200,000 strings.
 * A semiperfect index creates a partial function
 * semiperfect(string)-> integer. This is not
 * a true function as not all members of the
 * range (the type String) map to the range (the
 * set of integers). But all the strings in 200,000 entered
 * strings <i>will</i>.
 * <P>
 * This index really has two phases. First there is
 * a training phase, when the strings one wants
 * to recognized are entered. During the next phase,
 * the index is configured so that will no longer
 * accept any new strings. Because of this change,
 * the index will only recognize strings that it
 * consumed during the training phase. This is the
 * operational phase.
 * <P>
 * The recognized strings - the ones that have been
 * previously entered into the index - will return
 * a numeric value. So this index converts strings
 * into numeric values, and does it (we hope)
 * fairly quickly.
 * <P>
 * Internally, the index is actually a sequence
 * of component indices.
 * <P>
 * While I do not claim that this indexing method
 * is original, I did think of it myself, original or not.
 * I came up with this method because I needed some
 * very fast look up method that would not depend
 * on large hash tables. In Java, at least,
 * large hash tables failed miserably. Similarly,
 * b-tree like indexing methods struck me as being too
 * slow in this case. I needed very fast main-memory
 * performance, and to make that work, I needed an indexing
 * method that was sparing in its use of memory, and
 * certainly would require no disk I/O.
 * <P>
 * This indexing method was conceived of during
 * a dance recital, oddly enough.
 * <P>
 * Finally, for best runtime performance, it is best to train
 * the index by inserting strings in order of their frequency,
 * more frequent terms being entered into the index before
 * last frequent strings. This approach will make frequent
 * lookups faster.
 *
 * @author Stefan Gower.
 *
 **/

public class StaticIndex implements IComponentIndex, java.io.Serializable {

    public static final String ORIGIN = "com.snowfox.semiperfect.SemiPerfectSubIndex";

    public static final long FREE = -2;

    public final static int NON_KEY = ISemiPerfect.NOT_FOUND;

    public final static int CONSTANT = 1;

    private int indexSize = 0;

    private int level = -1;

    /** The total count is the sum total of all
     *  strings stored in this subindex or
     *  referred to next subindex.
     */

    private int totalCount = 0;

    private int numberOfStringsThatHaveBeenStoredInThisSubIndex;

    // If a string isn't found in this component,
    // it is referred to the next one. Here we count
    // these referrals.
    private int numberOfStringsReferredToNextSubIndex = 0;

    private int numberOfChecksums;
    private long _checksums[][] = null;

    /** An array of strings; only
     *  maintained if the index is not trusting. **/

    private String strings[] = null;


    private long sequenceNumbers[] = null;

    /** The next semi-perfect subindex. **/

    private IComponentIndex nextSubIndex = null;

    /** The hasher to use for this subindex,
     *  this hash key will locate the string
     *  in some position of the array.
     */

    private Hasher placeInArrayHasher = null;

    /** True if the subindex is writable. **/

    private boolean writable = true;


    /** Returns true if the subindex is "trusting". **/

    private boolean retention = true;

    public static final int HASHER_ARRAY_SIZE = 3;
    private int hashConstants[] = new int[]{2 + level, 8, 19, };
    private int hashLeftShifts[] = new int[]{4 + level, 3, 5};
    private int hashRightShifts[] = new int[]{5 + level, 4, 7, };

    private Hasher hashers[] = new Hasher[HASHER_ARRAY_SIZE];


    /** Construct a snow semiperfect.
     *
     * @param indexSize
     * @param level
     * @param trusting
     */

    public StaticIndex(int indexSize, int level, boolean trusting, int numberOfChecksums) {

        this.numberOfChecksums = numberOfChecksums;
        setRetention(trusting);
        init(indexSize);
        this.level = level;
        initHashers();
        placeInArrayHasher = new Hasher(indexSize, 3);

    }

    /**
     * Initialize the array of hashers
     */

    private void initHashers() {

        hashers[0] = new Hasher(Integer.MAX_VALUE, hashConstants[0]+level, hashLeftShifts[0], hashRightShifts[0]);
        hashers[1] = new Hasher(Integer.MAX_VALUE, hashConstants[1]+level, hashLeftShifts[1], hashRightShifts[1]);
        hashers[2] = new Hasher(Integer.MAX_VALUE, hashConstants[2]+level, hashLeftShifts[2], hashRightShifts[2]);

    }

    /**
     * Get the next semiperfect subindex, if any,
     * in the sequence of semiperfect subindex components.
     ***/

    public IComponentIndex getNextSubIndex() {
        return nextSubIndex;
    }

    /**
     * Subindex components are linked from "left to right".
     * Set the next subindex.
     *
     * @param subIndex
     */

    public void setNextSubIndex(IComponentIndex subIndex) {
        this.nextSubIndex = subIndex;
    }

    /**
     * Returns true if the semiperfect subindex is
     *writable. **/

    public boolean getWritable() {
        return writable;
    }

    /**
     * Set the writable flag.
     *
     * @param writableFlag
     */
    public void setWritable(boolean writableFlag) {
        this.writable = writableFlag;
    }

    /**
     * Initialize the semiperfect subindex.
     *
     * @param indexSize
     */
    public void init(int indexSize) {

        this.indexSize = indexSize;

        //checksums = new long[indexSize];

        //Arrays.fill(checksums, FREE);

        initChecksums();

        sequenceNumbers = new long[indexSize];
        Arrays.fill(sequenceNumbers, -1);

        // Only initialize the array of strings
        // if that array is to be maintained.

        if (retention == true)
            strings = new String[indexSize];
    }

    public void initChecksums() {
        _checksums = new long[numberOfChecksums][];
        for (int k = 0; k < numberOfChecksums; k++) {
            _checksums[k] = new long[indexSize];
            Arrays.fill(_checksums[k], FREE);
        }

    }

    /**
     * Enter a string in the semiperfect
     * index.
     * @param string
     * @param key
     * @return
     */

    public IndexKey enter(String string,
                          IndexKey key,
                          SequenceGenerator sgen) {
        totalCount++;

        // Hash the string. The hash will determine
        // the position of the string.

        int position = placeInArrayHasher.hash(string);

        // Now hash it with a different hasher,
        // so that this hash can be stored at that
        // location.
        //
        // This gives us a double hash - one hash
        // for the actual position of the entry - and
        // another, using a different hash, for the value.
        //
        // Nothing special here... but two hashes are
        // better than one...
        //

        // long checkSum = string.hashCode();

        long stringChecksums[] = generateChecksums(string);

        // We want to reserve zero for a special
        // meaning (FREE), so if the hash of the string
        // returns zero, we adjust the hash value to one.
        //


        // Is there a zero-value at the stored location?
        // If so, it means that this position is free!!
        // That is, it hasn't been used.
        //
        // If there is a checksum, it can be used
        // to see if the current string matches it.
        //

        //long checksum = _checksums[0][position];

        // Is this a free location?

        boolean free = isFree(position);
        if (free == true) {
            if (key.isProbe())
                key.invalidate();
            else if (writable == true)
                addNewEntry(stringChecksums, position, string, key, sgen);
            else
                key.invalidate();
            return key;
        }


        // Okay, this position is not free.
        // But the checksum at this location could
        // match with the checksum of this string.
        //
        // If so, we now have used two different hashes
        // on the same value, and both could match.
        //
        // If both hashes match, the statistical chance that
        // this is the same string is very likely.
        //
        // This probability is even higher when a subindex is larger...

        if (checksumsDoNotMatch(stringChecksums, position)) {
            // We track the number of rejections,
            // because this is an interesting metric.
            //
            addReject(string);

            // We are now in the following state:
            // we have encountered a hash collision.
            // The checksum doesn't match. So not only is
            // this position taken, but the string we are
            // searching for has a checksum that does *not*
            // match the checksum of this location.
            //
            // Accordingly, we search for the next subindex.
            //
            return nextSubIndex.enter(string, key, sgen);
        }

        // Okay, these should be the same strings.
        // If trusting is false, you can then
        // check that they *are* the same strings.

        if (true); // retention == true)
            paranoidCheck(position, string);

        key.setPosition(position);
        key.setLevel(level);

        long sequenceNumber = sequenceNumbers[position];
        key.setSequenceNumber(sequenceNumber);
        return key;
    }

    public boolean checksumsDoNotMatch(long values[], int position) {
        for (int k = 0; k < values.length; k++) {
            if (_checksums[k][position] != values[k])
                return true;
        }
        return false;
    }

    IStringHashCoder hasherTwo = null;


    public long hashString(int number, String string) {
        if (number == 0)
            return string.hashCode();
        else
            return hashers[number].hash(string);
    }


    /**
     * Generate checksum(s) for the string.
     * @param string
     * @return checksum(s)
     */
    public long[] generateChecksums(String string) {


        long stringChecksums[] = new long[this.numberOfChecksums];
        for (int k = 0; k < this.numberOfChecksums; k++) {
            stringChecksums[k] = hashString(k, string);
        }
        return stringChecksums;
    }


    /**
     * A paranoid check...
     * @param position
     * @param string
     */
    private void paranoidCheck(int position, String string) {
        // Here is the paranoid test (a very much needed paranoid test.
        // Could the unlikely occur? Could two strings map to the snowkey?

        if (strings==null)
          return;

        String firstStringThatEndedUpAtThisLocation = strings[(int) position];
        if (string.equals(firstStringThatEndedUpAtThisLocation) == false)
            throw new Error("String do not match: " + string + "<>" + firstStringThatEndedUpAtThisLocation);
    }

    /**
     * Add a new entry
     * @param checkSum
     * @param position
     * @param string
     * @param key
     * @param sgen
     */

    private void addNewEntry(long checkSum[],
                             int position,
                             String string,
                             IndexKey key,
                             SequenceGenerator sgen) {
        // Increment the counter that indicates how
        // many strings have been stored in this subindex.

        numberOfStringsThatHaveBeenStoredInThisSubIndex++;

        // checksums[position] = checkSum;

        applyChecksums(checkSum, position);

        long sequenceNumber = sgen.getNext();
        sequenceNumbers[position] = sequenceNumber;

        // Store the string.

        if (retention == true) {
            // Remember the string that was stored at this position.
            if (strings == null)
                throw new Error("Non-initialized strings array");
            strings[position] = string;
        }
        key.setPosition(position);
        key.setLevel(level);
        key.setSequenceNumber(sequenceNumber);
    }

    /**
     * Assign the checksums.
     * @param checkSums
     * @param position
     */

    private void applyChecksums(long checkSums[], int position) {
        for (int k = 0; k < checkSums.length; k++)
            _checksums[k][position] = checkSums[k];
    }

    /**
     * Add another rejection to
     * the running count of rejections.
     * @param string
     */

    public void addReject(String string) {
        numberOfStringsReferredToNextSubIndex++;
    }


    /**
     * Get the size of the semiperfect subindex.
     ***/

    public int getIndexSize() {
        return indexSize;
    }

    /**
     * Get the percentage of the
     * non-unique keys. To do this,
     * we take the number of unique
     * keys and divide them by the
     * total number of strings. As this
     * ratio gets closer to one, the
     * semiperfect is covering a larger percentage
     * of the strings in the set of strings.
     * Conversely, as this semiperfect tends towards
     * zero, the coverage of the semiperfect is
     * becoming poorer.
     **/

    public double getRatio() {

        return (double) (numberOfStringsThatHaveBeenStoredInThisSubIndex / totalCount);
    }

    /**
     * Get the total count.
     ***/

    public int getTotalCount() {
        return this.totalCount;
    }

    /**
     * Get the number of strings that have been
     * stored in this index component.
     ***/

    public int getNumberOfStringsThatHaveBeenStoredInThisSubIndex() {
        return this.numberOfStringsThatHaveBeenStoredInThisSubIndex;
    }

    /**
     * Get the number of strings that
     * have been rejected; that is,
     * return the number of times a string has
     * been referred to the next level.
     ***/

    public int getNumberOfStringsReferredToNextSubIndex() {
        return this.numberOfStringsReferredToNextSubIndex;
    }

    /**
     * Return the string at the specified position.
     * The index can only retrieve such a string
     * if the index has retained strings. If the
     * index has not retained strings, null will
     * always be returned.
     *
     * @param snowKey
     * @return
     */

    public String getString(IndexKey snowKey) {
        if (retention == false)
            return null;
        else
            return strings[snowKey.getPosition()];
    }

    /**
     * Compute the number of active keys;
     * this just means counting the "slots"
     * in the array that have been occupied.
     ***/

    public int getNumberOfEntries() {
        int len = _checksums[0].length;
        int activeCount = 0;
        for (int k = 0; k < len; k++) {
            if (_checksums[0][k] != FREE)
                activeCount++;
        }
        return activeCount;

    }


    /**
     * Returns true if the subindex is trusting.
     ***/

    public boolean areStringsRetained() {
        return retention;
    }

    /**
     * Make the subindex trusting (or not).
     * Without strings, the index must "trust"
     * the double hashing method; it is this
     * sense that we use the word "trusting".
     *
     * @param retention
     */

    public void setRetention(boolean retention) {
        this.retention = retention;
    }

    /**
     * Get the level of the semiperfect subindex.
     ***/

    public int getLevel() {
        return level;
    }

    /**
     * Set the level of the semiperfect subindex.
     *
     * @param level
     */

    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Returns true if the checksum
     * is equal to the special value
     * that indicates that this position
     * is free.
     * @param position
     * @return boolean
     */

    private boolean isFree(int position) {
        return _checksums[0][position] == FREE;
    }

    /**
     * Returns true if this is
     * the last subindex in the semiperfect hash.
     * @return boolean
     */

    public boolean isLast() {
        return false;
    }

    /**
     * Get the number of items
     * this subindex can hold
     * @return size
     */

    public int getCapacity() {
        return this.getIndexSize();
    }

    /**
     * The semiperfect index can retain strings
     * during its construction... or not. Configuration
     * determines whether the index retains strings or not.
     * <P>
     * Even when the index initially retains strings, it
     * may be desirable (to save space)
     * to remove all these retained strings.
     * <P>
     * This method clears all such retained strings,
     * thus saving that space.
     */

    public void clearStrings() {
        if (this.retention != true)
            return;
        if (this.strings == null)
            return;
        for (int k = 0; k < strings.length; k++)
            strings[k] = null;

        strings = null;
    }
}