package com.meloffy.proto.user;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.53.0)",
    comments = "Source: user.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class UserServiceGrpc {

  private UserServiceGrpc() {}

  public static final String SERVICE_NAME = "user.UserService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.meloffy.proto.user.CreateUserRequest,
      com.meloffy.proto.user.CreateUserResponse> getCreateUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "createUser",
      requestType = com.meloffy.proto.user.CreateUserRequest.class,
      responseType = com.meloffy.proto.user.CreateUserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.meloffy.proto.user.CreateUserRequest,
      com.meloffy.proto.user.CreateUserResponse> getCreateUserMethod() {
    io.grpc.MethodDescriptor<com.meloffy.proto.user.CreateUserRequest, com.meloffy.proto.user.CreateUserResponse> getCreateUserMethod;
    if ((getCreateUserMethod = UserServiceGrpc.getCreateUserMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getCreateUserMethod = UserServiceGrpc.getCreateUserMethod) == null) {
          UserServiceGrpc.getCreateUserMethod = getCreateUserMethod =
              io.grpc.MethodDescriptor.<com.meloffy.proto.user.CreateUserRequest, com.meloffy.proto.user.CreateUserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "createUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.meloffy.proto.user.CreateUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.meloffy.proto.user.CreateUserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("createUser"))
              .build();
        }
      }
    }
    return getCreateUserMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.meloffy.proto.user.GetUserRequest,
      com.meloffy.proto.user.GetUserResponse> getGetUserByEmailOrPhoneMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "getUserByEmailOrPhone",
      requestType = com.meloffy.proto.user.GetUserRequest.class,
      responseType = com.meloffy.proto.user.GetUserResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.meloffy.proto.user.GetUserRequest,
      com.meloffy.proto.user.GetUserResponse> getGetUserByEmailOrPhoneMethod() {
    io.grpc.MethodDescriptor<com.meloffy.proto.user.GetUserRequest, com.meloffy.proto.user.GetUserResponse> getGetUserByEmailOrPhoneMethod;
    if ((getGetUserByEmailOrPhoneMethod = UserServiceGrpc.getGetUserByEmailOrPhoneMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getGetUserByEmailOrPhoneMethod = UserServiceGrpc.getGetUserByEmailOrPhoneMethod) == null) {
          UserServiceGrpc.getGetUserByEmailOrPhoneMethod = getGetUserByEmailOrPhoneMethod =
              io.grpc.MethodDescriptor.<com.meloffy.proto.user.GetUserRequest, com.meloffy.proto.user.GetUserResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "getUserByEmailOrPhone"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.meloffy.proto.user.GetUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.meloffy.proto.user.GetUserResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("getUserByEmailOrPhone"))
              .build();
        }
      }
    }
    return getGetUserByEmailOrPhoneMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.meloffy.proto.user.UpdateLastActivityRequest,
      com.meloffy.proto.user.UpdateLastActivityResponse> getUpdateLastActivityMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "updateLastActivity",
      requestType = com.meloffy.proto.user.UpdateLastActivityRequest.class,
      responseType = com.meloffy.proto.user.UpdateLastActivityResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.meloffy.proto.user.UpdateLastActivityRequest,
      com.meloffy.proto.user.UpdateLastActivityResponse> getUpdateLastActivityMethod() {
    io.grpc.MethodDescriptor<com.meloffy.proto.user.UpdateLastActivityRequest, com.meloffy.proto.user.UpdateLastActivityResponse> getUpdateLastActivityMethod;
    if ((getUpdateLastActivityMethod = UserServiceGrpc.getUpdateLastActivityMethod) == null) {
      synchronized (UserServiceGrpc.class) {
        if ((getUpdateLastActivityMethod = UserServiceGrpc.getUpdateLastActivityMethod) == null) {
          UserServiceGrpc.getUpdateLastActivityMethod = getUpdateLastActivityMethod =
              io.grpc.MethodDescriptor.<com.meloffy.proto.user.UpdateLastActivityRequest, com.meloffy.proto.user.UpdateLastActivityResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "updateLastActivity"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.meloffy.proto.user.UpdateLastActivityRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.meloffy.proto.user.UpdateLastActivityResponse.getDefaultInstance()))
              .setSchemaDescriptor(new UserServiceMethodDescriptorSupplier("updateLastActivity"))
              .build();
        }
      }
    }
    return getUpdateLastActivityMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceStub>() {
        @java.lang.Override
        public UserServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceStub(channel, callOptions);
        }
      };
    return UserServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceBlockingStub>() {
        @java.lang.Override
        public UserServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceBlockingStub(channel, callOptions);
        }
      };
    return UserServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<UserServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<UserServiceFutureStub>() {
        @java.lang.Override
        public UserServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new UserServiceFutureStub(channel, callOptions);
        }
      };
    return UserServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class UserServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void createUser(com.meloffy.proto.user.CreateUserRequest request,
        io.grpc.stub.StreamObserver<com.meloffy.proto.user.CreateUserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateUserMethod(), responseObserver);
    }

    /**
     */
    public void getUserByEmailOrPhone(com.meloffy.proto.user.GetUserRequest request,
        io.grpc.stub.StreamObserver<com.meloffy.proto.user.GetUserResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetUserByEmailOrPhoneMethod(), responseObserver);
    }

    /**
     */
    public void updateLastActivity(com.meloffy.proto.user.UpdateLastActivityRequest request,
        io.grpc.stub.StreamObserver<com.meloffy.proto.user.UpdateLastActivityResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateLastActivityMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getCreateUserMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.meloffy.proto.user.CreateUserRequest,
                com.meloffy.proto.user.CreateUserResponse>(
                  this, METHODID_CREATE_USER)))
          .addMethod(
            getGetUserByEmailOrPhoneMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.meloffy.proto.user.GetUserRequest,
                com.meloffy.proto.user.GetUserResponse>(
                  this, METHODID_GET_USER_BY_EMAIL_OR_PHONE)))
          .addMethod(
            getUpdateLastActivityMethod(),
            io.grpc.stub.ServerCalls.asyncUnaryCall(
              new MethodHandlers<
                com.meloffy.proto.user.UpdateLastActivityRequest,
                com.meloffy.proto.user.UpdateLastActivityResponse>(
                  this, METHODID_UPDATE_LAST_ACTIVITY)))
          .build();
    }
  }

  /**
   */
  public static final class UserServiceStub extends io.grpc.stub.AbstractAsyncStub<UserServiceStub> {
    private UserServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceStub(channel, callOptions);
    }

    /**
     */
    public void createUser(com.meloffy.proto.user.CreateUserRequest request,
        io.grpc.stub.StreamObserver<com.meloffy.proto.user.CreateUserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getUserByEmailOrPhone(com.meloffy.proto.user.GetUserRequest request,
        io.grpc.stub.StreamObserver<com.meloffy.proto.user.GetUserResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetUserByEmailOrPhoneMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateLastActivity(com.meloffy.proto.user.UpdateLastActivityRequest request,
        io.grpc.stub.StreamObserver<com.meloffy.proto.user.UpdateLastActivityResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateLastActivityMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class UserServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<UserServiceBlockingStub> {
    private UserServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.meloffy.proto.user.CreateUserResponse createUser(com.meloffy.proto.user.CreateUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateUserMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.meloffy.proto.user.GetUserResponse getUserByEmailOrPhone(com.meloffy.proto.user.GetUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetUserByEmailOrPhoneMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.meloffy.proto.user.UpdateLastActivityResponse updateLastActivity(com.meloffy.proto.user.UpdateLastActivityRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateLastActivityMethod(), getCallOptions(), request);
    }
  }

  /**
   */
  public static final class UserServiceFutureStub extends io.grpc.stub.AbstractFutureStub<UserServiceFutureStub> {
    private UserServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new UserServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.meloffy.proto.user.CreateUserResponse> createUser(
        com.meloffy.proto.user.CreateUserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.meloffy.proto.user.GetUserResponse> getUserByEmailOrPhone(
        com.meloffy.proto.user.GetUserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetUserByEmailOrPhoneMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.meloffy.proto.user.UpdateLastActivityResponse> updateLastActivity(
        com.meloffy.proto.user.UpdateLastActivityRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateLastActivityMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_USER = 0;
  private static final int METHODID_GET_USER_BY_EMAIL_OR_PHONE = 1;
  private static final int METHODID_UPDATE_LAST_ACTIVITY = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final UserServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(UserServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_CREATE_USER:
          serviceImpl.createUser((com.meloffy.proto.user.CreateUserRequest) request,
              (io.grpc.stub.StreamObserver<com.meloffy.proto.user.CreateUserResponse>) responseObserver);
          break;
        case METHODID_GET_USER_BY_EMAIL_OR_PHONE:
          serviceImpl.getUserByEmailOrPhone((com.meloffy.proto.user.GetUserRequest) request,
              (io.grpc.stub.StreamObserver<com.meloffy.proto.user.GetUserResponse>) responseObserver);
          break;
        case METHODID_UPDATE_LAST_ACTIVITY:
          serviceImpl.updateLastActivity((com.meloffy.proto.user.UpdateLastActivityRequest) request,
              (io.grpc.stub.StreamObserver<com.meloffy.proto.user.UpdateLastActivityResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    UserServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.meloffy.proto.user.UserProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("UserService");
    }
  }

  private static final class UserServiceFileDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier {
    UserServiceFileDescriptorSupplier() {}
  }

  private static final class UserServiceMethodDescriptorSupplier
      extends UserServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    UserServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (UserServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserServiceFileDescriptorSupplier())
              .addMethod(getCreateUserMethod())
              .addMethod(getGetUserByEmailOrPhoneMethod())
              .addMethod(getUpdateLastActivityMethod())
              .build();
        }
      }
    }
    return result;
  }
}
