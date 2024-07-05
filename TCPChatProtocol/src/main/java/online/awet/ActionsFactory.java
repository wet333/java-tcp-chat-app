package online.awet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class ActionsFactory {

    public static Set<AbstractAction> getAllActionsInstances() {
        Set<AbstractAction> actions = new HashSet<>();

        try {
            Enumeration<URL> resources = ActionsFactory.class.getClassLoader().getResources("online/awet/userManagement");
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());

                for (File file : directory.listFiles()) {
                    if (file.getName().endsWith(".class")) {
                        String className = file.getName().replace(".class", "");
                        Class<?> clazz = Class.forName("online.awet.userManagement." + className);

                        if (AbstractAction.class.isAssignableFrom(clazz) && !clazz.isInterface()) {
                            Constructor<?>[] constructors = clazz.getConstructors();
                            for (Constructor<?> constructor : constructors) {
                                if (constructor.getParameterCount() == 0) {
                                    AbstractAction action = (AbstractAction) constructor.newInstance();
                                    actions.add(action);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Could not load classes from userManagement package.");
        } catch (ClassNotFoundException e) {
            System.out.println("Could not find class.");
        } catch (InvocationTargetException e) {
            System.out.println("Could not call constructor.");
        } catch (InstantiationException e) {
            System.out.println("Class could not be instantiated.");
        } catch (IllegalAccessException e) {
            System.out.println("Illegal access to class.");
        }
        return actions;
    }

}
