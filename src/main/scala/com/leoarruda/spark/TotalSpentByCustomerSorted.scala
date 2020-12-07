package com.leoarruda.spark


import org.apache.spark._
import org.apache.log4j._

/** Compute the total amount spent per customer in some fake e-commerce data. */
object TotalSpentByCustomerSorted {
  
  /** Convert input data to (customerID, amountSpent) tuples */
  def extractCustomerPricePairs(line: String): (Int, Float) = {
    val fields = line.split(",")
    (fields(0).toInt, fields(2).toFloat)
  }
 
  /** Our main function where the action happens */
  def main(args: Array[String]) {
   
    // Set the log level to only print errors
    Logger.getLogger("org").setLevel(Level.ERROR)
    
     // Create a SparkContext using every core of the local machine
    val sc = new SparkContext("local[*]", "TotalSpentByCustomerSorted")   
    
    val input = sc.textFile("data/customer-orders.csv")

    val mappedInput = input.map(extractCustomerPricePairs)
    
    val totalByCustomer = mappedInput.reduceByKey( (x,y) => x + y )

    // Flipping the list to Sort by the total Spent    
    val flipped = totalByCustomer.map( x => (x._2, x._1) )
    
    // Sorting by the total spent
    val totalByCustomerSorted = flipped.sortByKey()

    // Flipping back the list
    val results = totalByCustomerSorted.collect()

    val results_final = results.map( x=> (x._2, x._1))

    // Printing the results.
    results_final.foreach(println)
  }
  
}