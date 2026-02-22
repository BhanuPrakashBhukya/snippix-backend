package com.meloffy.snippix.auth_service.grpc;

import com.meloffy.proto.user.*;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Component;

@Component
public class UserServiceGrpcClient {

    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub stub;

    public String createUser(String keyCloakUserId, String username, String fullName, String email, String phone) {

        CreateUserRequest request = CreateUserRequest.newBuilder()
                .setKeycloakUserId(keyCloakUserId)
                .setUsername(username)
                .setFullName(fullName)
                .setEmail(email == null ? "" : email)
                .setPhone(phone == null ? "" : phone)
                .build();

        CreateUserResponse response = stub.createUser(request);

        return response.getUserId();
    }

    public boolean updateLastActivity(String keycloakUserId) {
        UpdateLastActivityRequest req = UpdateLastActivityRequest.newBuilder()
                .setKeycloakUserid(keycloakUserId)
                .build();

        UpdateLastActivityResponse resp = stub.updateLastActivity(req);
        return resp.getSuccess();
    }
}
