package com.meloffy.us.grpc;

import com.meloffy.proto.user.*;
import com.meloffy.us.service.UserService;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {

    private static final Logger log =
            LoggerFactory.getLogger(UserServiceGrpcImpl.class);

    @Autowired
    private UserService userService;

    @Override
    public void createUser(CreateUserRequest request,
                           StreamObserver<CreateUserResponse> responseObserver) {

        log.info("[gRPC] createUser called");

        String newUserId = userService.createUser(
                request.getKeycloakUserId(),
                request.getUsername(),
                request.getFullName(),
                request.getEmail(),
                request.getPhone(),
                request.getPassword()
        );

        log.info("[gRPC] User persisted with keycloakUserId={}", newUserId);

        CreateUserResponse response = CreateUserResponse.newBuilder()
                .setUserId(newUserId)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void updateLastActivity(UpdateLastActivityRequest request,
                                   StreamObserver<UpdateLastActivityResponse> responseObserver) {
        log.info("[gRPC] updateLastActivity called");
        boolean status = userService.updateLastActivity(
                request.getKeycloakUserid()
        );
        log.info("[gRPC] User updated with keycloakUserId={}", status);

        UpdateLastActivityResponse resp = UpdateLastActivityResponse.newBuilder()
                .setSuccess(status)
                .build();
        responseObserver.onNext(resp);
        responseObserver.onCompleted();
    }
}