package com.danke.gradle.maven.publish

import org.gradle.api.Project
import org.gradle.api.artifacts.ExcludeRule
import org.gradle.api.artifacts.maven.MavenPom

/**
 * @author danke
 * @date 2018/8/1
 */

static def addDependencies(MavenPom pom, Project project) {

    pom.withXml {
        Node root = asNode()
        Node dependenciesNode = root.appendNode('dependencies')

        def addDependency = { configuration, scope ->
            configuration.allDependencies.each {
                if (it.group == null || it.version == null || it.name == null || it.name == 'unspecified') {
                    return // ignore invalid dependencies
                }

                Node dependencyNode = dependenciesNode.appendNode('dependency')
                dependencyNode.appendNode('groupId', it.group)
                dependencyNode.appendNode('artifactId', it.name)
                dependencyNode.appendNode('version', it.version)
                dependencyNode.appendNode('scope', scope)

                // If this dependency is not transitive, we should force exclude all its dependencies from the POM
                if (!it.transitive) {
                    Node exclusionNode = dependencyNode.appendNode('exclusions').appendNode('exclusion')
                    exclusionNode.appendNode('groupId', '*')
                    exclusionNode.appendNode('artifactId', '*')
                }
                // Otherwise add specified exclude rules
                else if (!it.properties.excludeRules.empty) {
                    Node exclusionNode = dependencyNode.appendNode('exclusions').appendNode('exclusion')
                    it.properties.excludeRules.each { ExcludeRule rule ->
                        exclusionNode.appendNode('groupId', rule.group ?: '*')
                        exclusionNode.appendNode('artifactId', rule.module ?: '*')
                    }
                }
            }
        }

        // addDependency(project.configurations.compile, 'compile')
        // addDependency(project.configurations.api, 'compile')
        addDependency(project.configurations.implementation, 'compile')
        addDependency(project.configurations.compileOnly, 'provided')
        addDependency(project.configurations.runtimeOnly, 'runtime')
    }
}

static def removeDependencies(MavenPom pom) {

    pom.withXml {
        Node root = asNode()
        def dependenciesNode = root.children().find {
            it.name().getLocalPart().equals('dependencies')
        }

        if (dependenciesNode != null) {
            root.remove(dependenciesNode)
        }
    }
}
