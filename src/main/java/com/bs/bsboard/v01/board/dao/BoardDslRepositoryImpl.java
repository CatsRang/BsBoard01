package com.bs.bsboard.v01.board.dao;

import com.bs.bsboard.v01.board.vo.BoardVO;
import com.bs.bsboard.util.StringUtil;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static com.bs.bsboard.v01.board.vo.QBoardVO.boardVO;

@Repository
public class BoardDslRepositoryImpl implements BoardDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public BoardDslRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<BoardVO> selectAll() {
        return jpaQueryFactory.selectFrom(boardVO)
                .where(boardVO.deletedAt.isNull())
                .fetch();
    }

    @Override
    public List<BoardVO> selectList(String kw_name, String order_by, long offset, long limit) {
        JPAQuery<BoardVO> jq = jpaQueryFactory
                .selectFrom(boardVO)
                .where(boardVO.deletedAt.isNull());

        if (StringUtil.isNotEmpty(kw_name)) {
            jq = jq.where(boardVO.boardName.like(kw_name));
        }

        switch (order_by) {
            case "NAME_ASC":
                jq = jq.orderBy(boardVO.boardName.asc());
                break;
            case "NAME_DESC":
                jq = jq.orderBy(boardVO.boardName.desc());
                break;
            case "DATE_ASC":
                jq = jq.orderBy(boardVO.updatedAt.asc());
                break;
            default:
                jq = jq.orderBy(boardVO.updatedAt.desc());
        }

        return jq.offset(offset).limit(limit).fetch();
    }

    @Override
    @Nullable
    public Long countList(String kw_name) {
        JPAQuery<Long> jq = jpaQueryFactory
                .select(boardVO.boardId.count())
                .from(boardVO)
                .where(boardVO.deletedAt.isNull());

        if (StringUtil.isNotEmpty(kw_name)) {
            jq = jq.where(boardVO.boardName.like(kw_name));
        }

        Long count = jq.fetchOne();

        return count;
    }

    @Override
    @Transactional
    public long updateBasicInfo(long board_id, BoardVO board) {
        return jpaQueryFactory.update(boardVO)
                .where(boardVO.boardId.eq(board_id))
                .set(boardVO.title, board.getTitle())
                .set(boardVO.desc, board.getDesc())
                .set(boardVO.updatedAt, LocalDateTime.now())
                .execute();
    }

    @Override
    @Transactional
    public long markAsDeleted(long board_id) {
        return jpaQueryFactory.update(boardVO)
                .where(boardVO.boardId.eq(board_id))
                .set(boardVO.deletedAt, LocalDateTime.now())
                .execute();
    }
}
