package com.danke.android.maven.publish

import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.model.ObjectFactory
import org.gradle.util.GradleVersion

/**
 * @author danke
 * @date 2018/7/25
 */
class AndroidLibrary implements SoftwareComponentInternal {

    private final String CONFIG_COMPILE = 'compile'
    private final String CONFIG_IMPLEMENTATION = 'implementation'
    private final String CONFIG_API = 'api'

    private final Set<UsageContext> usages = new DefaultDomainObjectSet<UsageContext>(UsageContext)

    AndroidLibrary(Project project) {
        ObjectFactory objectFactory = project.getObjects()

        def isNewerThan4_1 = GradleVersion.current() > GradleVersion.version('4.1')
        Usage api = objectFactory.named(Usage.class, isNewerThan4_1 ? Usage.JAVA_API : 'for compile')
        Usage runtime = objectFactory.named(Usage.class, isNewerThan4_1 ? Usage.JAVA_RUNTIME : 'for runtime')

        addUsageContextFromConfiguration(project, CONFIG_COMPILE, api)
        addUsageContextFromConfiguration(project, CONFIG_API, api)
        addUsageContextFromConfiguration(project, CONFIG_IMPLEMENTATION, runtime)
    }

    private addUsageContextFromConfiguration(Project project, String configuration, Usage usage) {
        try {
            def configurationObj = project.configurations.getByName(configuration)
            def dependency = configurationObj.dependencies
            if (!dependency.isEmpty()) {
                def libraryUsage = new LibraryUsage(dependency, usage)
                usages.add(libraryUsage)
            }
        } catch (UnknownDomainObjectException ignore) {
            // cannot find configuration
        }
    }

    @Override
    String getName() {
        return 'android'
    }

    @Override
    Set<UsageContext> getUsages() {
        return usages
    }

    private static class LibraryUsage implements UsageContext {

        private final DomainObjectSet<Dependency> dependencies
        private final Usage usage

        LibraryUsage(DomainObjectSet<Dependency> dependencies, Usage usage) {
            this.dependencies = dependencies
            this.usage = usage
        }

        @Override
        Usage getUsage() {
            return usage
        }

        @Override
        Set<PublishArtifact> getArtifacts() {
            new LinkedHashSet<PublishArtifact>()
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            dependencies.withType(ModuleDependency)
        }

        @Override
        String getName() {
            return 'runtime'
        }

        @Override
        AttributeContainer getAttributes() {
            return null
        }
    }
}
