package io.nullptr.helloworld;

import io.grpc.stub.StreamObserver;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class GreeterServiceImpl extends GreeterServiceGrpc.GreeterServiceImplBase {

    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply helloReply = HelloReply.newBuilder()
                .setMessage("Hello " + request.getName())
                .build();

        responseObserver.onNext(helloReply);
        responseObserver.onCompleted();
    }
}
