package com.github.dazzbourgh.avroschemageneratot.services

import com.intellij.openapi.project.Project
import com.github.dazzbourgh.avroschemageneratot.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
