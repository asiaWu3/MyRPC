# MyRPC
## RPC是什么？原理是什么？
RPC（Remote Procedure Call），远程过程调用，即可以像调用本地方法一样调用远程方法。
RPC可以看作由五部分组成：
● 客户端（服务消费端）：调用方
● 客户端Stub（桩）：本质上就是一个代理类，把本地调用的方法、类、参数等信息传到服务端
● 网络传输：把调用信息（类名、方法名、参数等）通过网络传输到服务端。实现方式有Socket、Netty等等
● 服务端Stub（桩）：这个不是代理类。接收请求并实际调用方法，封装结果并返回
● 服务端（服务提供端）：被调用方
一次RPC过程如下：
1. 服务消费端（client）以本地调用的方式调用远程服务； 
2. 客户端 Stub（client stub） 接收到调用后负责将方法、参数等组装成能够进行网络传输的消息体（序列 化）： RpcRequest ；
3. 客户端 Stub（client stub） 找到远程服务的地址，并将消息发送到服务提供端；
4. 服务端 Stub（桩）收到消息将消息反序列化为 Java 对象: RpcRequest ； 
5. 服务端 Stub（桩）根据 RpcRequest 中的类、方法、方法参数等信息调用本地的方法； 
6. 服务端 Stub（桩）得到方法执行结果并将组装成能够进行网络传输的消息体： RpcResponse （序列化）发 送至消费方；
7. 客户端 Stub（client stub）接收到消息并将消息反序列化为 Java 对象: RpcResponse ，这样也就得到了最 终结果。
## 一个基本的RPC框架应该有什么？
一个最简单的RPC框架示意图如下：
![image](https://github.com/asiaWu3/MyRPC/assets/92590536/38df2d39-4da9-4717-a78c-cce42bef7a98)

其中应该包含的组件/功能有：
+ 注册中心：Server启动后，应该把自己的地址（ip+port）告诉注册中心，Client想要请求某个方法时，先询问注册中心可用的Server地址，然后直接向这个地址发起RPC。注册中心可以用Zookeeper、Nacos、Redis等
+ 网络传输：可以用Socket（BIO）或NIO，但更推荐Netty（封装的NIO）
+ 传输协议：需要制定一个两端共识的传输协议，这样在接收到二进制数据后才可以正确解析
+ 序列化和反序列化：在RPC中，一次调用的信息（类/接口、方法名、方法参数等）肯定要封装成对象，又因为在网络传输中传输的是二进制数据，所以必须先把封装好的对象序列化为二进制数据；同样的，在收到二进制数据后，也需要反序列化解析回对象，不推荐使用Java自带的序列化（性能差、不支持跨语言），可以用hessian、kryo、protobuf等等
+ 动态代理：调用方在调用方法时可以像调用本地方法一样，所以这其中需要动态代理来屏蔽其中转化上的细节
+ 负载均衡：调用方在调用时要尽量均衡地选择server

## MyRPC
### 01
简单的方法调用，同步阻塞式IO
### 02
改进点：
1. 封装RpcRequest和RpcResponse，使传输内容统一
2. 支持根据方法名来调用不同的方法
### 03
改进点：
1. Client端利用jdk动态代理让调用远程方法像调用本地方法一样简单
2. Server端提前装配所有的ServiceImpl Bean，可以根据RpcRequest中的interfaceName使用不同的Service
### 04
改进点：
1. 抽象Server为接口，提供多种实现类，其中之前一直用的是SimpleServer，这次新增一个ThreadPoolServer
2. 封装serviceProvider为一个类，提供addService方法，传入service对象，不再手动添加service路径；serviceProvider对象在构造Server时传入
### 05
改进点：
1. 抽象Client为接口，提供多种实现类，其中一直用的是SimpleClient，这次新增NettyClient
2. Server新增实现类NettyServer
3. 初步定义传输协议： [长度][内容]
### 06
1. 提供多种序列化方式：JDK、JSON、Kryo
自定义Serializer接口和多种实现类，自定义MyEncoder/MyDecoder，在其中选择合适的Seralizer
2. 完善传输协议：
![image](https://github.com/asiaWu3/MyRPC/assets/92590536/e5fcd8c4-636a-4f30-a58f-fded93830e0b)

+ 消息类型
  - 1：RpcRequest
  - 2：RpcResponse
  - 3：ping
  - 4：pong
+ 序列化类型
  - 1：JDK
  - 2：JSON
  - 3：Kryo
+ 消息长度：后面消息体的长度
+ 消息体：RpcRequest/RpcResponse对象
### 07
前置知识：
Zookeeper是什么？
Zookeeper是一个分布式协调服务的开源框架，本质上是一个分布式的小文件存储系统。主要用来解决分布式集群中应用系统的一致性问题（强一致性，基于Paxos，满足CP）。可以用作：统一命名服务、分布式配置管理、分布式消息队列、分布式锁、分布式协调。

改进点：
1. 增加注册中心：默认Zookeeper实现（后续改为SPI）   初步实现：服务注册、服务寻找
