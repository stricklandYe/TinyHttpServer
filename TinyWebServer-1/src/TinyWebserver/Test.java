package TinyWebserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Test {
    public static void main(String[] args) throws ClassNotFoundException {
        try {
            FileInputStream input = new FileInputStream("foo.txt");
            FileChannel channel = input.getChannel();
            ArrayList foo = new ArrayList<>();
            ByteBuffer buffer = ByteBuffer.allocate(8024);
            channel.read(buffer);
            buffer.asReadOnlyBuffer();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
