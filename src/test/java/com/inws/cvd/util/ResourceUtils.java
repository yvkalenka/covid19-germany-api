package com.inws.cvd.util;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.util.Objects.requireNonNull;

public class ResourceUtils {

    public static String getResourceContents(String resourceName) {

        var resourceUrl = requireNonNull(
                ResourceUtils.class.getClassLoader().getResource(resourceName)
        );

        try {
            var path = Path.of(resourceUrl.toURI());
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException("Failed to read resource '" + resourceName + "'.", e);
        }
    }

}
