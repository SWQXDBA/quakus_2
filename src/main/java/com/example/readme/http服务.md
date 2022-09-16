详见 https://quarkus.io/guides/resteasy-reactive

# 媒体类型 @Consumes @produces

consumes： 指定处理请求的提交内容类型（Content-Type），例如application/json, text/html;



produces: 指定返回的内容类型，仅当request请求头中的(Accept)类型中包含该指定类型才返回；

 声明端点:表示/内容类型
每个端点方法都可以使用或产生特定的资源表示，这些资源表示由HTTPContent-Type头，它又包含MIME(媒体类型)值，如下所示:

text/plain这是任何返回String.

text/html对于HTML(比如用Qute模板)

application/json对于一个JSON REST端点

text/*它是任何文本媒体类型的子类型通配符

*/*它是任何媒体类型的通配符

您可以用@Produces或者@Consumes批注，它允许您指定一个或多个媒体类型，您的端点可以将这些媒体类型作为HTTP请求体接受或作为HTTP响应体产生。这些类注释适用于每个方法。

任何方法也可以用@Produces或者@Consumes注释，在这种情况下，它们会覆盖任何最终的类注释。

这MediaType类有许多常量，可以用来指向特定的预定义媒体类型。

# 设置路径统一前缀
```java
@ApplicationPath("/api")
public static class MyApplication extends Application {

}
```
这将导致所有rest端点相对于/api，所以上面的端点用@Path("rest")可在以下位置访问/api/rest/。您还可以设置quarkus.resteasy-reactive.path如果不想使用批注，可以使用构建时属性来设置根路径。

#  声明其他http方法
```java

@Retention(RetentionPolicy.RUNTIME)
@HttpMethod("CHEESE")
@interface CHEESE {
}

@Path("")
public class Endpoint {

    @CHEESE
    public String hello() {
        return "Hello, Cheese World!";
    }
}
```

# 获取请求的参数


@RestPath

@RestQuery

@RestHeader

@RestCookie

@RestForm

@RestMatrix

对于这些注释中的每一个，您可以指定它们所引用的元素的名称，否则它们将使用带注释的方法参数的名称。  

你也可以使用任何JAX-RS注解@PathParam, @QueryParam, @HeaderParam, @CookieParam, @FormParam或者@MatrixParam但是它们要求您指定参数名。
比如
```

    @GET
    @Path("/3/{name}")
    public Uni<Fruit> get3(@PathParam("name") String name){
        return Uni.createFrom().item(fruits.stream().filter(f -> f.name.equals(name)).findFirst().get());
    }
    
```
其中 @PathParam("name") String name 不等同于@PathParamString name  

但是 @RestPath("name") String name 等同于@RestPath String name


# 解析body中的内容
任何不带注释的方法参数都将接收方法体。在它从其HTTP表示映射到参数的Java类型之后。


类型	使用
File=>临时文件中的整个请求体

byte[]=>整个请求正文，未解码

char[]=>解码后的整个请求体

String=>解码后的整个请求体

InputStream=>阻塞流中的请求体

Reader=>阻塞流中的请求体

所有Java原语及其包装类=>Java原始类型

BigDecimal, BigInteger=> 大整数和小数。

JsonArray, JsonObject, JsonStructure, JsonValue =>JSON值类型

Buffer =>垂直x缓冲区

任何其他类型  =>将会从JSON映射到该类型

# 处理多部分表单数据
处理具有以下属性的HTTP请求multipart/form-data作为内容类型，  
RESTEasy Reactive引入了@MultipartForm注释。让我们看一个使用它的例子。

假设需要处理一个包含文件上传和包含字符串描述的表单值的HTTP请求，我们可以编写一个POJO来保存这些信息，如下所示:  

```java
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;

public class FormData {

    @RestForm
    @PartType(MediaType.TEXT_PLAIN)
    public String description;

    @RestForm("image")
    public FileUpload file;
}
```

这name字段将包含被调用的HTTP请求部分中包含的数据description(因为@RestForm不定义值，使用字段名称)，而file字段将包含有关上传文件的数据imageHTTP请求的一部分。

多部分请求中每个部分的大小必须符合quarkus.http.limits.max-form-attribute-size，默认值为2048字节。任何部分大小超过此配置的请求都将导致HTTP状态代码413。

FileUpload提供对上传文件的各种元数据的访问。  
但是，如果您需要的只是上传文件的句柄，java.nio.file.Path或者java.io.File可以使用。 

当需要在不指定表单名称的情况下访问**所有上传的文件**时，RESTEasy Reactive允许使用
`@RestForm List<FileUpload>` 同时不给@RestForm注解设置value  
@PartType用于帮助将请求的相应部分反序列化为所需的Java类型。例如，当相应的body部分是JSON并且需要转换成POJO时，这非常有用。  

```java

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.MultipartForm;

@Path("multipart")
public class Endpoint {

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("form")
    public String form(@MultipartForm FormData formData) {
        // return something
    }
}
```
**使用@MultipartFormas方法参数使RESTEasy Reactive将请求作为多部分表单请求来处理。**
但是在上面的例子中 使用@MultipartForm实际上是不必要的，因为RESTEasy Reactive可以从  
@Consumes(MediaType.MULTIPART_FORM_DATA) 中得知它是多表单参数

### 临时文件存储::

在处理文件上传时，在处理POJO的代码中，将文件移动到永久存储区(如数据库、专用文件系统或云存储)是非常重要的。 
否则，当请求终止时，该文件将不再可访问。 
而且，如果quarkus.http.body.delete-uploaded-files-on-end设置为true，Quarkus将在发送HTTP响应时删除上传的文件。  
如果禁用该设置，该文件将驻留在服务器的文件系统中(在由quarkus.http.body.uploads-directory配置选项)，  
但是由于上传的文件以UUID文件名保存，并且没有保存额外的元数据，所以这些文件本质上是文件的随机转储。  

# 下载文件
类似地，RESTEasy Reactive可以生成多部分表单数据，以允许用户从服务器下载文件。  
例如，我们可以编写一个POJO来保存我们想要公开的信息:
```java

import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

public class DownloadFormData {

    @RestForm
    String name;

    @RestForm
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    File file;
}
```  
  


```java

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("multipart")
public class Endpoint {

    @GET
    @Produces(MediaType.MULTIPART_FORM_DATA)
    @Path("file")
    public DownloadFormData getFile() {
        // return something
    }
}
```
**_返回多部分数据仅限于阻塞端点。_**

###处理格式错误的输入 
作为读取多部分主体的一部分，RESTEasy Reactive调用适当的MessageBodyReaderMessageBodyReader请求的每个部分。如果一个IOException对于这些部分中的一个(例如，如果Jackson无法反序列化JSON部分)，则org.jboss.resteasy.reactive.server.multipart.MultipartPartReadingException被抛出。如果应用程序没有处理此异常，如异常映射默认情况下，将返回HTTP 400响应。  

# 返回响应正文 

为了返回HTTP响应，只需从方法中返回所需的资源。方法返回类型及其可选内容类型将用于决定如何将其序列化为HTTP响应(参见Negotiation更多高级信息)。

您可以返回任何预定义的类型，您可以从HTTP响应，任何其他类型都将被映射从类型到JSON.

此外，还支持以下返回类型:


Path 由给定路径指定的文件内容



PathPart 由给定路径指定的文件的部分内容



FilePart 文件的部分内容



AsyncFile  :(Vert.x AsyncFile)，可以是完整的，也可以是部分的



或者，您也可以返回一个反应型  
诸如Uni, Multi或者CompletionStage解析为上述返回类型之一的。

#设置其他响应属性
比如需要设置header cookie等 
通过`org.jboss.resteasy.reactive.RestResponse`
```java
package org.acme.rest;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;

import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.RestResponse.ResponseBuilder;

@Path("")
public class Endpoint {

    @GET
    public RestResponse<String> hello() {
        // HTTP OK status with text/plain content type
        return ResponseBuilder.ok("Hello, World!", MediaType.TEXT_PLAIN_TYPE)
         // set a response header
         .header("X-Cheese", "Camembert")
         // set the Expires response header to two days from now
         .expires(Date.from(Instant.now().plus(Duration.ofDays(2))))
         // send a new cookie
         .cookie(new NewCookie("Flavour", "chocolate"))
         // end of builder API
         .build();
    }
}
```
也可以使用JAX-RS型Response但是它不是针对您的实体的强类型。//丢失泛型信息

###使用注释设置其他响应属性
```java

package org.acme.rest;

import org.jboss.resteasy.reactive.Header;
import org.jboss.resteasy.reactive.ResponseHeaders;
import org.jboss.resteasy.reactive.ResponseStatus;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("")
public class Endpoint {

    @ResponseStatus(201)
    @ResponseHeader(name = "X-Cheese", value = "Camembert")
    @GET
    public String hello() {
        return "Hello, World!";
    }
}
```
# 异步/反应式支持
返回Uni后者Multi即可 

响应过滤器包括不在流响应上调用，因为它们会给人一种您可以设置头或HTTP状态代码的错误印象，  
而在初始响应之后，情况并非如此。异常映射器也不会被调用，因为响应的一部分可能已经被写入。

#服务器发送事件(SSE)支持
如果想在响应中流式传输JSON对象，可以使用服务器发送的事件只需用注释端点方法@Produces(MediaType.SERVER_SENT_EVENTS)并指定每个元素都应该序列化为JSON随着@RestStreamElementType(MediaType.APPLICATION_JSON).

```java
package org.acme.rest;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.reactive.RestStreamElementType;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import io.smallrye.reactive.messaging.annotations.Channel;

@Path("escoffier")
public class Endpoint {

    // Inject our Book channel
    @Inject
    @Channel("book-out")
    Multi<Book> books;

    @GET
    // Send the stream over SSE
    @Produces(MediaType.SERVER_SENT_EVENTS)
    // Each element will be sent as JSON
    @RestStreamElementType(MediaType.APPLICATION_JSON)
    public Multi<Book> stream() {
        return books;
    }
}
```

# 控制HTTP缓存功能
RESTEasy Reactive提供了@Cache和@NoCache便于处理HTTP缓存语义的注释，即设置“Cache-Control”HTTP头。

这些注释可以放在资源方法或资源类上(在这种情况下，它适用于该类的所有资源方法不包含相同的注释)并允许用户返回域对象，而不必处理构建Cache-ControlHTTP头显式。

正在…@Cache构建一个复杂的Cache-Control标题，@NoCache是一种简化的表示法，表示您不想缓存任何东西；即Cache-Control: nocache.


### 运行的线程:  
请求过滤器通常与处理请求的方法在同一个线程上执行。这意味着如果服务于请求的方法用@Blocking，那么过滤器也将在工作线程上运行。如果该方法用@NonBlocking(或者根本没有注释)，那么过滤器也将在相同的事件循环线程上运行。

然而，如果过滤器需要在事件循环上运行，尽管服务于请求的方法将在工作线程上运行，那么@ServerRequestFilter(nonBlocking=true)可以使用。但是请注意，这些过滤器需要在任何的不使用该设置并在工作线程上运行的筛选器。