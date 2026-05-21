package io.github.hwangsihu.materialthemebuilder;

import com.android.build.api.variant.AndroidComponentsExtension;
import com.android.build.api.variant.Variant;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.TaskProvider;

@SuppressWarnings("unused")
public class MaterialThemeBuilderPlugin implements Plugin<Project> {

    private final Logger logger = Logging.getLogger(MaterialThemeBuilderPlugin.class);

    @Override
    public void apply(Project project) {
        var extension = project.getExtensions().create(
                MaterialThemeBuilderExtension.class, "materialThemeBuilder", MaterialThemeBuilderExtension.class);

        project.getPlugins().withId("com.android.base", plugin -> {
            @SuppressWarnings("unchecked")
            AndroidComponentsExtension<?, ?, Variant> androidComponents =
                    (AndroidComponentsExtension<?, ?, Variant>) project.getExtensions().getByType(AndroidComponentsExtension.class);

            androidComponents.onVariants(androidComponents.selector().all(), variant -> {
                String variantName = variant.getName();
                String capitalized = Util.capitalize(variantName);

                {
                    String taskName = "generate" + capitalized + "MaterialThemeBuilderRes";
                    TaskProvider<GenerateResTask> task = project.getTasks().register(
                            taskName, GenerateResTask.class, extension);
                    variant.getSources().getRes().addGeneratedSourceDirectory(task, GenerateResTask::getOutputDir);
                }

                {
                    String taskName = "generate" + capitalized + "MaterialThemeBuilderSource";
                    TaskProvider<GenerateJavaTask> task = project.getTasks().register(
                            taskName, GenerateJavaTask.class, extension);
                    variant.getSources().getJava().addGeneratedSourceDirectory(task, GenerateJavaTask::getOutputDir);
                }
            });
        });
    }
}
