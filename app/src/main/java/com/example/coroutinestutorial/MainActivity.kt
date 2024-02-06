package com.example.coroutinestutorial

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kotlin.system.measureTimeMillis

val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.btnStartActivity)
        btnStart.setOnClickListener{
            lifecycleScope.launch {
                while(true){
                    delay(1000L)
                    Log.d(TAG,"Still run...")
                }
            }
            GlobalScope.launch {
                delay(5000L)
                Intent(this@MainActivity, SecondActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

    }

    fun fib(n: Int) : Long {
        return if(n==0) 0
        else if(n==1) 1
        else fib(n-1) + fib(n-2)
    }
    suspend fun doNetworkCall(): String {
        delay(3000L)
        return "Answer 1"
    }
    suspend fun doNetworkCall2(): String {
        delay(3000L)
        return "Answer 2"
    }

    fun tutorial() {
        //1
        GlobalScope.launch {
            delay(1000L)
            Log.d(TAG, "Hello coroutine from thread ${Thread.currentThread().name}")
        }
        Log.d(TAG, "Hello from thread ${Thread.currentThread().name}")
        //2
        GlobalScope.launch {
        val networkCall = doNetworkCall()
        Log.d(TAG, networkCall)
        }
        //3
        GlobalScope.launch(newSingleThreadContext("Mythread")) {}
        //4
//        var tvDummy = findViewById<TextView>(R.id.tvDummy)
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Starting coroutine in thread ${Thread.currentThread().name}")
            val answer = doNetworkCall()
            withContext(Dispatchers.Main) {
                Log.d(TAG, "Setting coroutine in thread ${Thread.currentThread().name}")
//                tvDummy.text = answer
            }
        }
        //5
        Log.d(TAG, "Before runBlocking")
        runBlocking {
            launch {
                Log.d(TAG, "Start IO Coroutine 1")
                delay(3000L)
                Log.d(TAG, "End IO Coroutine 1")
            }
            launch {
                Log.d(TAG, "Start IO Coroutine 2")
                delay(3000L)
                Log.d(TAG, "End IO Coroutine 2")
            }
            Log.d(TAG, "Start runBlocking")
            delay(5000L)
            Log.d(TAG, "End runBlocking")
        }
        Log.d(TAG, "After runBlocking")
        //6
        val job = GlobalScope.launch(Dispatchers.Default) {
//            repeat(5){
//                Log.d(TAG,"Coroutine is still working...")
//                delay(1000L)
//            }
            Log.d(TAG,"Starting long running calc...")
            withTimeout(3000L){
                for(i in 30..40) {
                    if(isActive){
                        Log.d(TAG,"Result for i = $i: ${fib(i)}")
                    }
                }
                Log.d(TAG,"Ending long running calc...")
            }
        }
        runBlocking {
            job.join()
            delay(2000L)
            job.cancel()
            Log.d(TAG,"Canceled job")
        }
        //7
        GlobalScope.launch(Dispatchers.IO) {
            val time = measureTimeMillis {
                val answer1 = async {
                    doNetworkCall()
                }
                val answer2 = async {
                    doNetworkCall2()
                }
                Log.d(TAG, "Answer 1 is ${answer1.await()}")
                Log.d(TAG, "Answer 2 is ${answer2.await()}")
            }
            Log.d(TAG, "Request took $time mils")
        }
        //8
        val btnStart = findViewById<Button>(R.id.btnStartActivity)
        btnStart.setOnClickListener{
            lifecycleScope.launch {
                while(true){
                    delay(1000L)
                    Log.d(TAG,"Still run...")
                }
            }
            GlobalScope.launch {
                delay(5000L)
                Intent(this@MainActivity, SecondActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

    }
}