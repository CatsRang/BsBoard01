package com.bs.bsboard.v01.user.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUserVO is a Querydsl query type for UserVO
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUserVO extends EntityPathBase<UserVO> {

    private static final long serialVersionUID = -334677610L;

    public static final QUserVO userVO = new QUserVO("userVO");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath email = createString("email");

    public final StringPath loginId = createString("loginId");

    public final StringPath password = createString("password");

    public final StringPath realName = createString("realName");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public final StringPath userGroup = createString("userGroup");

    public final NumberPath<Long> userId = createNumber("userId", Long.class);

    public QUserVO(String variable) {
        super(UserVO.class, forVariable(variable));
    }

    public QUserVO(Path<? extends UserVO> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUserVO(PathMetadata metadata) {
        super(UserVO.class, metadata);
    }

}

