# TinyHttpServer
一个简单的HTTP服务器，仅供学习，不**适合用于任何正式**的环境。目前而言，只能处理最简单的HTTP报文数据。

在接口的设计上，模仿了一些HttpServlert的接口接口定义。使用＠WebServlet注解来实现对URL的映射。使用反射来动态地加载Serlvet。tmp文件夹保存的是用户上传的文件,static保存的是静态文件，比如html文件。使用线程池来一次可以处理多个http请求。

## 支持的请求
- GET
- POST

其他的HTTP请求没有实现，只支持最简单的功能。上传文件，显示文件，处理GET请求中的参数，以及POST表单中的文件数据。

## Bug
这个bug我不知道如何解决，**虽然代码可以正常运行**。在TinyHttpServer运行一段时间之后，会出现`NullPointerExcedption`。

## 可以拓展的地方
我使用的是传统的IO来实现的，不够高效。可以在此基础上引入NIO的实现。相对于传统的阻塞IO（blocking IO）来说,NIO更好。
## 如何使用?
将TinyHttpServer内的TinyWebServer直接用IDEA打开就行。
