package com.bs.bsboard.v01.board.vo;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QBoardVO is a Querydsl query type for BoardVO
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoardVO extends EntityPathBase<BoardVO> {

    private static final long serialVersionUID = -1606118126L;

    public static final QBoardVO boardVO = new QBoardVO("boardVO");

    public final NumberPath<Long> boardId = createNumber("boardId", Long.class);

    public final StringPath boardName = createString("boardName");

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath desc = createString("desc");

    public final StringPath title = createString("title");

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QBoardVO(String variable) {
        super(BoardVO.class, forVariable(variable));
    }

    public QBoardVO(Path<? extends BoardVO> path) {
        super(path.getType(), path.getMetadata());
    }

    public QBoardVO(PathMetadata metadata) {
        super(BoardVO.class, metadata);
    }

}

