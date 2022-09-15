详见 https://quarkus.io/guides/rest-json

# @RegisterForReflection
在构建本地镜像时 必须给需要序列化为json的类上添加这个注解，否则因为无法反射序列化出来是空的。


## jackson依赖
```xml
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-resteasy-reactive-jackson</artifactId>
        </dependency>
```
