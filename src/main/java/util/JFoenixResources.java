package util;

import java.net.URL;

public final class JFoenixResources {

    public static URL load(String path) {
        return JFoenixResources.class.getResource(path);
    }

    private JFoenixResources() {}

}
