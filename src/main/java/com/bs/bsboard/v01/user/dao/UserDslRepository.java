package com.bs.bsboard.v01.user.dao;

import com.bs.bsboard.v01.user.vo.UserVO;

import java.util.List;

public interface UserDslRepository {
    List<UserVO> selectAll();
    List<UserVO> selectList(String kw_loginid, String kw_name, String order_by, long offset, long limit);
    Long countList(String kw_loginid, String kw_name);
    long updateBasicInfo(long user_id, UserVO user);
    long markAsDeleted(long user_id);
}
