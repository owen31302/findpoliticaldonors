# Table of Contents
1. [Environment](README.md#environment)
2. [Class Description](README.md#class-description)
3. [How to Run](README.md#how-to-run)

# Environment
Programming language: Java<br>
IDE: IntelliJ<br>
OS: MAC OS<br>
Java version: Java 8

# Class Description
There are three classes in this project:
1. FindPoliticalDonors  
 This is the main logic program.  
 It divided into two parts:  
 **`(1)Input argument part`**  
 It will take the input argument, read files, loop over the input files, and output the result in txt.  
 **`(2)Maintaining data pool`**  
 It will hold the ZIPCODE container in Map of Map of TransactionManager structure, so that we can identify the recipient in the specified zipcode in O(1) time. It will also hold the TX_DT container in the same structure but in different implementation. It uses TreeMap to make the recipient name in alphabetical order, and chronologically by date. When we insert one row in the TX_DT container, it takes O(logN) time. When we save the TX_DT container to the txt file, it take O(N) time without sorting.  
2. TransactionManager   
 It will take transaction amount as input, and update the information, such as median, number of transaction, total amount.  
 **`Note:`**  
 In order to optimize the getMedian operation, it uses two heap structure (minHeap, maxHeap) to find the median. It will take O(logN) times to maintain the structure, and gives us O(1) time to find the median.  
3. TransactionObject  
 It is to parse the input string as the specified format, and to tell other class if this transaction object is valid or not and provide other classes with the desired information.

# How to Run
* Install Java 8 if you don't have it on your computer.
* Download this repository, and extract it.
* Change directory to this folder.
* Run shell script `sh run.sh`, then the program will process the input file and save the result to the output file. 