package com.bs.bsboard.v01.board.dao;

import com.bs.bsboard.v01.board.vo.BoardVO;

import java.util.List;

public interface BoardDslRepository {
    List<BoardVO> selectAll();
    List<BoardVO> selectList(String kw_name, String order_by, long offset, long limit);
    Long countList(String kw_name);
    long updateBasicInfo(long board_id, BoardVO board);
    long markAsDeleted(long board_id);
}
