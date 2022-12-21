package com.bs.bsboard.v01.board.dao;

import com.bs.bsboard.v01.board.vo.BoardVO;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<BoardVO, Long>, BoardDslRepository {

}
