package online.awet.system.core;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class SystemUtils {

    public static List<String> getClassesFromPackage(String packageName) {
        String path = packageName.replace('.', '/');
        List<String> results = new ArrayList<>();

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URI uri = resources.nextElement().toURI();
                // If the application is packaged into a .jar file, the classLoader cant read the files as when running from
                // java IDE using target/ folder artifacts.
                if (uri.getScheme().equals("jar")) {
                    results.addAll(getClassesFromJar(uri, path));
                } else {
                    results.addAll(getClassesFromDirectory(new File(uri.getPath()), packageName));
                }
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println("An error occurred while trying to load resources from " + path );
        }
        return results;
    }

    private static List<String> getClassesFromJar(URI uri, String path) throws IOException, URISyntaxException {
        List<String> classes = new ArrayList<>();
        String jarPath = uri.getSchemeSpecificPart().split("!")[0];

        try (JarFile jarFile = new JarFile(new File(new URI(jarPath)))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(path) && entryName.endsWith(".class")) {
                    String className = entryName.replace('/', '.').replace(".class", "");
                    classes.add(className);
                }
            }
        }
        return classes;
    }

    private static List<String> getClassesFromDirectory(File directory, String packageName) {
        List<String> classes = new ArrayList<>();

        if (!directory.exists()) return classes;

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(getClassesFromDirectory(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(packageName + '.' + file.getName().replace(".class", ""));
                }
            }
        }
        return classes;
    }

    public static <T> Map<Class<? extends T>, T> instantiateClassesAnnotatedBy(String annotationName, String packageName) {
        Map<Class<? extends T>, T> results = new HashMap<>();
        List<String> classNames = getClassesFromPackage(packageName);

        for (String className : classNames) {
            try {
                Class<? extends T> klass = (Class<? extends T>) Class.forName(className);

                if (klass.isAnnotation()) continue; // Skip annotations itself

                for (Annotation annotation : klass.getAnnotations()) {
                    boolean hasAnnotation = annotation.annotationType().getSimpleName().equals(annotationName);
                    if (hasAnnotation) {
                        results.put(klass, klass.getDeclaredConstructor().newInstance());
                        break;
                    }
                }
            } catch (ClassNotFoundException e) {
                System.out.println("Class " + className + " not found.");
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                System.out.println(
                        "The class " + className + " could not be initialized, please make sure to declare a no args constructor."
                );
            }
        }
        return results;
    }
}
