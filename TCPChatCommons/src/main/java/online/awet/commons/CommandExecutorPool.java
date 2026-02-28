package online.awet.commons;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Singleton that manages a pool of {@link CommandExecutor}, allowing to register and select a {@link CommandExecutor} based on its {@link CommandSignature}.
 */
public class CommandExecutorPool {

    private static final CommandExecutorPool INSTANCE = new CommandExecutorPool();

    private final Map<CommandSignature, CommandExecutor> executors = new HashMap<>();

    public static CommandExecutorPool getInstance() {
        return INSTANCE;
    }

    /**
     * Registers a new {@link CommandExecutor} in the pool.
     * @param signature The signature of the command that the executor can execute.
     * @param executor The executor to register.
     */
    public void register(CommandSignature signature, CommandExecutor executor) {
        CommandExecutor previous = executors.putIfAbsent(signature, executor);
        if (previous != null) {
            throw new IllegalStateException("Command executor for signature already registered: " + signature);
        }
    }

    /**
     * Returns an unmodifiable view of all registered {@link CommandExecutor} instances.
     * Useful for introspection (e.g. listing available commands in a help executor).
     */
    public Collection<CommandExecutor> getAll() {
        return Collections.unmodifiableCollection(executors.values());
    }

    /**
     * Selects a {@link CommandExecutor} based on the {@link CommandSignature} of the given command.
     * @param command The command to select the executor for.
     */
    public void select(Command command) {
        CommandSignature signature = CommandSignature.of(command);
        CommandExecutor executor = executors.get(signature);

        if (executor == null) {
            throw new IllegalArgumentException("No executor found for command signature: " + signature);
        }
        executor.execute(command);
    }

    /**
     * Scans the given package (recursively) for all concrete classes that implement
     * {@link CommandExecutor}, instantiates each via its no-arg constructor, and
     * registers them in the pool. Intended to be called once during initialization
     * by the module that owns the executor implementations.
     *
     * @param packageName The fully-qualified package name to scan, e.g.
     *                    {@code "online.awet.client.executors"}.
     */
    public void loadExecutors(String packageName) {
        List<String> classNames = getClassNamesFromPackage(packageName);

        for (String className : classNames) {
            try {
                Class<?> klass = Class.forName(className);

                if (klass.isInterface() || klass.isAnnotation() || klass.isEnum()) continue;
                if (java.lang.reflect.Modifier.isAbstract(klass.getModifiers())) continue;
                if (!CommandExecutor.class.isAssignableFrom(klass)) continue;

                @SuppressWarnings("unchecked")
                Class<? extends CommandExecutor> executorClass = (Class<? extends CommandExecutor>) klass;
                CommandExecutor executor = executorClass.getDeclaredConstructor().newInstance();
                register(executor.getSignature(), executor);

            } catch (ClassNotFoundException e) {
                System.out.println("Class " + className + " not found.");
            } catch (ClassCastException ignored) {
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                System.out.println(
                        "The class " + className + " could not be initialized, please make sure to declare a no-args constructor."
                );
            }
        }
    }

    private static List<String> getClassNamesFromPackage(String packageName) {
        String path = packageName.replace('.', '/');
        List<String> results = new ArrayList<>();

        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Enumeration<URL> resources = classLoader.getResources(path);

            while (resources.hasMoreElements()) {
                URI uri = resources.nextElement().toURI();
                if (uri.getScheme().equals("jar")) {
                    results.addAll(getClassNamesFromJar(uri, path));
                } else {
                    results.addAll(getClassNamesFromDirectory(new File(uri.getPath()), packageName));
                }
            }
        } catch (IOException | URISyntaxException e) {
            System.out.println("An error occurred while trying to load resources from package: " + packageName);
        }

        return results;
    }

    private static List<String> getClassNamesFromJar(URI uri, String path) throws IOException, URISyntaxException {
        List<String> classes = new ArrayList<>();
        String jarPath = uri.getSchemeSpecificPart().split("!")[0];

        try (JarFile jarFile = new JarFile(new File(new URI(jarPath)))) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(path) && entryName.endsWith(".class")) {
                    classes.add(entryName.replace('/', '.').replace(".class", ""));
                }
            }
        }

        return classes;
    }

    private static List<String> getClassNamesFromDirectory(File directory, String packageName) {
        List<String> classes = new ArrayList<>();

        if (!directory.exists()) return classes;

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    classes.addAll(getClassNamesFromDirectory(file, packageName + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    classes.add(packageName + '.' + file.getName().replace(".class", ""));
                }
            }
        }

        return classes;
    }
}
