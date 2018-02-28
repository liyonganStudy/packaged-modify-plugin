package com.lya.gradle

import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.TaskContainerAdaptor
import com.android.build.gradle.internal.TaskFactory
import com.lya.gradle.hook.StripClassAndResTransform
import com.lya.gradle.hook.TaskHookerManager
import org.gradle.api.Action
import org.gradle.api.Project
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

        project.android.registerTransform(new StripClassAndResTransform(project))

        TaskHookerManager taskHookerManager = new TaskHookerManager(project)
        taskHookerManager.registerTaskHookers()

        taskFactory = new TaskContainerAdaptor(project.tasks)
        project.afterEvaluate {
            project.android.applicationVariants.each { ApkVariant variant ->

                if (variant.buildType.name.equalsIgnoreCase("debug")) {
                    final def variantPluginTaskName = "assemblePlugin${variant.name.capitalize()}"
                    final def configAction = new AssemblePlugin.ConfigAction(project, variant)

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

        project.task('assemblePlugin', dependsOn: "assembleDebug", group: 'build', description: 'Build plugin apk')
    }
}