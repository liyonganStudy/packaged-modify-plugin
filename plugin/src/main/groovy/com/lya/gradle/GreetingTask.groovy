package com.lya.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class GreetingTask extends DefaultTask {

    @TaskAction
    def greet() {
        println "dfadlsfjaldskjfalsdjkf"
    }
}