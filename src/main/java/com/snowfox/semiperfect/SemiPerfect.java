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
 * A semiperfect index.
 * <P>
 * The essence of its function is to convert a string into a number,
 * such that the number produced is unique.
 * <P>
 * Is it possible for two distinct strings to map
 * to the same number? Yes. But such mappings
 * are extremely unlikely, as it would mean
 * that both strings produced identical hashes
 * multiple times.
 *
 * @author Stefan Gower
 */

public class SemiPerfect
        implements ISemiPerfect,
        java.io.Serializable {

    public static final int DEFAULT_NUMBER_OF_CHECKSUMS = 2;

    private int numberOfChecksums = DEFAULT_NUMBER_OF_CHECKSUMS;
    private int[] indexSizes = null;
    private IComponentIndex[] subIndexes = null;
    private boolean writable = true;
    private boolean retainStrings = false;

    // This index key will be used to generate a position;
    // it will only be invalid if the index is not writable
    // or if the index runs out of space.

    private final IndexKey INDEX_KEY = new IndexKey(0, 0);

    // This index key is used to obtain the result of a probe

    private final IndexKey PROBING_INDEX_KEY = new IndexKey(0, 0, true);
    private SequenceGenerator sequenceGenerator = new SequenceGenerator();


    /**
     * Construct a master semiperfect index that
     * links together a sequence of subindices,
     * one for each level of the semiperfect index.
     * @param indexSizes
     * @param retention
     */

    public SemiPerfect(int[] indexSizes, boolean retention)
    {
     this(indexSizes,retention,DEFAULT_NUMBER_OF_CHECKSUMS);
    }

    /**
     * Construct a master semiperfect index that
     * links together a sequence of subindices,
     * one for each level of the semiperfect index.
     * @param indexSizes
     * @param retention
     * @param numberOfChecksums
     */

    public SemiPerfect(int[] indexSizes, boolean retention,int numberOfChecksums) {
        this.indexSizes = indexSizes;
        int levels = indexSizes.length;
        this.numberOfChecksums = numberOfChecksums;
        subIndexes = new IComponentIndex[levels];
        for (int k = 0; k < levels; k++) {
            int indexSize = indexSizes[k];
            IComponentIndex index = null;
            if ((k + 1) == levels)
                index = new DynamicIndex(indexSize, k, retention);
            else
                index = new StaticIndex(indexSize, k, retention,numberOfChecksums);

            subIndexes[k] = index;

            if ((k - 1) > -1) {
                subIndexes[k - 1].setNextSubIndex(index);
            }
        }

    }

    /**
     * Returns true if the index is writable.
     * @return
     */

    public boolean getWritable() {
        return writable;
    }

    /**
     * Makes all the subindices writable (or not)
     * depending on the value of the flag.
     * @param flag
     */

    public void setWritable(boolean flag) {
        this.writable = flag;
        int levels = subIndexes.length;
        for (int k = 0; k < levels; k++)
            subIndexes[k].setWritable(flag);

    }

    /**
     * Get the subindex at a given level.
     * @param level
     * @return
     */

    public IComponentIndex getSubIndex(int level) {
        return subIndexes[level];
    }


    /**
     * Enter a string into the index. If the index
     * is writable, this action may cause a new entry
     * to be created in the index. Otherwise, if the
     * index is not writable, no entry will be created.
     * The return value is a numeric value indicating
     * the string's position in the index. This
     * value is effectively unique in the sense that
     * it is statistically almost impossible that
     * two strings will map to the same numeric value.
     * Not impossible, but close enough to impossible
     * to not matter, particularly for patter mining.
     * @param string
     * @return key
     */


    public int enter(String string) {
        getRoot().enter(string, INDEX_KEY, this.getSequenceGenerator());
        return this.semiKeyToInt(INDEX_KEY);
    }

    /** Hash a string into a semikey.
     * @param string
     * @param semiKey
     * @return
     */
    public IndexKey enter(String string, IndexKey semiKey) {
        getRoot().enter(string, semiKey, this.getSequenceGenerator());
        return semiKey;
    }

    /**
     * Returns true if the string is in the index;
     * if it is not, no entry is created and
     * false is returned.
     * @param string
     * @return boolean
     */

    public boolean hasString(String string) {
        this.enter(string, PROBING_INDEX_KEY);
        return !PROBING_INDEX_KEY.isInvalid();
    }


    /**
     * Compute the number of active keys
     * in the index.
     * @return
     */

    public int getNumberOfEntries() {
        int activeCount = 0;
        int len = subIndexes.length;
        for (int k = 0; k < len; k++)
            activeCount += subIndexes[k].getNumberOfEntries();
        return activeCount;

    }

    /**
     * Get the first subindex.
     * @return
     */

    public IComponentIndex getRoot() {
        return subIndexes[0];
    }


    /**
     * Get the array of subindex sizes that
     * are used as an input when the
     * semiperfect index is created.
     * @return
     */

    public int[] getIndexSizes() {
        return this.indexSizes;
    }

    /**
     * Get the string associated with a position.
     * Note: the original string can only
     * be retrieved if the string values
     * are present in the index.
     * @param k
     * @return
     */

    public String getString(int k) {
        this.intToSemiKey(k, INDEX_KEY);
        IComponentIndex index = subIndexes[INDEX_KEY.getLevel()];
        if (index == null)
            return null;
        else
            return index.getString(INDEX_KEY);
    }

    /**
     * Retrieve the string associated with a key.
     * @param semiKey
     * @return
     */

    public String getString(IndexKey semiKey) {
        IComponentIndex index = subIndexes[semiKey.getLevel()];
        if (index == null)
            return null;
        else
            return index.getString(semiKey);
    }


    /**
     * Returns true if the semi-perfect index is trusting.
     * @return
     */

    public boolean areStringsRetained() {
        return retainStrings;
    }


    /**
     * Indicates whether this semi-perfect index
     * is trusting or not.
     * @param retention
     */

    public void setStringsRetained(boolean retention) {
        IComponentIndex subIndex = getRoot();
        while (subIndex != null) {
            subIndex.setRetention(retention);
            subIndex = subIndex.getNextSubIndex();
        }
        this.retainStrings = retention;
    }

    /**
     * Convert a key to an integer - an
     * integer that indicates how a string
     * has been mapped to a position in the index.
     * @param key
     * @return
     */

    public int semiKeyToInt(IIndexKey key) {

        int size = 0;

        int level = key.getLevel();
        int position = key.getPosition();
        int sum = 0;
        for (int k = 0; k < indexSizes.length; k++) {
            if (level == 0)
                return sum + position;
            level--;
            sum = sum + indexSizes[k];
        }
        return -1;
    }

    /**
     * Convert a key into a numeric position.
     * @param k
     * @param key
     */

    public void intToSemiKey(int k, IndexKey key) {
        final int lastLevel = indexSizes.length - 1;
        int size = 0;

        for (int level = 0; level < indexSizes.length; level++) {
            size = indexSizes[level];
            if ((level == lastLevel) || (k < size)) {
                key.setLevel(level);
                key.setPosition(k);
                break;
            }
            k = k - size;

        }

    }

    /**
     * Get the number of retained strings
     * in the index.
     * @return count
     */

    public int numberOfRetainedStrings() {
        if (this.areStringsRetained())
            return this.getNumberOfEntries();
        else
            return 0;
    }

    /**
     * Get the last subindex.
     * @return last subindex
     */

    public IComponentIndex getLastIndex() {
        int last = this.indexSizes.length - 1;
        return this.subIndexes[last];
    }

    /**
     * Get the number of levels.
     * @return number of levels.
     */

    public int getNumberOfLevels() {
        return this.subIndexes.length;
    }

    /**
     * Return metrics on the index.
     * @return metrics
     */

    public SemiPerfectMetrics getMetrics() {
        return new SemiPerfectMetrics(this);
    }

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

    public void clearStrings() {
        for (int k = 0; k < this.getNumberOfLevels(); k++)
            this.getSubIndex(k).clearStrings();
    }

    public SequenceGenerator getSequenceGenerator() {
        return sequenceGenerator;
    }

    public long getCurrentSequenceNumber() {
        return this.getSequenceGenerator().getCurrent();
    }

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

    public long getPosition(String string) {
        this.enter(string, PROBING_INDEX_KEY);
        if (PROBING_INDEX_KEY.isInvalid())
            return -1;
        else
            return PROBING_INDEX_KEY.getSequenceNumber();
    }


}