package io.github.hwangsihu.materialthemebuilder;

import io.github.hwangsihu.materialthemebuilder.generator.ColorStateListGenerator;
import io.github.hwangsihu.materialthemebuilder.generator.ValuesAllGenerator;
import io.github.hwangsihu.materialthemebuilder.generator.ValuesV31Generator;
import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class GenerateResTask extends DefaultTask {

    private final MaterialThemeBuilderExtension extension;

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @Inject
    public GenerateResTask(MaterialThemeBuilderExtension extension) {
        this.extension = extension;
    }

    @TaskAction
    public void generate() throws IOException {
        File dir = getOutputDir().get().getAsFile();
        Util.clearDir(dir);

        List<ColorStateListGenerator> colorStateListGenerators = new ArrayList<>();
        if (extension.isGenerateTextColors()) {
            for (String textColor : MaterialTheme.TEXT_COLORS) {
                for (String emphasis : MaterialTheme.TEXT_COLOR_EMPHASIS) {
                    String filename = "color/"
                            + MaterialTheme.getColorStateListFilename(textColor, emphasis)
                            + ".xml";
                    colorStateListGenerators.add(
                            new ColorStateListGenerator(new File(dir, filename), "?" + textColor, emphasis));
                }
            }
        }

        var valuesAllGenerator = new ValuesAllGenerator(new File(dir, "values/values.xml"), extension);
        var valuesV31Generator = new ValuesV31Generator(new File(dir, "values-v31/values.xml"), extension);

        for (ColorStateListGenerator g : colorStateListGenerators) {
            g.generate();
        }
        valuesAllGenerator.generate();
        valuesV31Generator.generate();
    }
}
