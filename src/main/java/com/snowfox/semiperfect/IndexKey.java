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


public class IndexKey implements IIndexKey,
        java.io.Serializable {

    public static final int INVALID = -1;
    private int position = 0;
    private int level = 0;
    private long sequenceNumber = 0;
    private boolean probe = false;

    public IndexKey(int position, int level) {
        this.position = position;
        this.level = level;
    }


    public IndexKey(int position, int level, boolean probe) {
        this.position = position;
        this.level = level;
        this.probe = true;
    }


    /** Get the position. **/

    public int getPosition() {
        return position;
    }

    /** Set the position of the key. **/

    public void setPosition(int position) {
        this.position = position;
    }

    /** Get the level of the key. **/

    public int getLevel() {
        return level;
    }

    /** Set the level of the key. **/

    public void setLevel(int level) {
        this.level = level;
    }

    public String toString() {
        return "<snowKey><position>" + position + "</position><level>" +
                level + "</level></snowKey>";
    }

    /** Two keys are equal if they have
     the same level and the same position. **/

    public boolean equals(Object other) {
        if (other == null)
            return false;
        if ((other instanceof IndexKey) == false)
            return false;
        IndexKey otherKey = (IndexKey) other;
        return (this.getLevel() == otherKey.getLevel())
                &&
                (this.getPosition() == otherKey.getPosition()
                &&
                (this.getSequenceNumber() == otherKey.getSequenceNumber()));

    }

    /** Returns true if the snowkey has a leve
     and a position, other than -1. **/

    public boolean isValid() {
        return (getLevel() != INVALID) && (getPosition() != INVALID);
    }

    /** Returns true if the key is invalid;
     that is, its level or position is equal to -1.
     **/

    public boolean isInvalid() {
        return !isValid();
    }

    /** Create a copy of this key. **/

    public IIndexKey copy() {
        return new IndexKey(this.getPosition(), this.getLevel());
    }

    /** Set the properties that indicate that a key is invalid. **/

    public void invalidate() {
        this.setLevel(-1);
        this.setPosition(ISemiPerfect.NOT_FOUND);
    }

    public long getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(long sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public boolean isProbe() {
        return probe;
    }

    public void setProbe(boolean probe) {
        this.probe = probe;
    }
}