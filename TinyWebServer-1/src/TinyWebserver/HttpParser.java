package TinyWebserver;


import TinyWebserver.servlet.HttpServletResponse;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class HttpParser {

    private Socket socket;
    private InputStream input;
    private PrintWriter output;
    private HttpRequest request;
    private HttpResponse response;

    public static int BUFFER_SIZE = 1024*8;

    public HttpParser(Socket socket,HttpRequest request,HttpResponse response) {
        this.socket = socket;
        this.request = request;
        this.response = response;
        try {
            this.input = socket.getInputStream();
            this.output = new PrintWriter(socket.getOutputStream(),true);
        } catch (IOException e) {
            response.sendError(HttpServletResponse.HTTP_SERVER_ERROR,"Server Error",e.getMessage());
        }
    }

    public void parse() {
        byte[] buf = new byte[BUFFER_SIZE];
        int count = 0;
        try {
            count = input.read(buf);
            if (count < 0) {
                //在一段时间后，应该是浏览器关闭了tcp链接，所以read会返回-1
                //此时没有数据可以读取了，应该直接退出这个线程.
                System.out.println("stream end has reached");
                return;
            }
        } catch (IOException e) {
            response.sendError(HttpServletResponse.HTTP_SERVER_ERROR,"Server Error",e.getMessage());
        }
        ByteArrayInputStream inputByteStream = new ByteArrayInputStream(buf);
        parseHeadersAndLine(inputByteStream);
        if (request.getMethod().equals("GET")) {
            if (request.getUri().contains("?")) {
                //带有参数的GET请求
                HashMap<String,String> params = parseParameters(request.getUri());
                request.setParametersMap(params);
                request.setType(HttpRequest.REQUEST_TYPE.GET);
            } else {
                //不带有参数的GET请求,那么就是请求静态页面
                request.setFileSuffix(getFileSuffix(request.getUri()));
                request.setType(HttpRequest.REQUEST_TYPE.GET_STATIC);
            }
        } else {
            //处理POST请求
            String type = request.getHeader("Content-Type");
            if (type.equals("application/x-www-form-urlencoded")) {
                //普通表单数据
                byte[] postData = getPostRequestBody(count,buf);
                HashMap<String,String> params = parseParameters(new String(postData));
                request.setParametersMap(params);
                request.setType(HttpRequest.REQUEST_TYPE.POST);
            } else {
                //带有文件的表单数据
                /*
                * Content-Type: multipart/form-data; boundary=----WebKitFormBoundary0vlW6VUHG6p85uWE
                * 如果post请求带有文件,此时对应的content-type的格式如上所示.这里没有判断content-type是否为multipart,
                * 是因为里面还包含着boundary,所以直接用else来处理.
                * */
                byte[] postData = getPostRequestBody(count,buf);
                System.out.println(new String(postData));
                String boundary =  type.substring(type.indexOf("=")+1);
                String startBoundary = "--"+boundary;
                int[] indexes = getBoundaryIndexes(postData,startBoundary.getBytes());
                HashMap<String,String> params = parseMultiPostRequest(postData,startBoundary,indexes);
                request.setParametersMap(params);
                request.setType(HttpRequest.REQUEST_TYPE.POST_FILE);
            }
        }
    }

    public int[] getBoundaryIndexes(byte[] postData,byte[] boundary) {
        /*
        * 找到每个boundary字符串在报文postData中的下标.postData[i] == boundary[matchCount]
        * 比较当前byte和boundary的byte是否相等.matchCount记录相等的字节个数
        * machByte记录但是相等的第一个字节的下标.matchCount == boundary.length
        * 说明找到了一个boundary.
        * */
        ArrayList<Integer> indexes = new ArrayList<>();
        int matchCount = 0,matchByte = -1;
        for (int i = 0; i < postData.length; i++) {
            if (postData[i] == boundary[matchCount]) {
                if (matchCount == 0) {
                    matchByte = i;
                }
                matchCount++;
                if (matchCount == boundary.length) {
                    indexes.add(matchByte);
                    matchCount = 0;
                    matchByte = -1;
                }
            } else {
                i -= matchCount;
                matchCount = 0;
                matchByte = -1;
            }
        }
        int[] result = new int[indexes.size()];
        for (int i = 0 ; i < indexes.size(); i++)
            result[i] = indexes.get(i);
        return result;
    }


    public int getPostEntryDataOffset(byte[] postData,int offset) {
        /*
        *　获得post block中数据部分的的offset,post block中
        *  数据和name之间的界限是\r\n\r\n.
        *  multipart报文中每一行数据的界限都是\r\n\r\n
        * */
        int dataOffset = 0;
        for (int i = offset; i < postData.length; i++) {
            if (postData[i] == '\r' && postData[++i] == '\n'
                    && postData[++i] == '\r'&& postData[++i] == '\n'){
                dataOffset = i;
                break;
            }
        }
        return dataOffset + 1;
    }

    public String getFileSuffix(String uri) {
        /*
        * 获得uri中文件的后缀,以此来选择对应的mimetype
        * */
        int index = uri.indexOf(".");
        if (index < 0)
            return null;
        else
            return uri.substring(index);
    }

    public HashMap<String,String> parseParameters(String uri) {
        /*
        * 获得get请求中的参数，get请求的参数都在uri中.所以只需要解析URI即可获得参数
        * */
        HashMap<String, String> paraMap = new HashMap<>();
        String paramStr = uri.substring(uri.indexOf("?")+1);
        String[] paramsArr = paramStr.split("&");
        for (String param:paramsArr) {
            String paramName = param.substring(0,param.indexOf("="));
            /*
            * 因为在第一次读取到时候，我们从inputStream读取了BUFFER_SIZE个字节报文数据
            * 对于较短的表单数据，可能会出现 key1=value1&key2=value2...，value2会出现一大堆的空格
            * 会导致一大堆没用的数据出现在value当中，所以使用trim来去除这些无效数据.
            * */
            String paramValue = param.substring(param.indexOf("=")+1).trim();
            paraMap.put(paramName,paramValue);
        }
        return paraMap;
    }

    public HashMap<String, String> parseMultiPostRequest(byte[] data,String startBoundary,int[] indexes) {
        /*
        * 该函数的作用是解析带有文件的POST请求.思路是:
        * 对于文本数据，HTTP的格式比较固定，具体可以看MDN中文档的说明。
        * 对于multipart/form-data格式的post报文.浏览器提交的数据都以block的形式发送到服务器
        * 每个block之间都是以boundary来分割。所以只要根据boundary来对每一个block处理即可
        * 而对于文件数据有些不同。因为文件中可能也有\r\n。所以readline不能用于文件处理
        * indexes是每一个boundary在报文data中的索引。　getPostEntryDataOffset()获得二进制文件
        * 在报文中的起始地址。
        * */
        HashMap<String, String> paraMap = new HashMap<>();
        InputStreamReader input = new InputStreamReader(new ByteArrayInputStream(data));
        BufferedReader  reader = new BufferedReader(input);
        String line;
        try {
            int boundaryIndex = -1;
            while((line = reader.readLine())!= null) {
                if (line.equals(startBoundary)) {
                    boundaryIndex ++;
                    String position = reader.readLine();
                    String name = position.substring(position.indexOf("=")+1)
                            .replaceAll("^\"|\"","");
                    String type = getPostContentType(reader.readLine());
                    if (type.equals("")) {
                        String value = reader.readLine();
                        paraMap.put(name,value);
                    } else {
                        int dataOffset = getPostEntryDataOffset(data,indexes[boundaryIndex]);
                        String inputName = name.substring(0,name.indexOf(";")); //input中的name
                        String value = name.substring(name.indexOf("=")+1); //上传的文件对应的文件名
                        //不知道为什么还需要减去两个字节，不减就会有问题
                        String path = saveTempFile(data,dataOffset,indexes[boundaryIndex+1]-dataOffset-2);
//                        System.out.println(path);
                        MultipartData multipartData = new MultipartData(inputName,value,path);
                        request.setData(multipartData);
                    }
                }

            }
        } catch (IOException e) {
            response.sendError(HttpServletResponse.HTTP_SERVER_ERROR,"Server Error",e.getMessage());
        }
        return paraMap;
    }


    public String saveTempFile(byte[] postData,int start,int len) {
        /*
        *  postData:HTTP报文数据
        *  start: 二进制文件数据在报文中的偏移
        *  len: 二进制数据文件在报文中的长度,计算方式十分简单，只要二进制文件的下一个boundary的地址
        *  减去文件数据在报文中的偏移地址
        *
        * 目前选择的是将文件先存到本地，在用户需要的时候在使用FileInputStream来从硬盘读取数据.虽然这样做性能较低
        * 但是如果直接将所有的文件数据都放在内存中。会导致内存塞满等情况。　我不知道Tomcat如何来实现这一点，这里只是
        * 我的做法，仅供参考
        * */
        String tmpdir = System.getProperty("java.io.tmpdir");
        String result = null;
        try {
            File temp = File.createTempFile("TinyWebServer","",new File(tmpdir));
            FileOutputStream out = new FileOutputStream(temp);
            out.write(postData,start,len);
            out.close();
            result = temp.getAbsolutePath();
        } catch (IOException e) {
            response.sendError(HttpServletResponse.HTTP_SERVER_ERROR,"Server Error",e.getMessage());
        }
        return result;
    }


    public String getPostContentType(String type) {
        //type可能为content-type或者是空字符串
        if (type.equals("")) {
            return type;
        } else {
            int index = type.indexOf(":");
            return type.substring(index+1).trim();
        }
    }


    public byte[] getPostRequestBody(int count,byte[] buf) {
        //获取没有附有文件的post请求的参数
        //count:http报文的长度
        //buf:首次读取的http报文
        int length = Integer.parseInt(request.getHeader("Content-Length"));
        ByteArrayOutputStream outByteStream = new ByteArrayOutputStream();
        byte[] bodyBuf = new byte[BUFFER_SIZE];
        int bodyIndex = 0;
        while (bodyIndex < count) {
            //request body和request headers之间的界限是\r\n\r\n
            if (buf[bodyIndex] == '\r' && buf[++bodyIndex] == '\n'
                    && buf[++bodyIndex] == '\r'&& buf[++bodyIndex] == '\n') {
                break;
            }
            bodyIndex++;
        }
        bodyIndex++;
        if ((bodyIndex+length) <= count ) {
            /*
            *  bodyIndex是报文中request body的地址.　bodyIndex+length　<= count
            *  说明第一次读取数据已经包含了request body.所以直接讲request body 复制到bodyBuf中即可
            *　复制的长度就是报文中给出的content-length长度.
            * */
            System.arraycopy(buf,bodyIndex,bodyBuf,0,length);
            try {
                outByteStream.write(bodyBuf);
            } catch (IOException e) {
                response.sendError(HttpServletResponse.HTTP_SERVER_ERROR,"Server Error",e.getMessage());
            }
        }
        if ((bodyIndex+length) > count) {
            /*
            *  如果超过了buffer size,那么说明第一次读取的数据超过了缓冲区大小。所以需要继续读取剩下的request body
            *  在这里，首先需要将在第一次读取中的报文数据提取出来，写入到outByteStream当中。
            * */
            int len = count-bodyIndex; //在第一次读取的中所含的表单数据的长度
            System.arraycopy(buf,bodyIndex,bodyBuf,0,len);
            try {
                outByteStream.write(bodyBuf,0,len);
                length -= len; //把第一次读取的数据从content-length中去除
                while (length > 0) {
                    count = input.read(bodyBuf,0,BUFFER_SIZE);
                    length -= count;
                    if (count > 0) {
                        /*
                        * input.read读取的数据往往不是BUFFER_SIZE个大小，所以
                        * 在写入
                        * */
                        outByteStream.write(bodyBuf,0,count);
                    }
                }
            } catch (IOException e){
                response.sendError(HttpServletResponse.HTTP_SERVER_ERROR,"Server Error",e.getMessage());
            }
        }
        return outByteStream.toByteArray();
    }

    public void parseHeadersAndLine(ByteArrayInputStream stream) {
        //解析HTTP headers和http request line,放到map中
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        HashMap<String, String> reqHeaderMap = new HashMap<>();
        try {
            String requestLine = reader.readLine();
            System.out.println(requestLine);
            parseRequestLine(requestLine);
            String line;
            while (!(line = reader.readLine()).equals("")) {
//                System.out.println(line);
                int index = line.indexOf(":");
                String name = line.substring(0, index);
                String value = line.substring(index + 2); //去除空格
                reqHeaderMap.put(name,value);
            }
        } catch (IOException e) {
            response.sendError(HttpServletResponse.HTTP_SERVER_ERROR,"Server Error",e.getMessage());
        }
        request.setHeadersMap(reqHeaderMap);
    }

    public void parseRequestLine(String requestLine) {
        //获得method和uri,method:get还是post
        int index1,index2;

        //判断是Method的类型
        index1 = requestLine.indexOf(" ");
        request.setMethod(requestLine.substring(0,index1));
        //获取URI
        index2 = requestLine.indexOf(' ', index1 + 1);
        String URI = requestLine.substring(index1 + 1, index2);
        request.setUri(URI);
        String httpVersion = requestLine.substring(index2).trim();
        request.setProtocol(httpVersion);
    }

}
