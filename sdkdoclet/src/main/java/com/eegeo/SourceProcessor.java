package com.eegeo;

import com.sun.javadoc.*;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SourceProcessor extends Doclet {
    private static String eeGeoInternal = "eegeo.internal";
    private static String eeGeoExamples = "eegeo.examples";
    private static List<String> primaryApiClasses = Arrays.asList(
            "EegeoApi",
            "EegeoMap",
            "MapView",
            "EegeoMapOptions",
            "OnInitialStreamingCompleteListener",
            "OnMapReadyCallback",
            "Marker",
            "MarkerOptions",
            "ElevationMode",
            "Polygon",
            "PolygonOptions",
            "Polyline",
            "PolylineOptions",
            "OnMarkerClickListener",
            "CameraPosition",
            "CameraUpdate",
            "CameraUpdateFactory",
            "IndoorMap",
            "OnFloorChangedListener",
            "OnIndoorEnteredListener",
            "OnIndoorExitedListener",
            "LatLng",
            "LatLngAlt",
            "LatLngBounds",
            "Projection",
            "Promise",
            "Ready",
            "BlueSphere"
    );
    // Would be nicer to extract this list automatically, but that would require two passes
    private static List<String> subClasses = Arrays.asList(
            "CameraPosition.Builder",
            "CameraUpdate.CameraUpdateType",
            "EegeoMap.OnCameraMoveListener",
            "EegeoMap.OnMapClickListener",
            "MarkerOptions.MarkerElevationMode",
            "LatLngBounds.Builder"
    );

    private static List<String> undocumentedTypes = new ArrayList<String>();
    private static void checkType(Type type) {
        if (type.isPrimitive()) {
            return;
        }
        String fullTypeName = type.qualifiedTypeName();
        if (fullTypeName.startsWith("java") || fullTypeName.startsWith("android")) {
            return;
        }

        String typename = type.typeName();
        if (!primaryApiClasses.contains(typename) && !subClasses.contains(typename)) {
            if (!undocumentedTypes.contains(typename)) {
                undocumentedTypes.add(typename);
            }
        }
    }

    private static String getTypeName(Type type) {
        ParameterizedType parameterizedType = type.asParameterizedType();
        if (parameterizedType == null) {
            return type.typeName();
        }
        String parameterizedTypeName = parameterizedType.typeName();
        for (int i=0; i<parameterizedType.typeArguments().length; i++) {
            if (i == 0) {
                parameterizedTypeName += "\\<";
            }
            parameterizedTypeName += parameterizedType.typeArguments()[i].typeName();
            if (i < parameterizedType.typeArguments().length - 1) {
                parameterizedTypeName += ", ";
            } else {
                parameterizedTypeName += "\\>";
            }
        }
        return parameterizedTypeName;
    }

    public static boolean start(RootDoc root) {
        // command line params
        String outputDir = null;
        for (int i=0; i<root.options().length; i++) {
            String[] opt = root.options()[i];
            if (opt[0].equals("-d")) {
                outputDir=opt[1];
            }
        }
        ClassDoc[] classes = root.classes();
        generateMarkdownForClasses(outputDir, classes);
        System.out.println("Undocumented types: \n");
        System.out.println(undocumentedTypes.toString());
        return true;
    }

    private static void generateMarkdownForClasses(String outputDir, ClassDoc[] classes) {
        for (int i = 0; i < classes.length; ++i) {
            ClassDoc cd = classes[i];
            if (primaryApiClasses.contains(cd.name())) {
                Path apiPath = Paths.get(outputDir);
                Path classDocPath = apiPath.resolve(cd.name());
                if (!Files.exists(classDocPath)) {
                    try {
                        Files.createDirectories(classDocPath);
                    } catch (java.io.IOException e) {
                        System.out.println("Failed to create output directory " + classDocPath.toString());
                        return;
                    }
                }
                Path classDocFile = classDocPath.resolve("index.md");
                if (!Files.exists(classDocFile)) {
                    try {
                        Files.createFile(classDocFile);
                    } catch (java.io.IOException e) {
                        System.out.println("Failed to create output file " + classDocFile.toString());
                        return;
                    }
                }
                generateMarkdown(classDocFile, cd);
            }
        }
    }

    private static void generateMarkdown(Path outputFilePath, ClassDoc classDoc) {
        try {
            FileWriter file = new FileWriter(outputFilePath.toFile());

            // Template parameters
            file.write("---\n");
            file.write("layout: apijava\n");
            file.write("title: " + classDoc.name() + "\n");
            file.write("package: " + classDoc.containingPackage().toString() + "\n");
            Tag[] examples = classDoc.tags(eeGeoExamples);
            if (examples.length > 0) {
                file.write("examples: ");
                for (int i = 0; i < examples.length; i++) {
                    file.write(examples[i].text());
                }
                file.write("\n");
            }
            file.write("---\n");

            printClassDoc(file, classDoc);

            file.close();
        } catch (java.io.IOException e) {
            System.out.println("Failed to write markdown to " + outputFilePath.toString());
        }
    }

    private static boolean hasPublicItems(ProgramElementDoc[] elements){
        if (elements.length == 0) {
            return false;
        }
        for (ProgramElementDoc elementDoc : elements) {
            if (elementDoc.tags("eegeo.internal").length == 0) {
                return true;
            }
        }
        return false;
    }

    private static void printClassDoc(FileWriter file, ClassDoc classDoc) {
        try {
            file.write(addInternalLink(classDoc.commentText()) + "\n\n");
            if (hasPublicItems(classDoc.fields())) {
                file.write("### Fields\n\n");
                printFields(file, classDoc.fields());
            }
            if (classDoc.isEnum()) {
                file.write("### enum " + classDoc.name() + "\n\n");
                printEnum(file, classDoc);
            } else {
            if (hasPublicItems(classDoc.constructors())) {
                file.write("### Constructors\n\n");
                printConstructors(file, classDoc.constructors());
            }
            if (hasPublicItems(classDoc.methods())) {
                file.write("### Methods\n\n");
                printMembers(file, classDoc.methods());
            }
            }
            if (hasPublicItems(classDoc.innerClasses())) {
                for (ClassDoc cd : classDoc.innerClasses()) {
                    file.write("<a name = \"" + cd.name().replaceAll("\\.", "").toLowerCase() +"\"></a>\n\n");
                    if (cd.isEnum()) {
                        file.write("### enum " + cd.name() + "\n\n");
                        printEnum(file, cd);
                    } else {
                        file.write("### class " + cd.name() + "\n\n");
                        printClassDoc(file, cd);
                    }
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Failed to write classdoc for " + classDoc.name());
        }
    }

    private static String buildSignature(ExecutableMemberDoc method) {
        String signature =  method.name() + "(";
        Parameter[] params = method.parameters();
        for (int i = 0; i < params.length; i++) {
            checkType(params[i].type());
            signature += addInternalLink(getTypeName(params[i].type()));
            signature += " " + params[i].name();
            if (i < params.length - 1) {
                signature += ", ";
            }
        }
        signature += ")";
        return signature;
    }

    // doesn't current deal with top level enum classes
    private static void printEnum(FileWriter file, ClassDoc enumDoc) {
        try {
            file.write("| Enum value        | Description   |\n");
            file.write("| ---               | ---           |\n");
            for (FieldDoc enumVal : enumDoc.enumConstants()) {
                file.write("|" + enumVal.name());
                file.write("|" + addInternalLink(enumVal.commentText()) + "|\n");
            }
            file.write("\n");
        } catch (java.io.IOException e) {
            System.out.println("Failed to write enum");
        }
    }

    private static void printFields(FileWriter file, FieldDoc[] fieldDocs) {
        try {
            for (FieldDoc fd: fieldDocs) {
                if (fd.tags(eeGeoInternal).length == 0) {
                    file.write("#### " + fd.modifiers() + " " + addInternalLink(fd.type().simpleTypeName()) + " " + fd.name() + "\n\n");
                    file.write(addInternalLink(fd.commentText()) + "\n\n");
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Failed to write fields");
        }
    }

    private static void printConstructors(FileWriter file, ConstructorDoc[] constructorDocs) {
        try {
            for (ConstructorDoc cd : constructorDocs) {
                if (cd.tags(eeGeoInternal).length == 0) {
                    file.write("#### " + buildSignature(cd) + "\n\n");
                    file.write(addInternalLink(cd.commentText()) + "\n\n");
                    printParameterTable(file, cd);
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Failed to write constructors");
        }
    }

    private static void printMembers(FileWriter file, MethodDoc[] mems) {
        try {
            for (MethodDoc methodDoc : mems) {
                if (methodDoc.tags(eeGeoInternal).length == 0) {
                    checkType(methodDoc.returnType());
                    file.write("#### " +
                            addInternalLink(getTypeName(methodDoc.returnType())) +
                            " " +
                            buildSignature(methodDoc) +
                            "\n\n");

                    file.write(addInternalLink(methodDoc.commentText()) + "\n\n");
                    Tag[] returnTags = methodDoc.tags("return");
                    if (returnTags.length > 0) {
                        file.write("Returns: " + returnTags[0].text() + "\n\n");
                    }
                    Tag[] codeIntroTags = methodDoc.tags("eegeo.codeintro");
                    if (codeIntroTags.length > 0) {
                        file.write(addInternalLink(codeIntroTags[0].text()) + "\n\n");
                    }
                    Tag[] codeTags = methodDoc.tags("eegeo.code");
                    if (codeTags.length > 0) {
                        for (Tag codeTag : codeTags) {
                            // map "pre" tags to highlight
                            String code = codeTag.text();
                            code = code.replaceAll("<pre>", "{% highlight java %}");
                            code = code.replaceAll("</pre>", "{% endhighlight %}");
                            code = code.replaceAll("&lt;", "<");
                            code = code.replaceAll("&gt;", ">");
                            file.write(code + "\n");
                        }
                    }
                    printParameterTable(file, methodDoc);
                }
            }
        } catch (java.io.IOException e) {
            System.out.println("Failed to write member data");
        }
    }

    private static void printParameterTable(FileWriter file, ExecutableMemberDoc member) {
        try {
            ParamTag[] paramtags = member.paramTags();
            Parameter[] params = member.parameters();
            if (paramtags.length > 0) {
                file.write("| Type          | argument          | Description   |\n");
                file.write("| ---               | ---           | ---           |\n");
                for (int j = 0; j < paramtags.length; ++j) {
                    Type paramType = params[j].type();
                    file.write("| " + addInternalLink(getTypeName(paramType))
                            + " | " + paramtags[j].parameterName()
                            + " | " + addInternalLink(paramtags[j].parameterComment().replaceAll("\n", ""))
                            + " |\n");
                }
                file.write("\n");
            }
        } catch (java.io.IOException e) {
            System.out.println("Failed to write params");
        }
    }

    private static String addInternalLink(String rawString) {
        String[] splitStr = rawString.split("\\s+");
        String resultString = "";
        for (int i=0; i < splitStr.length; i++) {
            String baseClassName = splitStr[i];
            if (baseClassName.contains("\\<")) {
                baseClassName = baseClassName.substring(0, baseClassName.indexOf("\\<"));
            }
            if (primaryApiClasses.contains(baseClassName)) {
                resultString += "[" + splitStr[i] + "]" + "({{ site.baseurl }}/docs/api/" + baseClassName + ")";
            } else if (subClasses.contains(splitStr[i])) {
                String[] classNames = splitStr[i].split("\\.");

                String anchorname = (classNames[0] + classNames[1]).toLowerCase();
                resultString += "[" + splitStr[i] + "]" +
                        "({{ site.baseurl }}/docs/api/" + classNames[0] + "/#" + anchorname + ")";
            } else {
                resultString += splitStr[i];
            }
            if (i < splitStr.length - 1) {
                resultString += " ";
            }
        }
        return resultString;
    }

    // The gradle javadoc task expects that these options are always supported
    public static int optionLength(String option) {
        if (option.equals("-d")) {
            return 2;
        }
        if (option.equals("-doctitle")) {
            return 2;
        }
        if (option.equals("-windowtitle")) {
            return 2;
        }
        return 0;
    }

    public static boolean validOptions(String options[][],
                                       DocErrorReporter reporter) {
        return true;
    }

    // Need to specify this to get enums to work
    public static LanguageVersion languageVersion() {
        // options are 1.1 and 1.5...
        return LanguageVersion.JAVA_1_5;
    }
}