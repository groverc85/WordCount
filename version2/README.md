### Advanced version of WordCount

#### Added the following features according to the requirement:

1. Generate huge file with a small file by duplicating file 10000 times

2. Use multiple threads so that the CPU is effectively utilized and program runs faster.

	2.1 Detect single word intercept situation during partition, add if(false == Character.isLetter(ch) && ''' != ch) to avoid that situation

	2.2 All results child threads are stored into TreeMap(better than HashMap for prefix searching), output the result alphabetically

3. Consider the situation that the entire file won't fit in memory

	3.1 Limit the number of threads, 10 would be the best number on my local machine obtained by multiple testing

	3.2 set the partition file size between 1M - 10M

4. Use thread pool to avoid the creation of too many threads thus draining system memory, thread pool can reduce the time and cost during creating and destroying thread.

5. Use stream and lambda expression to implement word partition, then use collect() method to collect result into map.

6. Complete pharse 2 - prefix search by reading user's input

FYI: https://www.umich.edu/~umfandsf/other/ebooks/alice30.txt needs UMich login


#### File structure:

ProcessText - deals with text partition for each thread

WordCount - main, deals with file read and running time output

WordsThread - process text (calculate occurrence within each thread)

1.txt - original file(~70kb)

2.txt - generated file by duplicating content in 1.txt x(suggest 10000) times, file size ~ 800M

result.txt - result showing occrence of each word

###### TODO:

1. Add strategy for Word Separation, Capitalization by reading user's command line input.