package com.lya.gradle.hook

import com.android.build.gradle.AppExtension
import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.pipeline.TransformTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

class TaskHookerManager {

    private Map<String, GradleTaskHooker> taskHookerMap = new HashMap<>()

    private Project project
    private AppExtension android

    TaskHookerManager(Project project) {
        this.project = project
        android = project.extensions.findByType(AppExtension)
        project.gradle.addListener(new VirtualApkTaskListener())
    }


    void registerTaskHookers() {
        project.afterEvaluate {
            android.applicationVariants.all { ApplicationVariant appVariant ->
                registerTaskHooker(new MergeManifestsHooker(project, appVariant))
                registerTaskHooker(new PrepareDependenciesHooker(project, appVariant))
                registerTaskHooker(new ProcessResourcesHooker(project, appVariant))
//                registerTaskHooker(instantiator.newInstance(MergeAssetsHooker, project, appVariant))
//                registerTaskHooker(instantiator.newInstance(MergeManifestsHooker, project, appVariant))
//                registerTaskHooker(instantiator.newInstance(MergeJniLibsHooker, project, appVariant))
//                registerTaskHooker(instantiator.newInstance(ProcessResourcesHooker, project, appVariant))
//                registerTaskHooker(instantiator.newInstance(ProguardHooker, project, appVariant))
//                registerTaskHooker(instantiator.newInstance(DxTaskHooker, project, appVariant))
            }
        }
    }


    private void registerTaskHooker(GradleTaskHooker taskHooker) {
        taskHooker.setTaskHookerManager(this)
        taskHookerMap.put(taskHooker.taskName, taskHooker)
    }


    def <T> T findHookerByName(String taskName) {
        return taskHookerMap[taskName] as T
    }


    private class VirtualApkTaskListener implements TaskExecutionListener {

        @Override
        void beforeExecute(Task task) {
            if (task.project == project) {
                if (task in TransformTask) {
                    taskHookerMap[task.transform.name]?.beforeTaskExecute(task)
                } else {
                    taskHookerMap[task.name]?.beforeTaskExecute(task)
                }
            }
        }

        @Override
        void afterExecute(Task task, TaskState taskState) {
            if (task.project == project) {
                if (task in TransformTask) {
                    taskHookerMap[task.transform.name]?.afterTaskExecute(task)
                } else {
                    taskHookerMap[task.name]?.afterTaskExecute(task)
                }
            }
        }
    }

}