详见 https://quarkus.io/guides/resteasy-reactive#via-annotations  

# 请求或响应过滤器

通过注释
您可以声明在请求处理的以下阶段调用的函数:

识别端点方法之前:路由前请求过滤器

在路由之后，但在端点方法被调用之前:普通请求过滤器

在端点方法被调用后:响应过滤器

这些过滤器允许您做各种事情，例如检查请求URI、HTTP方法、影响路由、查看或更改请求头、中止请求或修改响应。



请求筛选器可以用@ServerRequestFilter注释:
```java
import java.util.Optional;

class Filters {

    @ServerRequestFilter(preMatching = true)
    public void preMatchingFilter(ContainerRequestContext requestContext) {
        // make sure we don't lose cheese lovers
        if("yes".equals(requestContext.getHeaderString("Cheese"))) {
            requestContext.setRequestUri(URI.create("/cheese"));
        }
    }

    @ServerRequestFilter
    public Optional<RestResponse<Void>> getFilter(ContainerRequestContext ctx) {
        // only allow GET methods for now
        if(ctx.getMethod().equals(HttpMethod.GET)) {
            return Optional.of(RestResponse.status(Response.Status.METHOD_NOT_ALLOWED));
        }
        return Optional.empty();
    }
}
```
类似地，响应筛选器可以用@ServerResponseFilter注释:

```java
class Filters {
    @ServerResponseFilter
    public void getFilter(ContainerResponseContext responseContext) {
        Object entity = responseContext.getEntity();
        if(entity instanceof String) {
            // make it shout
            responseContext.setEntity(((String)entity).toUpperCase());
        }
    }
}
```

# 过滤器参数
您的过滤器可以声明以下任何参数类型:  
任何一个上下文对象

ContainerRequestContext 访问当前请求的上下文对象

ContainerResponseContext  用于访问当前响应的上下文对象


Throwable  任何抛出的异常，或者null(仅适用于response filters)
# 返回类型

RestResponse<?>或者Response  
要发送给客户端而不是继续筛选器链的响应，或者null如果过滤器链应该继续

Optional<RestResponse<?>>或者Optional<Response>  
发送到客户端而不是继续筛选器链的可选响应，如果筛选器链应该继续，则为空值

Uni<RestResponse<?>>或者Uni<Response>  
发送到客户端的异步响应，而不是继续筛选器链，或者null如果过滤器链应该继续

# JAX-RS
你也可以以JAX-RS方式声明请求和响应过滤器.

HTTP请求和响应都可以通过提供ContainerRequestFilter或者ContainerResponseFilter分别实现。这些过滤器适用于处理与消息相关联的元数据:HTTP头、查询参数、媒体类型和其他元数据。他们还能够中止请求处理，例如当用户没有访问端点的权限时。

让我们使用ContainerRequestFilter为我们的服务添加日志功能。我们可以通过实施ContainerRequestFilter并用@Provider注释:

```java
package org.acme.rest.json;

import io.vertx.core.http.HttpServerRequest;
import org.jboss.logging.Logger;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;

@Provider
public class LoggingFilter implements ContainerRequestFilter {

    private static final Logger LOG = Logger.getLogger(LoggingFilter.class);

    @Context
    UriInfo info;

    @Context
    HttpServerRequest request;

    @Override
    public void filter(ContainerRequestContext context) {

        final String method = context.getMethod();
        final String path = info.getPath();
        final String address = request.remoteAddress().toString();

        LOG.infof("Request %s %s from IP %s", method, path, address);
    }
}
```