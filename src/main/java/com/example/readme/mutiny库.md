详见 
https://quarkus.io/guides/mutiny-primer  
https://smallrye.io/smallrye-mutiny/1.7.0/guides/imperative-to-reactive/

返回单个元素 使用 Uni 返回多个元素使用Multi  
需要调用subscribe()方法来使得操作被真正调用  
在 Quarkus 中，一些扩展为您处理订阅。例如，在 RESTEasy Reactive 中，  
您的 HTTP 方法可以返回 Uni 或 Multi，而 RESTEasy Reactive 会帮你执行subscribe()


# #故障处理
故障处理
到目前为止，我们只处理项目事件，但处理失败是必不可少的。您可以使用onFailure().

例如，您可以使用后备项目进行恢复onFailure().recoverWithItem(fallback)：


Uni<String> uni1 = …
uni1
.onFailure().recoverWithItem(“my fallback value”);
您还可以重试该操作，例如：


Uni<String> uni1 = …
uni1
.onFailure().retry().atMost(5);