import org.gradle.api.Project
import org.gradle.kotlin.dsl.ScriptHandlerScope

fun Project.containerTags(project: Project): Set<String> = project.findProperty("containerTags")?.toString()?.split(",")?.toSet() ?: setOf()
