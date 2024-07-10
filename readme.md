    To compile:
    javac -sourcepath ./src/main/java/ru/clevertec/check/* -d src ./src/main/java/ru/clevertec/check/CheckRunner.java

    Arguments to start:
    java -cp src ./src/main/java/ru/clevertec/check/CheckRunner.java 3-1 2-5 5-1 discountCard=1111 balanceDebitCard=100 pathToFile=src/main/resources/products saveToFile=src/main/resources/out.csv
  
    "3-1 2-5 5-1" 
    3 for the product id
    1 for that product count
    etc.
    
    discountCard=1111
    1111 is the discount card number which is used to calculate the discount

    balanceDebitCard=100
    100 is the amount which must be higher than total receipt value otherwise program will throw an exception

    All csv files that program uses and generates can be found in the resources folder
