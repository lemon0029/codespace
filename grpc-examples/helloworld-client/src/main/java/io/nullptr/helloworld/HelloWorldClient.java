package io.nullptr.helloworld;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;

public class HelloWorldClient {

    public static void main(String[] args) {
        ManagedChannel channel = Grpc.newChannelBuilderForAddress("localhost", 50010, InsecureChannelCredentials.create())
                .build();

        GreeterServiceGrpc.GreeterServiceBlockingStub greeterServiceBlockingStub = GreeterServiceGrpc.newBlockingStub(channel);

        HelloRequest helloRequest = HelloRequest.newBuilder()
                .setName("World")
                .build();

        HelloReply helloReply = greeterServiceBlockingStub.sayHello(helloRequest);
        System.out.println(helloReply);
    }
}
