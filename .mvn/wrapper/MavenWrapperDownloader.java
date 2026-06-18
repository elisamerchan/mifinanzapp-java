import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class MavenWrapperDownloader {
    private static final String WRAPPER_VERSION = "3.2.0";
    private static final String DEFAULT_DOWNLOAD_URL = "https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/"
            + WRAPPER_VERSION + "/maven-wrapper-" + WRAPPER_VERSION + ".jar";

    public static void main(String[] args) {
        try {
            Path target = Paths.get(".mvn/wrapper/maven-wrapper.jar");
            if (!Files.exists(target.getParent())) {
                Files.createDirectories(target.getParent());
            }
            try (InputStream in = new URL(DEFAULT_DOWNLOAD_URL).openStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }
            System.out.println("Maven wrapper downloaded successfully");
        } catch (IOException e) {
            System.err.println("Error downloading Maven wrapper: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
