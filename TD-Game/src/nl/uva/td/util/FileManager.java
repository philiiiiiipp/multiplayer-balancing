package nl.uva.td.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * A helper class for file specific operations.
 */
public class FileManager {

    /** The folder containing all the assets */
    public final static String ASSETS_FOLDER = "assets/";

    /** The folder containing the sample documents */
    public final static String COLLECTION_FOLDER = ASSETS_FOLDER + "collection/";

    /**
     * Retrieves the contents from the specified file.
     *
     * @param file
     *            The file to get the contents for
     * @param encoding
     *            The encoding used for the file
     *
     * @return The file's contents
     *
     * @throws IOException
     *             Thrown when the contents of the file cannot be read
     */
    public static String getFileContents(final File file, final Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(file.toPath());
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }

    /**
     * Retrieves the contents from the specified file. Will use the default charset for the
     * encoding.
     *
     * @param file
     *            The file to get the contents for
     *
     * @return The file's contents
     *
     * @throws IOException
     *             Thrown when the contents of the file cannot be read
     */
    public static String getFileContents(final File file) throws IOException {
        return getFileContents(file, Charset.defaultCharset());
    }
}