package com.lya.gradle

import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.TaskContainerAdaptor
import com.android.build.gradle.internal.TaskFactory
import org.gradle.api.Action
import org.gradle.api.Project
import com.android.build.gradle.api.ApplicationVariant
import org.gradle.api.Plugin
import org.gradle.api.Task

class GreetingPlugin implements Plugin<Project> {

    protected Project project
    protected TaskFactory taskFactory
    protected boolean isBuildingPlugin = false

    void apply(Project project) {
        this.project = project

        def startParameter = project.gradle.startParameter
        def targetTasks = startParameter.taskNames

        targetTasks.each {
            if (it.contains("assemblePlugin") || it.contains("aP")) {
                isBuildingPlugin = true
            }
        }

        project.extensions.create('packageIdModifier', Extension)

        taskFactory = new TaskContainerAdaptor(project.tasks)
        project.afterEvaluate {
            project.android.applicationVariants.each { ApkVariant variant ->
                println variant.buildType.name
                println "assemblePlugin${variant.name.capitalize()}"

                if (variant.buildType.name.equalsIgnoreCase("release")) {
                    final def variantPluginTaskName = "assemblePlugin${variant.name.capitalize()}"
                    final def configAction = new AssemblePlugin.ConfigAction(project, variant)
//
                    taskFactory.create(variantPluginTaskName, AssemblePlugin, configAction)

                    taskFactory.named("assemblePlugin", new Action<Task>() {
                        @Override
                        void execute(Task task) {
                            task.dependsOn(variantPluginTaskName)
                        }
                    })
                }
            }
        }

        project.task('assemblePlugin', dependsOn: "assembleRelease", group: 'build', description: 'Build plugin apk')
    }

    protected final Extension getPackageIdModifier() {
        return this.project.packageIdModifier
    }
}