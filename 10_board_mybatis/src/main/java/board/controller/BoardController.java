package board.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import board.domain.BoardVO;
import board.service.BoardService;

@Controller
public class BoardController {
	private BoardService boardService;
	
	public void setBoardService(BoardService boardService) {
		this.boardService = boardService;
	}
	
	@RequestMapping(value="/board/list")
	public String list(Model model) {
		model.addAttribute("boardList", boardService.list());
		return "/board/list";
	}
	@RequestMapping(value="board/read/{seq}")
	public String read(Model model, @PathVariable int seq) {
		model.addAttribute("boardVO", boardService.read(seq));
		return "/board/read";
	}
	//새 글 작성을 위한 요청을 처리
	@RequestMapping(value="/board/write", method=RequestMethod.GET)
	public String write(Model model){
		model.addAttribute("boardVO", new BoardVO());
		return "/board/write";
	}
	
	//새 글 등록을 위한 요청을 처리
	@RequestMapping(value="/board/write", method=RequestMethod.POST)
	public String write(@Valid BoardVO boardVO, BindingResult bindingResult) {
		if(bindingResult.hasErrors()) {
			return "/board/write";
		}
		boardService.write(boardVO);
		return "redirect:/board/list";
	}
	
	//글 수정 기능
	@RequestMapping(value="/board/edit/{seq}", method=RequestMethod.GET)
	public String edit(@PathVariable int seq, Model model) {
		BoardVO boardVO = boardService.read(seq);
		model.addAttribute("boardVO", boardVO);
		return "/board/edit";
	}
	
	@RequestMapping(value="/board/edit/{seq}", method=RequestMethod.POST)
	public String edit(
			@Valid BoardVO boardVO,
			BindingResult result,
			int pwd,
			Model model) {
		if(result.hasErrors()) {
			return "/board/edit";
		}
		else {
			if(boardVO.getPassword() == pwd) {
				boardService.edit(boardVO);
				return "redirect:/board/list";
			}
		}
		model.addAttribute("msg", "비밀번호가 일치하지 않습니다.");
		return "/board/edit";
	}
}