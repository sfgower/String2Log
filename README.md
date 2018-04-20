# text2number
Flexible data structure to map each string to a unique long and do this using very little memory

Some algorithms work very nicely with numeric values, such as longs. For example, the FP-Growth clustering
algorithm works very nicely with longs. If, however, you start using strings, you can make the
algorithm work, however as the number of string increases, it is quite easy to blow out main-memory.

In general, if you have algorithm that works better with numeric values, there is a need to
convert strings into longs.  This isn't very hard to do if brutue force is used. But what
if you need a very efficient algorithm that conserves main-memory and is still *very* fast?

This is where this algorithm comes in handy.

But before considering it how it can be used, let's consider some special features
of this algorithm that arose from the original problem domain. 

Suppose you have a stream of strings that will arrive and, for each string, you want to
associate that string with a unique long value. Also suppose - to avoid blowing out main-memory - you
do not want (or simply cannot) fit all these strings into main-memory?

Well, I had this problem when trying to analyze a very large number of strings. Furthermore, the arrival
of these strings was incremental, such that I could not just do some single large batch job, and declare
that each and every string would be uniquely associated with a long, but there would no more strings.
That wasn't workable. In many cases, once I had 25 million strings, a new string could arrive, and
this new string would also need to be associated with a long value.

So I had a problem..

In the context of the original problem I faced, I needed to able to add new strings when and if needed.
At the time I wrote this code (several years ago), I could not find any algorithm that could do this.
I looked a lot. Maybe that algorithm and/or code was out there, but I never did find it.

I did find this to be a fairly nasty problem, and my attempts to solve it did not meet with any immediate
success. I came back to the problem from different angles several time before I finally found a suitable
approach.

Eventually, I came up with a flexible data structure that would guarantee with an extremely high probability
that each string would either be associated with the next long value, or it would be associated with
a previously assigned long value.  And it was not necessary to retain the strings in main-memory to do this.
(The code allows such retention of string, but this was primarily for debugging purposes).

So if you stream a sequence of string like...

   "the" , "dog" "jumped", "over", "the", "fence"
   
you would get
 
   "the" -> 1
   "dog" -> 2
   "jumped" -> 3
   "over" -> 4
   "the" -> 1
   "fence" -> 5
   
And this works for a very much longer quantity of strings. In my original application, I would typically have
25 million strings, and this converter did a great job turning those strings into long values.

The code is built using Maven.

Please note that running the tests can take three to five minutes. A lot of data is generated in the process.
So just do 'mvn test'.

The tests should how to use this data structure that I named SemiPerfect, because the data structure
acts somewhat like a perfect hash, but it - unlike a perfect hash - is dynamic in nature.

The savvy reader may wonder this code was not paired with some backing store where the strings could be stored
and fetched? This simply wasn't something I needed at the time, so I never got around to doing this;
however, it wouldn't be too hard to pair this code with backing store - be it BerkeleyDB, Cassandra or Avro
or something else - such that all the pairings of string and long number would be stored somewhere, such that given, a long value, one could
retrieve the original string from the backing store.



