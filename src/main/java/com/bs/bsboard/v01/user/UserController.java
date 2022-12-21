package com.bs.bsboard.v01.user;

import com.bs.bsboard.v01.user.dao.UserRepository;
import com.bs.bsboard.v01.user.vo.UserResultVO;
import com.bs.bsboard.v01.user.vo.UserVO;
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
@RequestMapping(path = "/user/V01")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;

    /**
     * ID로 사용자 조회
     *
     * @param user_id
     * @param req
     * @param resp
     * @return
     */
    @GetMapping(path = "/{user_id}")
    public ResponseEntity<UserResultVO> getById(
            @PathVariable Long user_id,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> getById, @RequestParam = {}", user_id);

        UserResultVO rvo = new UserResultVO();
        List<UserVO> rows = new ArrayList<UserVO>();

        Optional<UserVO> ouser = userRepository.findById(user_id);
        if (ouser.isPresent()) {
            rows.add(ouser.get());
        } else {
            // TODO throw 404 not_found exception
        }

        rvo.setResultRows(rows);
        rvo.setNumRows(rows.size());

        return ResponseEntity.status(HttpStatus.OK).body(rvo);
    }

    @PostMapping()
    public ResponseEntity<UserResultVO> add(
            @RequestBody(required = true) Map<String, String> rbody,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> add, @RequestParam = {}", rbody);

        UserVO vo = new UserVO();
        vo.setLoginId(StringUtil.trimToEmpty(rbody.get("login_id")));
        // FIXME
        vo.setPassword(StringUtil.trimToEmpty(rbody.get("password")));
        //vo.setUserUuid(UuidUtil.generate());
        vo.setRealName(StringUtil.trimToEmpty(rbody.get("real_name")));
        vo.setDeletedAt(null);
        UserVO vo_saved = userRepository.save(vo);

        // ---- 추가한 user 다시 조회
        return this.getById(vo_saved.getUserId(), req, resp);
    }

    @PutMapping(path = "/{user_id}")
    public ResponseEntity<UserResultVO> update(
            @PathVariable Long user_id,
            @RequestBody(required = true) Map<String, String> rbody,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> update, @RequestParam = {}", user_id);

        UserResultVO rvo = new UserResultVO();
        List<UserVO> rows = new ArrayList<UserVO>();

        UserVO vo = new UserVO();
        vo.setUserId(user_id);
        // FIXME
        vo.setPassword(StringUtil.trimToEmpty(rbody.get("password")));
        //vo.setUserUuid(UuidUtil.generate());
        vo.setRealName(StringUtil.trimToEmpty(rbody.get("real_name")));
        long num_updated = userRepository.updateBasicInfo(user_id, vo);
        log.debug("> #updated = {}", num_updated);

        // ---- 수정된 user 다시 조회
        return this.getById(user_id, req, resp);
    }

    @DeleteMapping(path = "/{user_id}")
    public ResponseEntity<UserResultVO> markAsDeleted(
            @PathVariable Long user_id,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> markAsDeleted, @RequestParam = {}", user_id);

        UserResultVO rvo = new UserResultVO();
        List<UserVO> rows = new ArrayList<UserVO>();

        long num_updated = userRepository.markAsDeleted(user_id);
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
    public ResponseEntity<UserResultVO> getList(
            @RequestBody(required = true) Map<String, String> rbody,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> getList, @RequestParam = {}", rbody);

        UserResultVO rvo = new UserResultVO();

        List<UserVO> user_list = userRepository.selectList(
                StringUtil.trimToEmpty(rbody.get("KW_LOGIN_ID")),
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
    public ResponseEntity<UserResultVO> getCount(
            @RequestBody(required = true) Map<String, String> rbody,
            HttpServletRequest req, HttpServletResponse resp
    ) {
        log.debug("> getList, @RequestParam = {}", rbody);

        UserResultVO rvo = new UserResultVO();

        Long user_count = userRepository.countList(
                StringUtil.trimToEmpty(rbody.get("KW_LOGIN_ID")),
                StringUtil.trimToEmpty(rbody.get("KW_NAME"))
        );
        rvo.setResultRows(new ArrayList<UserVO>());

        if (user_count != null) {
            rvo.setNumRows(user_count);
        } else {
            rvo.setNumRows(0);
        }

        return ResponseEntity.status(HttpStatus.OK).body(rvo);
    }
}
