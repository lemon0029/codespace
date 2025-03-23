package io.nullptr.helloworld;

import io.grpc.Grpc;
import io.grpc.Server;
import io.grpc.InsecureServerCredentials;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HelloWorldServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Server server = Grpc.newServerBuilderForPort(50010, InsecureServerCredentials.create())
                .addService(new GreeterServiceImpl())
                .executor(executorService)
                .build();

        server.start();

        System.out.println("gRPC Sever Started!");
        server.awaitTermination();
    }
}
