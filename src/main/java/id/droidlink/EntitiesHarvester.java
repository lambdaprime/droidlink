package id.droidlink;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.persistence.jpa.PersistenceProvider;

public class EntitiesHarvester {

    private static final Logger LOGGER = Logger.getLogger(EntitiesHarvester.class.getSimpleName());

    private static void deleteDir(Path output) throws IOException {
        if (!output.toFile().exists())
            return;
        Files.walkFileTree(output, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
         });
    }

    private static int mainInternal(String[] args) throws IOException {
        if (args.length != 2) {
            LOGGER.warning(String.format("Usage: %s PERSISTENCE_UNIT_NAME OUTPUT_DIR", EntitiesHarvester.class.getSimpleName()));
            return -1;
        }

        String xml = "persistence.xml";
        if (EntitiesHarvester.class.getClassLoader().getResource(xml) == null) {
            LOGGER.severe(String.format("File %s is not found in the classpath", xml));
            return -1;
        }

        final Path output = Paths.get(args[1]);
        deleteDir(output);
        if (!output.toFile().mkdirs()) {
            LOGGER.severe(String.format("Failed to create output dir %s", output));
            return -1;
        }

        Map<String, Object> properties = new HashMap<>();
        final int[] c = new int[1];
        final int[] ret = new int[1];
        properties.put("eclipselink.classloader", new ClassLoader() {
            @Override
            public InputStream getResourceAsStream(String resName) {
                try {
                    c[0]++;
                    LOGGER.info(String.format("Loading %s", resName));
                    Path resFile = Paths.get(resName);
                    Path parent = output.resolve(resFile.getParent());
                    parent.toFile().mkdirs();
                    InputStream is = getClass().getClassLoader().getResourceAsStream(resName);
                    Files.copy(is, output.resolve(resName));
                    return getClass().getClassLoader().getResourceAsStream(resName);
                } catch (Exception e) {
                    LOGGER.severe(String.format("Failed to copy entity '%s'", e.getMessage()));
                    ret[0] = -1;
                    throw new RuntimeException(e);
                }
            }
        });

        properties.put("eclipselink.persistencexml", "!/persistence.xml");
        Logger.getLogger("org").setLevel(Level.ALL);
        LOGGER.info(String.format("Harvesting entities from '%s' persistence unit", args[0]));
        new PersistenceProvider().createEntityManagerFactory(args[0], properties);
        if (c[0] == 0) {
            LOGGER.warning(String.format("No entity classes were found. Please make sure that %s is correct", xml));
            return -1;
        }

        return 0;
    }
    
    public static void main(String[] args) throws IOException {
        System.exit(mainInternal(args));
    }
}
