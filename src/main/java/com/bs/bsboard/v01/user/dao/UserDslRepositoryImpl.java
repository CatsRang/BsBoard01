package com.bs.bsboard.v01.user.dao;

import com.bs.bsboard.v01.user.vo.UserVO;
import com.bs.bsboard.util.StringUtil;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.bs.bsboard.v01.user.vo.QUserVO.userVO;

@Repository
public class UserDslRepositoryImpl implements UserDslRepository {
    private final JPAQueryFactory jpaQueryFactory;

    public UserDslRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public List<UserVO> selectAll() {
        return jpaQueryFactory.selectFrom(userVO)
                .where(userVO.deletedAt.isNull())
                .fetch();
    }

    @Override
    public List<UserVO> selectList(String kw_loginid, String kw_name, String order_by, long offset, long limit) {
        // ---- where 조건
        BooleanBuilder where_builder = new BooleanBuilder();
        where_builder.and(userVO.deletedAt.isNull());

        if (StringUtil.isNotEmpty(kw_loginid)) {
            where_builder.and(userVO.loginId.like(kw_loginid));
        }

        if (StringUtil.isNotEmpty(kw_name)) {
            where_builder.and(userVO.realName.like(kw_name));
        }

        // ---- order by 조건
        List<OrderSpecifier<?>> orderList = new ArrayList<>();

        switch (order_by) {
            case "LOGIN_ID_ASC":
                orderList.add(userVO.loginId.asc());
                break;
            case "LOGIN_ID_DESC":
                orderList.add(userVO.loginId.desc());
                break;
            case "NAME_ASC":
                orderList.add(userVO.realName.asc());
                break;
            case "NAME_DESC":
                orderList.add(userVO.realName.desc());
                break;
            case "DATE_ASC":
                orderList.add(userVO.updatedAt.asc());
                break;
            default:
                orderList.add(userVO.updatedAt.desc());
        }

        return jpaQueryFactory
                .selectFrom(userVO)
                .where(where_builder)
                .orderBy((OrderSpecifier<?>[]) orderList.toArray(new OrderSpecifier<?>[orderList.size()]))
                .offset(offset).limit(limit)
                .fetch();
    }

    @Override
    @Nullable
    public Long countList(String kw_loginid, String kw_name) {
        JPAQuery<Long> jq = jpaQueryFactory
                .select(userVO.userId.count())
                .from(userVO)
                .where(userVO.deletedAt.isNull());

        if (StringUtil.isNotEmpty(kw_loginid)) {
            jq = jq.where(userVO.loginId.like(kw_loginid));
        }

        if (StringUtil.isNotEmpty(kw_name)) {
            jq = jq.where(userVO.realName.like(kw_name));
        }

        // ----[FN] return #rows fetched
        return jq.fetchOne();
    }

    @Override
    @Transactional
    public long updateBasicInfo(long user_id, UserVO user) {
        return jpaQueryFactory.update(userVO)
                .where(userVO.userId.eq(user_id))
                .set(userVO.password, user.getPassword())
                .set(userVO.realName, user.getRealName())
                .set(userVO.updatedAt, LocalDateTime.now())
                .execute();
    }

    @Override
    @Transactional
    public long markAsDeleted(long user_id) {
        return jpaQueryFactory.update(userVO)
                .where(userVO.userId.eq(user_id))
                .set(userVO.deletedAt, LocalDateTime.now())
                .execute();
    }
}
