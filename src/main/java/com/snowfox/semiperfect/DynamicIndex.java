package com.snowfox.semiperfect;

import java.util.*;

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

public class DynamicIndex implements IComponentIndex, java.io.Serializable {
    private int level;
    private IStringHashCoder hasher = null;
    private IStringHashCoder hasher2 = null;
    private IStringHashCoder hasher3 = null;

    private boolean writable = true;
    private boolean retention = false;
    private int entryCount = 0;

    private Map entryMap = new HashMap();

    private final int hashConstant = 13;
    private final int hashConstant2 = 27;
    private final int hashConstant3 = 2;

    /**
     * Create a map subindex.
     * @param initialIndexSize
     * @param level
     */

    public DynamicIndex(int initialIndexSize,
                        int level,
                        boolean retention) {
        entryMap = new HashMap(initialIndexSize);
        hasher = new Hasher(Integer.MAX_VALUE, hashConstant);
        hasher2 = new Hasher(Integer.MAX_VALUE, hashConstant2);
        hasher3 = new Hasher(Integer.MAX_VALUE, hashConstant3,4,17);
        this.level = level;
    }

    public int getLevel() {
        return this.level;
    }

    public IComponentIndex getNextSubIndex() {
        return null;
    }

    /** Get the index size. **/

    public int getIndexSize() {
        return entryCount;
    }


    /**
     * Set the next subindex in the the
     * sequence of subindices.
     * <P>
     * This is ignored as this is the last index.
     * @param index
     */

    public void setNextSubIndex(IComponentIndex index) {
        // do nothing
    }

    /**
     * Enter a string into the index.
     * @param string
     * @param key
     * @return key
     */

    public IndexKey enter(String string, IndexKey key, SequenceGenerator sgen) {



        // The first hash is used to find the string's position
        // in a map.
        Integer hashKey = new Integer(hasher.hash(string));
        Object object = this.entryMap.get(hashKey);
        boolean isList = object instanceof List;
        if ((object == null) && (!this.getWritable())) {
            key.invalidate();
            return key;
        }


        int hash2 = hasher2.hash(string);
        int hash3 = hasher3.hash(string);
        int hashvalues[] = new int[]{hash2,hash3};
        if (isList)
            return handleList(hashvalues, (List) object, string, key, sgen);
        else
            return handleSingleton(hashKey,
                    hashvalues,
                    (DynamicIndexEntry) object,
                    string,
                    key,
                    sgen
            );


    }

    /**
     * Handle an entry that is not part of a list.
     * @param hashKey
     * @param hashvalues
     * @param entry
     * @param string
     * @param key
     * @param sgen
     * @return index key
     */

    public IndexKey handleSingleton(Integer hashKey,
                                int hashvalues[],
                                DynamicIndexEntry entry,
                                String string,
                                IndexKey key,
                                SequenceGenerator sgen) {
        if ((entry != null) && (compareHashValues(hashvalues,entry.getHashvalues()))) {

            if ((entry.getString()!=null)
                       &&
                 (! entry.getString().equals(string)))
            {
             System.out.println();
             System.out.println("Check sums match but strings do not.");
             String entryString = entry.getString();
             System.out.println("input string="+entryString);
             System.out.println("entry string="+entryString);
             int rehashOne = hasher2.hash(entryString);
             int rehashTwo = hasher3.hash(entryString);
             System.out.println("rehashOne="+rehashOne);
             System.out.println("rehashTwo="+rehashTwo);
             System.out.println("hashone="+hashvalues[0]);
             System.out.println("hashtwo="+hashvalues[1]);
             System.out.println();
            }

            key.setLevel(level);
            key.setPosition(entry.getPosition());
            key.setSequenceNumber(entry.getSequenceNumber());
            return key;
        }

        // If we are probing and we have reached this far,
        // it means that either there was no entry or that
        // a comparison with the entry failed. In either
        // case, the probe has failed and should result
        // that the key is invalid; that is, there is no
        // such string in the index.

        if (key.isProbe()) {
            key.invalidate();
            return key;
        }

        if (!this.getWritable()) {
            key.invalidate();
            return key;
        }


        int position = ++entryCount;
        String s = this.areStringsRetained() ? string : null;
        DynamicIndexEntry newEntry = new DynamicIndexEntry(hashvalues, position, s);
        newEntry.setSequenceNumber(sgen.getNext());

        if (entry == null)
            entryMap.put(hashKey, newEntry);
        else {
            List list = new LinkedList();
            list.add(entry);
            list.add(newEntry);
            entryMap.put(hashKey,list);
        }

        key.setLevel(level);
        key.setPosition(position);
        key.setSequenceNumber(newEntry.getSequenceNumber());
        return key;
    }

    /**
     * Compare two arrays of hashes
     * @param x
     * @param y
     * @return boolean
     */

    private boolean compareHashValues(int[] x,int[] y)
    {
     final int len = x.length;
     for (int k = 0; k < len; k++)
        if (x[k]!=y[k])
            return false;
     return true;
    }

    public IndexKey handleList(int hashValues[],
                               List list,
                               String string,
                               IndexKey key,
                               SequenceGenerator sgen) {
        DynamicIndexEntry entry = null;
        Iterator scan = list.iterator();
        while (scan.hasNext()) {
            entry = (DynamicIndexEntry) scan.next();
            if (entry.getString() != null)
              if (! entry.equals(string))
                 continue;
            if (compareHashValues(hashValues,entry.getHashvalues())) {
                key.setLevel(level);
                key.setPosition(entry.getPosition());
                key.setSequenceNumber(entry.getSequenceNumber());
                return key;
            }
        }


        // If the key is a probe, it means to
        // return the entry if found, but otherwise
        // return an invalid key and do NOT create
        // an entry.

        if (key.isProbe()) {
            key.invalidate();
            return key;
        }

        // If this index component is not writiable,
        // then we will not be creating any entry.
        // Instead, it is just to return "not found".


        if (!this.getWritable()) {
            key.invalidate();
            return key;
        }

        // We will create a new entry, so we increment
        // the entry count.
        //
        // The entry count *is* the position.

        int position = ++entryCount;

        // If strings are retained, keep the string,
        // otherwise lose it.

        String s = this.areStringsRetained() ? string : null;


        // Create an entry with the hash, position and string (or null).

        entry = new DynamicIndexEntry(hashValues, position, s);
        long sequenceNumber = sgen.getNext();
        entry.setSequenceNumber(sequenceNumber);

        // Add it to the list...

        list.add(entry);

        // Configure the key and return it.

        key.setLevel(getLevel());
        key.setPosition(position);
        key.setSequenceNumber(sequenceNumber);
        return key;
    }


    public void setWritable(boolean flag) {
        this.writable = flag;
    }

    public boolean getWritable() {
        return this.writable;
    }

    public int getNumberOfEntries() {
        return this.entryCount;
    }

    /**
     * Get a string based on a key.
     * @param key
     * @return
     */

    public String getString(IndexKey key) {


        if (!this.areStringsRetained())
            return null;
        int position = key.getPosition();
        List list;
        Iterator scan = this.entryMap.values().iterator();
        Iterator scan2;
        Object object;
        DynamicIndexEntry entry;
        while (scan.hasNext()) {
            object = scan.next();
            if (object instanceof DynamicIndexEntry) {
                entry = (DynamicIndexEntry) object;
                if (entry.getPosition() == key.getPosition())
                    return entry.getString();
                continue;
            }
            list = (List) scan.next();
            scan2 = list.iterator();
            while (scan2.hasNext()) {
                entry = (DynamicIndexEntry) scan2.next();
                if (entry.getPosition() == position)
                    return entry.getString();
            }

        }

        return null;
    }

    /**
     *  Returns true if the subindex retains strings.
     *  When retaining, index actually holds
     *  the original string value
     *  and does a direct string to string comparison.
     *  <P>
     *  The last index never is trusting. It always
     *  retains the strings, and simply compares the
     *  search string against the strings stored in the index.
     * @return false
     *
     */

    public boolean areStringsRetained() {
        return retention;
    }

    /**
     * Sets the flag that determines
     *  if entered strings are retained
     *  or not.
     * **/

    public void setRetention(boolean flag) {
        this.retention = flag;
    }

    /**
     * Returns true if this is
     * the last subindex in the semiperfect hash.
     * And it always will be.
     * @return boolean
     */

    public boolean isLast() {
        return true;
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


        List list;
        Iterator scan = this.entryMap.values().iterator();
        Iterator scan2;
        DynamicIndexEntry entry;
        while (scan.hasNext()) {
            list = (List) scan.next();
            scan2 = list.iterator();
            while (scan2.hasNext()) {
                entry = (DynamicIndexEntry) scan2.next();
                entry.clearString();
            }

        }


    }

}
