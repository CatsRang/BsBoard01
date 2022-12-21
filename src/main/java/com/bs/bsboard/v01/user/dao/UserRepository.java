package com.bs.bsboard.v01.user.dao;

import com.bs.bsboard.v01.user.vo.UserVO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserVO, Long>, UserDslRepository {

}
