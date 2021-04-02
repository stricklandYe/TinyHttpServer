package TinyWebserver;

/*
*　这个类是用于存放multipart表单中的文件数据.
*  name:是input中的name
*  value:是直接上传文件的文件名
*  tempFile:是保存的临时文件的文件名
* */
public class MultipartData {
    String name;
    String value;
    String tempFile;

    public MultipartData(String name, String value, String tempFile) {
        this.name = name;
        this.value = value;
        this.tempFile = tempFile;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public String getTempFile() {
        return tempFile;
    }
}
