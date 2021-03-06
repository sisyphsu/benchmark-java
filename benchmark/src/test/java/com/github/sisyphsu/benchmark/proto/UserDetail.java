// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: resources/proto/test.proto

package com.github.sisyphsu.benchmark.proto;

/**
 * Protobuf type {@code com.github.sisyphsu.benchmark.proto.UserDetail}
 */
public  final class UserDetail extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:com.github.sisyphsu.benchmark.proto.UserDetail)
    UserDetailOrBuilder {
private static final long serialVersionUID = 0L;
  // Use UserDetail.newBuilder() to construct.
  private UserDetail(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private UserDetail() {
    nickname_ = "";
    avatarUrl_ = "";
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private UserDetail(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            id_ = input.readInt64();
            break;
          }
          case 16: {

            age_ = input.readInt32();
            break;
          }
          case 26: {
            String s = input.readStringRequireUtf8();

            nickname_ = s;
            break;
          }
          case 34: {
            String s = input.readStringRequireUtf8();

            avatarUrl_ = s;
            break;
          }
          case 80: {

            createTime_ = input.readInt64();
            break;
          }
          case 88: {

            updateTime_ = input.readInt64();
            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.github.sisyphsu.benchmark.proto.Test.internal_static_com_github_sisyphsu_nakedata_proto_UserDetail_descriptor;
  }

  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.github.sisyphsu.benchmark.proto.Test.internal_static_com_github_sisyphsu_nakedata_proto_UserDetail_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.github.sisyphsu.benchmark.proto.UserDetail.class, com.github.sisyphsu.benchmark.proto.UserDetail.Builder.class);
  }

  public static final int ID_FIELD_NUMBER = 1;
  private long id_;
  /**
   * <code>int64 id = 1;</code>
   */
  public long getId() {
    return id_;
  }

  public static final int AGE_FIELD_NUMBER = 2;
  private int age_;
  /**
   * <code>int32 age = 2;</code>
   */
  public int getAge() {
    return age_;
  }

  public static final int NICKNAME_FIELD_NUMBER = 3;
  private volatile Object nickname_;
  /**
   * <code>string nickname = 3;</code>
   */
  public String getNickname() {
    Object ref = nickname_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      nickname_ = s;
      return s;
    }
  }
  /**
   * <code>string nickname = 3;</code>
   */
  public com.google.protobuf.ByteString
      getNicknameBytes() {
    Object ref = nickname_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      nickname_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int AVATARURL_FIELD_NUMBER = 4;
  private volatile Object avatarUrl_;
  /**
   * <code>string avatarUrl = 4;</code>
   */
  public String getAvatarUrl() {
    Object ref = avatarUrl_;
    if (ref instanceof String) {
      return (String) ref;
    } else {
      com.google.protobuf.ByteString bs =
          (com.google.protobuf.ByteString) ref;
      String s = bs.toStringUtf8();
      avatarUrl_ = s;
      return s;
    }
  }
  /**
   * <code>string avatarUrl = 4;</code>
   */
  public com.google.protobuf.ByteString
      getAvatarUrlBytes() {
    Object ref = avatarUrl_;
    if (ref instanceof String) {
      com.google.protobuf.ByteString b =
          com.google.protobuf.ByteString.copyFromUtf8(
              (String) ref);
      avatarUrl_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int CREATETIME_FIELD_NUMBER = 10;
  private long createTime_;
  /**
   * <code>int64 createTime = 10;</code>
   */
  public long getCreateTime() {
    return createTime_;
  }

  public static final int UPDATETIME_FIELD_NUMBER = 11;
  private long updateTime_;
  /**
   * <code>int64 updateTime = 11;</code>
   */
  public long getUpdateTime() {
    return updateTime_;
  }

  private byte memoizedIsInitialized = -1;
  @Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (id_ != 0L) {
      output.writeInt64(1, id_);
    }
    if (age_ != 0) {
      output.writeInt32(2, age_);
    }
    if (!getNicknameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 3, nickname_);
    }
    if (!getAvatarUrlBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, avatarUrl_);
    }
    if (createTime_ != 0L) {
      output.writeInt64(10, createTime_);
    }
    if (updateTime_ != 0L) {
      output.writeInt64(11, updateTime_);
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (id_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(1, id_);
    }
    if (age_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(2, age_);
    }
    if (!getNicknameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, nickname_);
    }
    if (!getAvatarUrlBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, avatarUrl_);
    }
    if (createTime_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(10, createTime_);
    }
    if (updateTime_ != 0L) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt64Size(11, updateTime_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.github.sisyphsu.benchmark.proto.UserDetail)) {
      return super.equals(obj);
    }
    com.github.sisyphsu.benchmark.proto.UserDetail other = (com.github.sisyphsu.benchmark.proto.UserDetail) obj;

    if (getId()
        != other.getId()) return false;
    if (getAge()
        != other.getAge()) return false;
    if (!getNickname()
        .equals(other.getNickname())) return false;
    if (!getAvatarUrl()
        .equals(other.getAvatarUrl())) return false;
    if (getCreateTime()
        != other.getCreateTime()) return false;
    if (getUpdateTime()
        != other.getUpdateTime()) return false;
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + ID_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getId());
    hash = (37 * hash) + AGE_FIELD_NUMBER;
    hash = (53 * hash) + getAge();
    hash = (37 * hash) + NICKNAME_FIELD_NUMBER;
    hash = (53 * hash) + getNickname().hashCode();
    hash = (37 * hash) + AVATARURL_FIELD_NUMBER;
    hash = (53 * hash) + getAvatarUrl().hashCode();
    hash = (37 * hash) + CREATETIME_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getCreateTime());
    hash = (37 * hash) + UPDATETIME_FIELD_NUMBER;
    hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
        getUpdateTime());
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.github.sisyphsu.benchmark.proto.UserDetail parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.github.sisyphsu.benchmark.proto.UserDetail parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.github.sisyphsu.benchmark.proto.UserDetail prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @Override
  protected Builder newBuilderForType(
      BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code com.github.sisyphsu.benchmark.proto.UserDetail}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:com.github.sisyphsu.benchmark.proto.UserDetail)
      com.github.sisyphsu.benchmark.proto.UserDetailOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.github.sisyphsu.benchmark.proto.Test.internal_static_com_github_sisyphsu_nakedata_proto_UserDetail_descriptor;
    }

    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.github.sisyphsu.benchmark.proto.Test.internal_static_com_github_sisyphsu_nakedata_proto_UserDetail_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.github.sisyphsu.benchmark.proto.UserDetail.class, com.github.sisyphsu.benchmark.proto.UserDetail.Builder.class);
    }

    // Construct using com.github.sisyphsu.benchmark.proto.UserDetail.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @Override
    public Builder clear() {
      super.clear();
      id_ = 0L;

      age_ = 0;

      nickname_ = "";

      avatarUrl_ = "";

      createTime_ = 0L;

      updateTime_ = 0L;

      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.github.sisyphsu.benchmark.proto.Test.internal_static_com_github_sisyphsu_nakedata_proto_UserDetail_descriptor;
    }

    @Override
    public com.github.sisyphsu.benchmark.proto.UserDetail getDefaultInstanceForType() {
      return com.github.sisyphsu.benchmark.proto.UserDetail.getDefaultInstance();
    }

    @Override
    public com.github.sisyphsu.benchmark.proto.UserDetail build() {
      com.github.sisyphsu.benchmark.proto.UserDetail result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public com.github.sisyphsu.benchmark.proto.UserDetail buildPartial() {
      com.github.sisyphsu.benchmark.proto.UserDetail result = new com.github.sisyphsu.benchmark.proto.UserDetail(this);
      result.id_ = id_;
      result.age_ = age_;
      result.nickname_ = nickname_;
      result.avatarUrl_ = avatarUrl_;
      result.createTime_ = createTime_;
      result.updateTime_ = updateTime_;
      onBuilt();
      return result;
    }

    @Override
    public Builder clone() {
      return super.clone();
    }
    @Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    @Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    @Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.github.sisyphsu.benchmark.proto.UserDetail) {
        return mergeFrom((com.github.sisyphsu.benchmark.proto.UserDetail)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.github.sisyphsu.benchmark.proto.UserDetail other) {
      if (other == com.github.sisyphsu.benchmark.proto.UserDetail.getDefaultInstance()) return this;
      if (other.getId() != 0L) {
        setId(other.getId());
      }
      if (other.getAge() != 0) {
        setAge(other.getAge());
      }
      if (!other.getNickname().isEmpty()) {
        nickname_ = other.nickname_;
        onChanged();
      }
      if (!other.getAvatarUrl().isEmpty()) {
        avatarUrl_ = other.avatarUrl_;
        onChanged();
      }
      if (other.getCreateTime() != 0L) {
        setCreateTime(other.getCreateTime());
      }
      if (other.getUpdateTime() != 0L) {
        setUpdateTime(other.getUpdateTime());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @Override
    public final boolean isInitialized() {
      return true;
    }

    @Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.github.sisyphsu.benchmark.proto.UserDetail parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.github.sisyphsu.benchmark.proto.UserDetail) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private long id_ ;
    /**
     * <code>int64 id = 1;</code>
     */
    public long getId() {
      return id_;
    }
    /**
     * <code>int64 id = 1;</code>
     */
    public Builder setId(long value) {

      id_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 id = 1;</code>
     */
    public Builder clearId() {

      id_ = 0L;
      onChanged();
      return this;
    }

    private int age_ ;
    /**
     * <code>int32 age = 2;</code>
     */
    public int getAge() {
      return age_;
    }
    /**
     * <code>int32 age = 2;</code>
     */
    public Builder setAge(int value) {

      age_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 age = 2;</code>
     */
    public Builder clearAge() {

      age_ = 0;
      onChanged();
      return this;
    }

    private Object nickname_ = "";
    /**
     * <code>string nickname = 3;</code>
     */
    public String getNickname() {
      Object ref = nickname_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        nickname_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string nickname = 3;</code>
     */
    public com.google.protobuf.ByteString
        getNicknameBytes() {
      Object ref = nickname_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        nickname_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string nickname = 3;</code>
     */
    public Builder setNickname(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }

      nickname_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string nickname = 3;</code>
     */
    public Builder clearNickname() {

      nickname_ = getDefaultInstance().getNickname();
      onChanged();
      return this;
    }
    /**
     * <code>string nickname = 3;</code>
     */
    public Builder setNicknameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

      nickname_ = value;
      onChanged();
      return this;
    }

    private Object avatarUrl_ = "";
    /**
     * <code>string avatarUrl = 4;</code>
     */
    public String getAvatarUrl() {
      Object ref = avatarUrl_;
      if (!(ref instanceof String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        avatarUrl_ = s;
        return s;
      } else {
        return (String) ref;
      }
    }
    /**
     * <code>string avatarUrl = 4;</code>
     */
    public com.google.protobuf.ByteString
        getAvatarUrlBytes() {
      Object ref = avatarUrl_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b =
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        avatarUrl_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <code>string avatarUrl = 4;</code>
     */
    public Builder setAvatarUrl(
        String value) {
      if (value == null) {
    throw new NullPointerException();
  }

      avatarUrl_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>string avatarUrl = 4;</code>
     */
    public Builder clearAvatarUrl() {

      avatarUrl_ = getDefaultInstance().getAvatarUrl();
      onChanged();
      return this;
    }
    /**
     * <code>string avatarUrl = 4;</code>
     */
    public Builder setAvatarUrlBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);

      avatarUrl_ = value;
      onChanged();
      return this;
    }

    private long createTime_ ;
    /**
     * <code>int64 createTime = 10;</code>
     */
    public long getCreateTime() {
      return createTime_;
    }
    /**
     * <code>int64 createTime = 10;</code>
     */
    public Builder setCreateTime(long value) {

      createTime_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 createTime = 10;</code>
     */
    public Builder clearCreateTime() {

      createTime_ = 0L;
      onChanged();
      return this;
    }

    private long updateTime_ ;
    /**
     * <code>int64 updateTime = 11;</code>
     */
    public long getUpdateTime() {
      return updateTime_;
    }
    /**
     * <code>int64 updateTime = 11;</code>
     */
    public Builder setUpdateTime(long value) {

      updateTime_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int64 updateTime = 11;</code>
     */
    public Builder clearUpdateTime() {

      updateTime_ = 0L;
      onChanged();
      return this;
    }
    @Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:com.github.sisyphsu.benchmark.proto.UserDetail)
  }

  // @@protoc_insertion_point(class_scope:com.github.sisyphsu.benchmark.proto.UserDetail)
  private static final com.github.sisyphsu.benchmark.proto.UserDetail DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.github.sisyphsu.benchmark.proto.UserDetail();
  }

  public static com.github.sisyphsu.benchmark.proto.UserDetail getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<UserDetail>
      PARSER = new com.google.protobuf.AbstractParser<UserDetail>() {
    @Override
    public UserDetail parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new UserDetail(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<UserDetail> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<UserDetail> getParserForType() {
    return PARSER;
  }

  @Override
  public com.github.sisyphsu.benchmark.proto.UserDetail getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

