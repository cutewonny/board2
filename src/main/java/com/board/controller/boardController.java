package com.board.controller;

import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.board.domain.BoardVO;
import com.board.domain.Page;
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
		
		Page page = new Page();
		
		page.setNum(num);
		page.setCount(service.count());// <게시물> 총 개수
		
		List<BoardVO> list = null;
		list = service.pageList(page.getDisplayPost(), page.getPostNum_defalut());// 게시물 목록 데이터
		
		
		model.addAttribute("list", list); // 게시물 목록 데이터
		
		/*
		model.addAttribute("pageNum", page.getPageNum());// 하단 페이지 수 나열
		model.addAttribute("startPageNum", page.getStartPageNum());// 선택한 페이지의 처음 장
		model.addAttribute("endPageNum", page.getEndPageNum());// 선택한 페이지의 마지막 장
		// 페이징 이동
		model.addAttribute("prev", page.isPrev());
		model.addAttribute("next", page.isNext());
		*/
		model.addAttribute("page", page);
		model.addAttribute("select", num);//현재 페이지
		
	}
	
	@RequestMapping(value="/listPage2", method=RequestMethod.GET)
	public void getListPage2(Model model, @RequestParam("num") int num) throws Exception{
		// 매개변수 num: 페이지 목록 번호

		// <게시물> 총 개수
		int count = service.count();
		
		// <게시물> 한 페이지에 출력할 게시물 개수
		int postNum_defalut = 10;//고정 값
		
		/*
		 * <페이징 + DB> pageNum: 하단 페이지 총 번호 (게시물 총 개수 / 한 페이지 출력 할 개수)의 올림
		 * count=611개의 게시물이 있다면 -> pageNum: 62장
		 */
		int pageNum = (int)Math.ceil((double)count/postNum_defalut);//62장
		
		/*
		 * <게시물> displayPost: 출력할 게시물
		 * num: 현재 1페이지 -> limit 0, 10 
		 * num: 현재 2페이지 -> limit 10, 10
		 * num: 현재 3페이지 -> limit 20, 10
		 */
		int displayPost = (num-1)*postNum_defalut;
		
		// <페이징> 한번에 표시할 페이징 번호의 개수
		int pageNum_defalut = 10;//고정 값
		
		/*
		 * <페이징> endPageNum : 표시되는 페이지 번호 중 마지막 번호 : 10단위로 끝난다.
		 *  num: 현재 1페이지 -> (1)*10 => 마지막번호 10
		 *  num: 현재 2페이지 -> (1)*10 => 마지막번호 10
		 *  num: 현재 10페이지 -> (1)*10 => 마지막번호 10
		 *  num: 현재 11페이지 -> (2)*10 => 마지막번호 20
		 *  num: 현재 19페이지 -> (2)*10 => 마지막번호 20
		 */
		int endPageNum = (int)(Math.ceil((double)num/(double)pageNum_defalut)*pageNum_defalut);
		
		/*
		 * <페이징> startPageNum: 표시되는 페이지 번호 중 첫번째 번호
		 * 마지막번호 10 -> 10-(9) => 1 페이지 시작
		 * 마지막 번호 20 -> 20-(9) => 11 페이지 시작
		 * 마지막 번호 30 -> 30-(9) => 21 페이지 시작
		 */
		int startPageNum = endPageNum - (pageNum_defalut-1);
		
		/*
		 * <페이징> 마지막 번호 재계산
		 *   count=611개의 게시물이 있다면 -> pageNum: 62장
		 *   위에서 endPageNum: 62장을 눌렀는데 70장까지 표시됨 -> 63~70장은 필요없음
		 *   70 > 62
		 */
		if(endPageNum>pageNum) {
			endPageNum=pageNum;
		}
		
		/*
		 * <페이징 이동> 1페이지:이전 못눌러
		 */
		boolean prev = startPageNum == 1 ? false:true;
		
		/*
		 * <페이징 이동> 마지막 페이지: 다음 못눌러
		 * 현재 페이지 num: 62장일때 >= pageNum: 62장 -> 다음 없어
		 */
		boolean next = num>=pageNum ? false:true;
		
		
		List<BoardVO> list = null;
		list = service.pageList(displayPost, postNum_defalut);// 게시물 목록 데이터
		
		
		model.addAttribute("list", list); // 게시물 목록 데이터
		
		model.addAttribute("pageNum", pageNum);// 하단 페이지 수 나열
		model.addAttribute("startPageNum", startPageNum);// 선택한 페이지의 처음 장
		model.addAttribute("endPageNum", endPageNum);// 선택한 페이지의 마지막 장
		// 페이징 이동
		model.addAttribute("prev", prev);
		model.addAttribute("next", next);
		model.addAttribute("select", num);//현재 페이지
		
	}
	
	// 게시물 목록 service.list(); + 페이징 추가
	@RequestMapping(value="/listPageSearch", method=RequestMethod.GET)
	public void getListPageSearch(Model model, @RequestParam("num") int num, @RequestParam(value="searchType", required=false, defaultValue="title")String searchType, @RequestParam(value="keyword", required=false, defaultValue="")String keyword) throws Exception{
		Page page = new Page();
		
		page.setNum(num);
		page.setCount(service.searchCount(searchType, keyword));// <게시물> 총 개수 // 다른 변수 설정 함수 실행
		
		//검색 타입과 검색어
		//page.setSearchTypeKeyword(searchType, keyword);//searchType,keyword를 set 하는 함수
		page.setSearchType(searchType);
		page.setKeyword(keyword);
		
		List<BoardVO> list = null;
		list = service.pageListSearch(page.getDisplayPost(), page.getPostNum_defalut(), searchType, keyword);// 게시물 목록 데이터
		
		
		model.addAttribute("list", list); // 게시물 목록 데이터
		
		model.addAttribute("page", page);
		model.addAttribute("select", num);//현재 페이지
		
		//model.addAttribute("searchType", searchType);
		//model.addAttribute("keyword", keyword);
		
	}
	
}
