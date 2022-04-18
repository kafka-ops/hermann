package com.purbon.kafka.hermann;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public final class TestUtils {

    private TestUtils() {}


    public static String getResourceFilename(final String resource) {
        return getResourceFile(resource).toString();
    }

    public static File getResourceFile(final String resource) {
        final URL resourceUrl = TestUtils.class.getResource(resource);
        try {
            return Paths.get(resourceUrl.toURI()).toFile();
        } catch (final URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}