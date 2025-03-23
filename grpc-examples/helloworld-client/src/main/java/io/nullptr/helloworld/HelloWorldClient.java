package io.nullptr.helloworld;

import io.grpc.Grpc;
import io.grpc.InsecureChannelCredentials;
import io.grpc.ManagedChannel;
import io.grpc.health.v1.HealthCheckRequest;
import io.grpc.health.v1.HealthCheckResponse;
import io.grpc.health.v1.HealthGrpc;

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

        HealthGrpc.HealthBlockingStub healthBlockingStub = HealthGrpc.newBlockingStub(channel);
        HealthCheckRequest healthCheckRequest = HealthCheckRequest.newBuilder().build();
        HealthCheckResponse healthCheckResponse = healthBlockingStub.check(healthCheckRequest);
        System.out.println(healthCheckResponse);
    }
}
