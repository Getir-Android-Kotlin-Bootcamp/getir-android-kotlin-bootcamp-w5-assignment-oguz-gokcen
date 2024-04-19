package com.example.coroutineshomework

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //startCar()  // slayt 6

        //startJob()  //7,8

        //dispatchersExample()

        //withContextExample()

        //coroutineJobExample()

        //flowExample()

        //supervisorExample()

        //unsupervisorExample()

        //supervisorscopeExample()
    }

    private fun startJob() {
        val job = GlobalScope.async {
            println("Fetching data")
            val result = fetchDataFromNetwork()
            result
        }

        runBlocking {
            val data = job.await()
            println(data)
        }
    }

    private fun startCar() {
        CoroutineScope(IO).launch {
            println("Starting Car")
            delay(1000L)
            println("Car Started")
        }
    }

    private fun dispatchersExample() {
        CoroutineScope(Main).launch {
            println(getUIData())
        }

        CoroutineScope(IO).launch {
            println(fetchDataFromNetwork())
        }

        CoroutineScope(Default).launch {
            var result = 1
            repeat(10) {
                result *= 2
            }
            println("CPU result $result")
        }
    }

    private fun withContextExample() {
        CoroutineScope(Main).launch {
            val result = withContext(IO) {
                // Perform IO operation in the IO dispatcher
                fetchDataFromNetwork()
            }
            updateUI(result)
        }
    }

    private fun coroutineJobExample() {
        val job = GlobalScope.async {
            delay(5000L)
            val result = fetchDataFromNetwork()
            result
        }
        CoroutineScope(IO).launch(job) {
            try {
                withTimeout(5000) {
                    println("waiting data")
                    val data = job.await()
                    println(data)

                }
            } catch (e: TimeoutCancellationException) {
                // Task took more than 5 seconds
                // Cancel the job
                job.cancel()
                // Perform actions accordingly
                println("Job Canceled")
            }
        }
    }

    private fun flowExample() {
        val flow = flow {
            for (i in 1..5) {
                // Emit each number with a delay of 500 milliseconds
                emit(i)
                delay(500)
            }
        }
        runBlocking {
            launch {
                flow.collect { value ->
                    println("Received: $value")
                }
            }
            println("Flow collection started")


            delay(3000)
            println("Flow collection completed")
        }
    }

    private fun supervisorExample() {
        val supervisorJob = IO + SupervisorJob()
        val scope = CoroutineScope(supervisorJob)
        val job1 = scope.launch {
            println("First child coroutine is executing")
            delay(500)
            throw Exception("First child coroutine failed")
        }

        // Launch the second child coroutine
        val job2 = scope.launch {
            println("Second child coroutine is executing")
            delay(1000)
            println("Second child coroutine completed")
        }
        val jobs = mutableListOf(job1, job2)
        runBlocking {
            jobs.forEach { it.join() }
            println(scope.isActive)
        }
    }

    private fun unsupervisorExample(){
        val scope =  CoroutineScope(Job())


            val job1=scope.launch {
                println("First child coroutine is executing")
                delay(500)
                throw RuntimeException("First child coroutine failed")
            }

            // Launch the second child coroutine
            val job2=scope.launch {
                println("Second child coroutine is executing")
                delay(1000)
                println("Second child coroutine completed")
            }
            val jobs = mutableListOf(job1, job2)
            runBlocking {
                jobs.forEach { it.join() }
                println(scope.isActive)
            }
    }

    fun supervisorscopeExample(){
        runBlocking {
            supervisorScope {
                launch {
                    // Child coroutine 1
                    delay(100)
                    println("Child coroutine 1 completed")
                }.join()

                launch {
                    // Child coroutine 2
                    delay(200)

                    throw Exception("Child coroutine 2 failed")
                }
                launch {
                    // Child coroutine 3
                    delay(300)
                    println("Child coroutine 3 completed")
                }

            }
        }
    }
    private suspend fun fetchDataFromNetwork(): String {
        // Simulate fetching data from a network
        delay(2000) // Simulate a 2-second delay
        return "Data from network"
    }


    private suspend fun getUIData():String{
        delay(500L)
        return "UI data"
    }

    private fun updateUI(result: String){
        println("UI Updated as $result")
    }



}