package com.board.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.board.domain.BoardVO;
import com.board.service.BoardService;

@Controller
@RequestMapping("/board/")
public class boardController {
	
	@Inject
	BoardService service;

	// 게시물 목록
	@RequestMapping(value="/list", method=RequestMethod.GET)
	public void getList(Model model) throws Exception{
		List<BoardVO> list = null;
		list = service.list();
		
		model.addAttribute("list",list);
	}
	
	// 게시물 작성 GET
	@RequestMapping(value="/write", method=RequestMethod.GET)
	public void getWrite() throws Exception{
		
	}
	
	// 게시물 작성 POST
	@RequestMapping(value="/write", method=RequestMethod.POST)
	public String postWrite(BoardVO vo) throws Exception{
		service.write(vo);
		return "redirect:/board/list";
	}
	
	// 게시물 조회
	@RequestMapping(value="/view", method=RequestMethod.GET)
	public void getView(@RequestParam("bno") int bno, Model model) throws Exception{
		BoardVO vo = service.view(bno);
		model.addAttribute("view",vo);
	}
	
	// 게시물 수정 get으로 호출시 데이터 불러옴
	@RequestMapping(value="/modify", method=RequestMethod.GET)
	public void getModify(@RequestParam("bno") int bno, Model model) throws Exception{
		BoardVO vo = service.view(bno);
		model.addAttribute("view", vo);
		
	}
	
	// 게시물 수정 post으로 호출시 데이터 update
	@RequestMapping(value="/modify", method=RequestMethod.POST)
	public String postModify(BoardVO vo) throws Exception{
		service.modify(vo);
		return "redirect:/board/view?bno="+vo.getBno();
	}
	
	// 게시물 삭제
	@RequestMapping(value="/delete", method=RequestMethod.GET)
	public String getDelete(@RequestParam("bno") int bno) throws Exception {
		service.delete(bno);
		return "redirect:/board/list";

	}
	
	// 게시물 목록 service.list(); + 페이징 추가
	@RequestMapping(value="/listPage", method=RequestMethod.GET)
	public void getListPage(Model model, @RequestParam("num") int num) throws Exception{
		// 매개변수 num: 페이지 목록 번호
		// 게시물 총 개수
		int count = service.count();
		
		// 한 페이지에 출력할 게시물 개수
		int postNum = 10;// 고정 값
		
		// 하단 페이지 번호를 나열 (게시물 총 개수 /한페이지 출력 개수)의 올림
		int pageNum = (int)Math.ceil((double)count/postNum);
		
		/*
		 * 출력할 게시물 번호
		 * 1페이지 -> limit 0, 10 
		 * 2페이지 -> limit 10, 10
		 * 3페이지 -> limit 20, 10
		 */
		int displayPost = (num-1)*postNum;


		List<BoardVO> list = null;
		list = service.pageList(displayPost, postNum);// 게시물 목록 데이터
		model.addAttribute("list", list); 
		model.addAttribute("pageNum", pageNum);// 하단 페이지 수 나열
	}
	
}
