
import java.io.File;  // Import for File
import java.io.FileInputStream;  // Import for FileInputStream
import java.io.FileOutputStream;  // Import for FileOutputStream
import java.io.IOException;  // Import for IOException
import java.nio.ByteBuffer;  // Import for ByteBuffer

public class NAFileUtils {

    public static void writeNAFile(String filename, int width, int height, short colorDepth, byte[] pixelData) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(ByteBuffer.allocate(4).putInt(width).array());
            fos.write(ByteBuffer.allocate(4).putInt(height).array());
            fos.write(ByteBuffer.allocate(2).putShort(colorDepth).array());
            fos.write(pixelData);
        }
    }

    public static int readWidth(File naFile) {
        try (FileInputStream fis = new FileInputStream(naFile)) {
            byte[] widthBytes = new byte[4];
            fis.read(widthBytes);
            return ByteBuffer.wrap(widthBytes).getInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static int readHeight(File naFile) {
        try (FileInputStream fis = new FileInputStream(naFile)) {
            byte[] heightBytes = new byte[4];
            fis.skip(4);
            fis.read(heightBytes);
            return ByteBuffer.wrap(heightBytes).getInt();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static byte[] readPixelData(String naFilePath, int size) {
        try (FileInputStream fis = new FileInputStream(naFilePath)) {
            fis.skip(10); // Skip width, height, and color depth
            byte[] pixelData = new byte[size];
            fis.read(pixelData);
            return pixelData;
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}