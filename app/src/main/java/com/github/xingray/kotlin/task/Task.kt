package com.github.xingray.kotlin.task

interface Task {
    fun calc(): Int
    fun exec(progressCallback: ((progress: Int, total: Int) -> Unit)?): Result
}