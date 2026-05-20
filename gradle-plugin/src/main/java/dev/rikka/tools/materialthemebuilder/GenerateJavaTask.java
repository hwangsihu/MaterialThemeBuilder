package dev.rikka.tools.materialthemebuilder;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public abstract class GenerateJavaTask extends DefaultTask {

    private static final String CLASSNAME = "Harmonization";

    private final MaterialThemeBuilderExtension extension;

    @OutputDirectory
    public abstract DirectoryProperty getOutputDir();

    @Inject
    public GenerateJavaTask(MaterialThemeBuilderExtension extension) {
        this.extension = extension;
    }

    @TaskAction
    public void generate() throws IOException {
        File dir = getOutputDir().get().getAsFile();
        Util.clearDir(dir);

        if (extension.getPackageName() == null) {
            return;
        }

        File file = new File(dir, String.join("/", (extension.getPackageName() + "." + CLASSNAME).split("\\.")) + ".java");
        Util.createFile(file);

        try (PrintStream os = new PrintStream(file)) {
            write(os);
        }
    }

    public void write(PrintStream os) {
        String content = "package %s;\n" +
                "\n" +
                "public final class %s {\n" +
                "    public static final int[] HARMONIZED_COLOR_ATTRIBUTES = {%s};\n" +
                "}\n";

        List<String> attrs = new ArrayList<>();

        for (MaterialThemeBuilderExtension.ExtendedColor extendedColor : extension.getExtendedColors()) {
            if (!extendedColor.isHarmonize()) {
                continue;
            }

            for (MaterialTheme.Color color : MaterialTheme.COLORS) {
                attrs.add("R.attr." + color.getAttributeName(extendedColor.getNameForAttribute()));
            }
        }

        os.printf(content,
                extension.getPackageName(),
                CLASSNAME,
                String.join(", ", attrs.toArray(new String[0])));
    }
}
