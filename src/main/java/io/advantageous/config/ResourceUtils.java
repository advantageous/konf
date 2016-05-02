package io.advantageous.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/** Used by sub-projects to find resources. */
@SuppressWarnings("WeakerAccess")
public class ResourceUtils {


    public static InputStream findResource(final String resourceName) {
        InputStream resourceAsStream = ConfigLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (resourceAsStream == null) {
            resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        }

        if (resourceAsStream == null) {
            final URI uri = URI.create(resourceName);
            if (uri.getScheme().equals("file")) {
                try {
                    final String path = uri.getPath();
                    resourceAsStream = new FileInputStream(new File(path));
                } catch (FileNotFoundException e) {
                    throw new IllegalArgumentException("File resource could not be loaded " + resourceName);
                }
            } else if (uri.getScheme().equals("http")) {
                try {
                    URL url = uri.toURL();
                    resourceAsStream = url.openStream();
                } catch (Exception e) {
                    throw new IllegalArgumentException("Web resource could not be loaded " + resourceName);
                }
            } else if (uri.getScheme().equals("classpath")) {
                String path = uri.getSchemeSpecificPart();
                if (path.startsWith("//")) {
                    path = path.substring(2);
                } else if (path.startsWith("/")) {
                    path = path.substring(1);
                }
                resourceAsStream = ConfigLoader.class.getClassLoader().getResourceAsStream(path);
                if (resourceAsStream == null) {
                    resourceAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
                }
            }
        }

        if (resourceAsStream == null) {
            throw new IllegalArgumentException("resources could not be loaded " + resourceName);
        }

        return resourceAsStream;
    }
}
