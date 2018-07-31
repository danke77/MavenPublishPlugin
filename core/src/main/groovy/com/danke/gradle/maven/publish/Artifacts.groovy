package com.danke.gradle.maven.publish;

import org.gradle.api.Project

/**
 * @author danke
 * @date 2018/7/25
 */
interface Artifacts {
    def all(String publicationName, Project project)
}
