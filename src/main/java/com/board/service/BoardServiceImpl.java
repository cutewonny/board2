package com.board.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import com.board.dao.BoardDAO;
import com.board.domain.BoardVO;

@Service
public class BoardServiceImpl implements BoardService {

	@Inject
	private BoardDAO dao;
	
	// 게시물 목록 구현
	@Override
	public List<BoardVO> list() throws Exception {
		return dao.list();
	}

	// 게시물 작성 구현
	@Override
	public void write(BoardVO vo) throws Exception {
		dao.write(vo);
	}

	// 게시물 조회 구현
	@Override
	public BoardVO view(int bno) throws Exception {
		return dao.view(bno);
	}

	// 게시물 수정 구현
	@Override
	public void modify(BoardVO vo) throws Exception {
		dao.modify(vo);
	}

	@Override
	public void delete(int bno) throws Exception {
		dao.delete(bno);
	}

}
