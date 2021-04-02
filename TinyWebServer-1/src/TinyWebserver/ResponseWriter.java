package TinyWebserver;

import java.io.*;
import java.util.Locale;


public class ResponseWriter extends PrintWriter {
    /*
    * count:本次返回的content-length
    * current:buffer中下一个可以写入的下标
    * bufferSize:初始的buffer size,
    * buffer:返回的数据存放的缓冲区，能够扩大.到最后才写入到PrintWriter的缓冲区当中.再flush到浏览器
    * 如果需要返回数据，具体的操作是:
    * 1. 先用writer()写到缓冲区中
    * 2. 然后封装好报文头,比如说自己封装，或者assembleResponse函数(该函数目前只能在200的时候使用
    * 也可以修改的更加通用一些)
    * 3. 然后调用realWrite讲数据从缓冲区中返回到浏览器
    * */
    int count = 0;
    int current = 0;
    int bufferSize = 2048;
    byte[] buffer = new byte[bufferSize];

    public ResponseWriter(Writer out) {
        super(out);
    }

    public ResponseWriter(Writer out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public ResponseWriter(OutputStream out) {
        super(out);
    }

    public ResponseWriter(OutputStream out, boolean autoFlush) {
        super(out, autoFlush);
    }

    public ResponseWriter(String fileName) throws FileNotFoundException {
        super(fileName);
    }

    public ResponseWriter(String fileName, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(fileName, csn);
    }

    public ResponseWriter(File file) throws FileNotFoundException {
        super(file);
    }

    public ResponseWriter(File file, String csn) throws FileNotFoundException, UnsupportedEncodingException {
        super(file, csn);
    }

    @Override
    public void flush() {
        /*
         * 用户调用flush并不会对Writer起任何作用
         * 让container来完成对流的flush
         * */
    }

    public void realFlush() {
        /*
        * 每次flush之后，count重新设置为0.因为count所表示的意思是:
        * 报文中的content-length
        * */
        count = 0;
        super.flush();
    }

    @Override
    public void close() {
        /*
        * 用户调用close并不会对Writer起任何作用
        * 让container来完成对流的关闭
        * */
    }

    public void realClose() {
        //关闭向client的输出流，该方法只有container可以调用
        //用户调用close()没有任何作用
        super.close();
    }

    @Override
    public boolean checkError() {
        return super.checkError();
    }

    @Override
    protected void setError() {
        super.setError();
    }

    @Override
    protected void clearError() {
        super.clearError();
    }

    @Override
    public void write(int c) {
        super.write(c);
    }

    @Override
    public void write(char[] buf, int off, int len) {
        super.write(buf, off, len);
    }

    @Override
    public void write(char[] buf) {
        super.write(buf);
    }

    @Override
    public void write(String s, int off, int len) {
        super.write(s, off, len);
    }

    @Override
    public void write(String s) {
        /*
        * 在操作字符流的时候，现将数据写到ResponseWriter的buffer中，因为我们在返回
        * 报文浏览器的时候，需要设置好content-length.如果使用PrintWriter来直接实现会稍微有些困难
        * */
        count += s.length();
        appendToBuffer(s);
    }

    public void realWrite(String requestHeaders) {
        /*
        * 将缓冲区中的数据写入到PrintWriter的缓冲区中，在flush的时候会被返回到
        * 浏览器中.此时buffer中可能还存在很多0(因为我们初始化buffer size = 2048)
        * 但是没有关系，因为使用count来记录一共写入到缓冲区中多少字节.
        * */
        super.write(requestHeaders);
        super.write(new String(buffer,0,count));
    }

    public void writeHeaders(String headers) {
        /*
        * 直接发送Response headers返回到浏览器，因为在发送图片等二进制文件的
        * 时候，使用字符流来操作不合适.所以使用的是字节流.用该方法来显示的写入response headers
        * 而不是想操作字符流那样写入到buffer中，在一起flush(就如同realWrite所做的那样)
        * */
        super.write(headers);
    }

    public void implicitFlush() {
        /*
        * 为了在发送二进制数据的时候，显示的来flush response headers到client
        * */
        super.flush();
    }

    public void appendToBuffer(String data) {
        /*
        *　用户调用这个访问来往缓冲区中写入数据.如果本次写入的数据大于current(当前可写入的数据的index)
        * + data.length(新写入数据的长度),那么表示需要扩充缓冲区，复制数据到新的缓冲区当中
        * 然后再将本次数据写入到新的缓冲区当中.
        * */
        if ((current + data.length()) > bufferSize) {
            byte[] old = buffer;
            buffer = new byte[bufferSize * 2];
            System.arraycopy(old,0,buffer,0,bufferSize);
        }
        byte[] bytes = data.getBytes();
        int i,j;
        for( i =0, j = current; i < bytes.length; i++,j++) {
            buffer[j] = bytes[i];
        }
        //更新下一个可用的数组下标.
        current += j;
    }

    @Override
    public void print(boolean b) {
        super.print(b);
    }

    @Override
    public void print(char c) {
        super.print(c);
    }

    @Override
    public void print(int i) {
        super.print(i);
    }

    @Override
    public void print(long l) {
        super.print(l);
    }

    @Override
    public void print(float f) {
        super.print(f);
    }

    @Override
    public void print(double d) {
        super.print(d);
    }

    @Override
    public void print(char[] s) {
        super.print(s);
    }

    @Override
    public void print(String s) {
        super.print(s);
    }

    @Override
    public void print(Object obj) {
        super.print(obj);
    }

    @Override
    public void println() {
        super.println();
    }

    @Override
    public void println(boolean x) {
        super.println(x);
    }

    @Override
    public void println(char x) {
        super.println(x);
    }

    @Override
    public void println(int x) {
        super.println(x);
    }

    @Override
    public void println(long x) {
        super.println(x);
    }

    @Override
    public void println(float x) {
        super.println(x);
    }

    @Override
    public void println(double x) {
        super.println(x);
    }

    @Override
    public void println(char[] x) {
        super.println(x);
    }

    @Override
    public void println(String x) {
        super.println(x);
    }

    @Override
    public void println(Object x) {
        super.println(x);
    }

    @Override
    public PrintWriter printf(String format, Object... args) {
        return super.printf(format, args);
    }

    @Override
    public PrintWriter printf(Locale l, String format, Object... args) {
        return super.printf(l, format, args);
    }

    @Override
    public PrintWriter format(String format, Object... args) {
        return super.format(format, args);
    }

    @Override
    public PrintWriter format(Locale l, String format, Object... args) {
        return super.format(l, format, args);
    }

    @Override
    public PrintWriter append(CharSequence csq) {
        return super.append(csq);
    }

    @Override
    public PrintWriter append(CharSequence csq, int start, int end) {
        return super.append(csq, start, end);
    }

    @Override
    public PrintWriter append(char c) {
        return super.append(c);
    }
}
