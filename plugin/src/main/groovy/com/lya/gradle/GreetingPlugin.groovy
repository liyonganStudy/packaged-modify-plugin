package com.lya.gradle

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApkVariant
import com.android.build.gradle.internal.TaskContainerAdaptor
import com.android.build.gradle.internal.TaskFactory
import com.lya.gradle.hook.StripClassAndResTransform
import com.lya.gradle.hook.TaskHookerManager
import org.gradle.api.Action
import org.gradle.api.InvalidUserDataException
import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.Task

class GreetingPlugin implements Plugin<Project> {

    private Project project

    void apply(Project p) {
        project = p

        def isBuildingPlugin = false
        def targetTasks = project.gradle.startParameter.taskNames
        targetTasks.each {
            if (it.contains("assemblePlugin") || it.contains("aP")) {
                isBuildingPlugin = true
            }
        }
        project.extensions.create('packageIdModifier', Extension)

        if (!isBuildingPlugin) {
            return
        } else {
            project.task('assemblePlugin', dependsOn: "assembleDebug", group: 'build', description: 'Build plugin apk')
        }

        // AppPlugin.createExtension中创建了AppExtension
        project.android.registerTransform(new StripClassAndResTransform(project))

        TaskHookerManager taskHookerManager = new TaskHookerManager(project)
        taskHookerManager.registerTaskHookers()

        TaskFactory taskFactory = new TaskContainerAdaptor(project.tasks)
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

                packageIdModifier.with {
                    packageName = variant.applicationId
                    packagePath = packageName.replaceAll('\\.', File.separator)
                }
                checkConfig()
            }
        }
    }

    private void checkConfig() {
        int packageId = packageIdModifier.packageId

        if (packageId == 0) {
            def err = new StringBuilder('you should set the packageId in build.gradle,\n ')
            err.append('please declare it in application project build.gradle:\n')
            err.append('    packageIdModifier {\n')
            err.append('        packageId = 0xXX \n shareCommonAarWithHost = true')
            err.append('    }\n')
            err.append('apply for the value of packageId, please contact with zhengtao@didichuxing.com\n')
            throw new InvalidUserDataException(err.toString())
        }

        if (packageIdModifier.shareCommonAarWithHost) {
            File commonAarRFile = new File(project.rootDir, "common_R.txt")
            if (!commonAarRFile.exists()) {
                def err = "The common aar R text  doesn't exist! File: ${commonAarRFile.absoluteFile}"
                throw new InvalidUserDataException(err)
            } else {
                packageIdModifier.commonSymbolFile = commonAarRFile
            }
        }
    }

    private Extension getPackageIdModifier() {
        return project.packageIdModifier
    }

}