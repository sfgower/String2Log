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


public class SequenceGenerator implements Serializable {

    public static long START_VALUE = -1;
    private long counter = START_VALUE;

    public SequenceGenerator() {
       counter = START_VALUE;
    }

    public long getNext() {
        return ++counter;
    }

    public long getCurrent()
    {
     return counter;
    }
}
