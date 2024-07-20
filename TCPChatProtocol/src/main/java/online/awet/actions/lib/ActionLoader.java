package online.awet.actions.lib;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class ActionLoader {

//    public static void main(String[] args) {
//        Set<AbstractAction> actions = loadActions("online.awet.actions");
//        for (AbstractAction action : actions) {
//            System.out.println(action.getClass().getName());
//        }
//    }

    public static Set<AbstractAction> loadActions(String packageName) {
        Set<AbstractAction> actions = new HashSet<>();

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');

        Set<File> actionFiles = new HashSet<>();

        try {
            Enumeration<URL> resources = classLoader.getResources(path);
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                actionFiles.add(new File(resource.getFile()));
            }
            for (File file : actionFiles) {
                actions.addAll(findClasses(file, packageName));
            }
        } catch (IOException e) {
            System.out.println("Error loading actions from package: " + packageName + ", Not found");
        } catch (ClassNotFoundException e) {
            System.out.println("Class not found: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.out.println("InvocationTargetException: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            System.out.println("NoSuchMethodException: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.out.println("IllegalAccessException: " + e.getMessage());
        }
        // TODO: Change error messages to something more descriptive
        return actions;
    }

    private static Set<AbstractAction> findClasses(File directory, String packageName)
            throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Set<AbstractAction> actions = new HashSet<>();

        if (!directory.exists()) {
            return actions;
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursive call
                    actions.addAll(findClasses(file, packageName + "." + file.getName()));
                } else {
                    if (file.getName().endsWith(".class")) {
                        String className = file.getName().replace(".class", "");
                        String fullyQualifiedClassName = packageName + "." + className;
                        Class<?> clazz = Class.forName(fullyQualifiedClassName);

                        if (AbstractAction.class.isAssignableFrom(clazz)) {
                            AbstractAction classAction = (AbstractAction) clazz.asSubclass(AbstractAction.class).getMethod("getInstance").invoke(null);
                            actions.add(classAction);
                        }
                    }
                }
            }
        }
        return actions;
    }
}