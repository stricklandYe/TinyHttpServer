package TinyWebserver;

import TinyWebserver.servlet.HttpServletRequest;
import java.util.HashMap;
public class HttpRequest implements HttpServletRequest  {
    protected enum REQUEST_TYPE {
        GET,
        GET_STATIC,
        POST,
        POST_FILE,
    }

    private String uri;
    private String method;
    private HashMap<String,String> parametersMap;
    private HashMap<String,String> headersMap;
    private String fileSuffix;
    private REQUEST_TYPE type;
    private String protocol;
    private MultipartData data;


    public String getFileSuffix() {
        return fileSuffix;
    }

    public void setFileSuffix(String fileSuffix) {
        this.fileSuffix = fileSuffix;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public MultipartData getData() {
        return data;
    }

    public void setData(MultipartData data) {
        this.data = data;
    }

    public void setHeader(String key, String value) {
        headersMap.put(key,value);
    }

    public String getHeader(String key) {
        return headersMap.get(key);
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public HashMap<String, String> getParametersMap() {
        return parametersMap;
    }

    public void setParametersMap(HashMap<String, String> parametersMap) {
        this.parametersMap = parametersMap;
    }

    public HashMap<String, String> getHeadersMap() {
        return headersMap;
    }

    public void setHeadersMap(HashMap<String, String> headersMap) {
        this.headersMap = headersMap;
    }

    public REQUEST_TYPE getType() {
        return type;
    }

    public void setType(REQUEST_TYPE type) {
        this.type = type;
    }

    @Override
    public String getProtocol() {
        return this.protocol;
    }

    @Override
    public String getPath() {
        return this.uri;
    }

    @Override
    public String getParameter(String name) {
        return this.parametersMap.get(name);
    }

    @Override
    public String[] getParameters(String name) {
        //todo:完成复选框，暂时还没做
        return new String[0];
    }
}
