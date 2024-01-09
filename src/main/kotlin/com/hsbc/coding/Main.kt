package com.hsbc.coding

import com.hsbc.coding.model.Employee
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.math.RoundingMode
import java.text.DecimalFormat


fun main() {
    val employees = arrayListOf<Employee>()

    /* Sample input for employees list as:*/
    employees.add(Employee("Ashish", "A", "IT", "Pune", "Software Engineer", 10000.toDouble()))
    employees.add(Employee("Amit", "R", "HR", "Pune", "Recruiter", 12000.toDouble()))
    employees.add(Employee("Ramesh", "D", "HR", "Pune", "Senior Recruiter", 14000.toDouble()))
    employees.add(Employee("Jaya", "S", "IT", "Pune", "Tech Lead", 15000.toDouble()))
    employees.add(Employee("Smita", "M", "IT", "Bangalore", "Recruiter", 16000.toDouble()))
    employees.add(Employee("Umesh", "A", "IT", "Bangalore", "Software Engineer", 12000.toDouble()))
    employees.add(Employee("Pooja", "R", "HR", "Bangalore", "Software Engineer", 12000.toDouble()))
    employees.add(Employee("Ramesh", "D", "HR", "Pune", "Recruiter", 16000.toDouble()))
    employees.add(Employee("Bobby", "S", "IT", "Bangalore", "Tech Lead", 20000.toDouble()))
    employees.add(Employee("Vipul", "M", "IT", "Bangalore", "Software Engineer", 14000.toDouble()))

    val app = FindAverageSalaryApp()
    app.findAverageSalary(employees)
}

/**
 * Class that calculates average salary by location
 */
class FindAverageSalaryApp {

    /**
     * Method to find [Employee] average salary by office location
     * @param employees list of [Employee]
     */
    fun findAverageSalary(employees: List<Employee>) {
        runBlocking {
            val averageSalaries = processAverageSalariesByLocation(employees)
            averageSalaries.forEach { location ->
                location.value.forEach { (designation, averageSalary) ->
                    println("${location.key} --> $designation --> ${averageSalary.roundOffDecimal()}")
                }
            }
        }
    }

    /**
     * Method to calculate salaries of an [Employee] group by office location
     * @param employees list of [Employee]
     * @return map of employee's average salary based on office location
     */
    private suspend fun processAverageSalariesByLocation(employees: List<Employee>)
            : Map<String, Map<String, Double>> =
        coroutineScope {
            val officeLocations = employees.map { it.officeLocation }.distinct()
            val averageSalaries = officeLocations.map { officeLocation ->
                async {
                    val employeesInLocation = employees.filter { it.officeLocation == officeLocation }
                    employeesInLocation.groupBy(Employee::designation)
                        .mapValues { (_, employeesWithSameDesignation) ->
                            employeesWithSameDesignation.map(Employee::salary).average()
                        }
                }
            }.awaitAll()

            officeLocations.zip(averageSalaries).toMap()
        }
}

/**
 * Extension function to round off decimal up to 2 places
 */
fun Double.roundOffDecimal(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()
}