package com.lya.gradle.hook

import com.android.build.gradle.api.ApplicationVariant
import com.android.build.gradle.internal.pipeline.TransformTask
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState

class TaskHookerManager {
    private Map<String, GradleTaskHooker> taskHookerMap = new HashMap<>()
    private Project project

    TaskHookerManager(Project project) {
        this.project = project
        project.gradle.addListener(new TaskExecutionListener() {
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
            void afterExecute(Task task, TaskState state) {
                if (task.project == project) {
                    if (task in TransformTask) {
                        taskHookerMap[task.transform.name]?.afterTaskExecute(task)
                    } else {
                        taskHookerMap[task.name]?.afterTaskExecute(task)
                    }
                }
            }
        })
    }

    void registerTaskHookers() {
        project.afterEvaluate {
            project.android.applicationVariants.all { ApplicationVariant appVariant ->
                registerTaskHooker(new MergeManifestsHooker(project, appVariant))
                registerTaskHooker(new PrepareDependenciesHooker(project, appVariant))
                registerTaskHooker(new ProcessResourcesHooker(project, appVariant))
                registerTaskHooker(new MergeResourceTaskHook(project, appVariant))
                registerTaskHooker(new DxTaskHooker(project, appVariant))
                registerTaskHooker(new MergeAssetsHooker(project, appVariant))
            }
        }
    }

    private void registerTaskHooker(GradleTaskHooker taskHooker) {
        taskHookerMap.put(taskHooker.taskName, taskHooker)
    }
}