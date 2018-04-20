package com.snowfox.semiperfect;
import java.io.Serializable;


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

public class DynamicIndexEntry implements Serializable {


    public static final long NO_SEQUENCE_NUMBER = -1;
    private int hashvalues[] = null;
    private int position = 0;
    private String string = null;
    private long sequenceNumber = NO_SEQUENCE_NUMBER;


    /**
     * Create an entry.
     * @param hashvalues
     * @param position
     * @param string
     */

    public DynamicIndexEntry(int hashvalues[], int position, String string) {
        this.hashvalues = hashvalues;
        this.position = position;
        this.string = string;

    }

    /**
     * Get the string (if present).
     * @return
     */

    public String getString() {
        return string;
    }

    /**
     * Get the position.
     * @return
     */

    public int getPosition() {
        return position;
    }

    /**
     * Get the hash value.
     * @return
     */

    public int[] getHashvalues() {
        return hashvalues;
    }

    public void clearString()
    {
     this.string=null;
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }


}
