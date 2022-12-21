package com.bs.bsboard.v01.board;

import com.bs.bsboard.v01.board.dao.BoardRepository;
import com.bs.bsboard.v01.board.vo.BoardResultVO;
import com.bs.bsboard.v01.board.vo.BoardVO;
import com.bs.bsboard.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(path = "/v01/board")
public class BoardController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private BoardRepository boardRepository;


    @GetMapping(path = "/{board_id}")
    public ResponseEntity<BoardResultVO> getById(
            @PathVariable Long board_id,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> getById, @Param = {}", board_id);

        BoardResultVO rvo = new BoardResultVO();
        List<BoardVO> rows = new ArrayList<BoardVO>();

        Optional<BoardVO> oboard = boardRepository.findById(board_id);
        if (oboard.isPresent()) {
            rows.add(oboard.get());
        } else {
            // TODO throw 404 not_found exception
        }

        rvo.setResultRows(rows);
        rvo.setNumRows(rows.size());

        return ResponseEntity.status(HttpStatus.OK).body(rvo);
    }


    @PostMapping()
    public ResponseEntity<BoardResultVO> add(
            @RequestBody(required = true) Map<String, String> rbody,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> add, @Param = {}", rbody);

        BoardVO vo = new BoardVO();
        vo.setBoardName(StringUtil.trimToEmpty(rbody.get("name")));
        vo.setTitle(StringUtil.trimToEmpty(rbody.get("title")));
        vo.setDesc(StringUtil.trimToEmpty(rbody.get("desc")));
        vo.setDeletedAt(null);
        BoardVO vo_saved = boardRepository.save(vo);

        // ---- 추가한 user 다시 조회
        return this.getById(vo_saved.getBoardId(), req, resp);
    }

    @PutMapping(path = "/{board_id}")
    public ResponseEntity<BoardResultVO> update(
            @PathVariable Long board_id,
            @RequestBody(required = true) Map<String, String> rbody,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> update, @Param = {}", board_id);

        BoardResultVO rvo = new BoardResultVO();
        List<BoardVO> rows = new ArrayList<BoardVO>();

        BoardVO vo = new BoardVO();
        vo.setTitle(StringUtil.trimToEmpty(rbody.get("title")));
        vo.setDesc(StringUtil.trimToEmpty(rbody.get("desc")));

        long num_updated = boardRepository.updateBasicInfo(board_id, vo);
        log.debug("> #updated = {}", num_updated);

        // ---- 수정된 user 다시 조회
        return this.getById(board_id, req, resp);
    }

    @DeleteMapping(path = "/{board_id}")
    public ResponseEntity<BoardResultVO> markAsDeleted(
            @PathVariable Long board_id,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> markAsDeleted, @Param = {}", board_id);

        BoardResultVO rvo = new BoardResultVO();
        List<BoardVO> rows = new ArrayList<BoardVO>();

        long num_updated = boardRepository.markAsDeleted(board_id);
        log.debug("> #updated = {}", num_updated);

        rvo.setResultRows(rows);
        rvo.setNumRows(rows.size());

        return ResponseEntity.status(HttpStatus.OK).body(rvo);
    }

    /**
     * 사용자 목록 조회(Paging 지원)
     *
     * @param rbody
     * @param req
     * @param resp
     * @return
     */
    @PostMapping(path = "/list")
    public ResponseEntity<BoardResultVO> getList(
            @RequestBody(required = true) Map<String, String> rbody,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> getList, @Param = {}", rbody);

        BoardResultVO rvo = new BoardResultVO();

        List<BoardVO> user_list = boardRepository.selectList(
                StringUtil.trimToEmpty(rbody.get("KW_NAME")),
                StringUtil.trimToEmpty(rbody.get("ORDER_BY")),
                Long.parseLong(rbody.get("OFFSET")),
                Long.parseLong(rbody.get("LIMIT"))
        );
        rvo.setResultRows(user_list);
        rvo.setNumRows(user_list.size());

        return ResponseEntity.status(HttpStatus.OK).body(rvo);
    }

    @PostMapping(path = "/count")
    public ResponseEntity<BoardResultVO> getCount(
            @RequestBody(required = true) Map<String, String> rbody,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> getList, @Param = {}", rbody);

        BoardResultVO rvo = new BoardResultVO();

        Long user_count = boardRepository.countList(
                StringUtil.trimToEmpty(rbody.get("KW_NAME"))
        );
        rvo.setResultRows(new ArrayList<BoardVO>());

        if (user_count != null) {
            rvo.setNumRows(user_count);
        } else {
            rvo.setNumRows(0);
        }

        return ResponseEntity.status(HttpStatus.OK).body(rvo);
    }

}
