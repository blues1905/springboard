package board.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;

import board.domain.BoardVO;
import board.domain.Criteria;
import board.domain.PageMaker;
import board.service.BoardService;

@Controller
@SessionAttributes("boardVO")
public class BoardController {
	private BoardService boardService;
	
	public void setBoardService(BoardService boardService) {
		this.boardService = boardService;
	}
	
	@RequestMapping(value="/board/list", method = RequestMethod.GET) 
	public String list(Model model, Criteria cri) throws Exception{
		
		model.addAttribute("boardList", boardService.list(cri));
		
		PageMaker pageMaker = new PageMaker();
		pageMaker.setCri(cri);
		pageMaker.setTotalCount(boardService.listCount());
		
		model.addAttribute("pageMaker", pageMaker);
		
		return "/board/list";
	}
	
	@RequestMapping(value="/board/read/{seq}")
	public String read(Model model, @PathVariable int seq) {
		model.addAttribute("boardVO", boardService.read(seq));

		return "/board/read";  
	}
	//파일 다운
	@RequestMapping(value="/fileDown/{seq}")
	public void fileDown(@PathVariable int seq,HttpServletRequest req, HttpServletResponse resp) throws IOException{
		//업로드 파일이 있는 경로
		String realPath = "C:\\upload\\";
		BoardVO vo = boardService.read(seq);
		String filename = vo.getFileName();
		
		File downFile = new File(realPath + "\\" + filename);
		//파일 이름이 파라미터로 넘어오지 않으면 리다이렉트 시킨다.
//		if(request.getParameter("fileName") == null || "".equals(request.getParameter("fileName"))) {
		if (downFile.exists() && downFile.isFile()) {
	         try {
	            filename = URLEncoder.encode(filename, "utf-8").replaceAll("\\+","%20");
	            long filesize = downFile.length();
	            
	            resp.setContentType("application/octet-stream; charset=utf-8");
	            resp.setContentLength((int) filesize);
	            String strClient = req.getHeader("user-agent");
	            
	            if (strClient.indexOf("MSIE 5.5") != -1) {
	               resp.setHeader("Content-Disposition", "filename="
	                            + filename + ";");
	                } else {
	                   resp.setHeader("Content-Disposition",
	                            "attachment; filename=" + filename + ";");
	                }
	            resp.setHeader("Content-Length", String.valueOf(filesize));
	            resp.setHeader("Content-Transfer-Encoding", "binary;");
	            resp.setHeader("Pragma", "no-cache");
	            resp.setHeader("Cache-Control", "private");
	 
	                byte b[] = new byte[1024];
	 
	                BufferedInputStream in = new BufferedInputStream(
	                        new FileInputStream(downFile));
	 
	                BufferedOutputStream out = new BufferedOutputStream(
	                      resp.getOutputStream());
	 
	                int read = 0;
	 
	                while ((read = in.read(b)) != -1) {
	                    out.write(b, 0, read);
	                }
	                out.flush();
	                out.close();
	                in.close();
	            
	         } catch (Exception e) {
	            System.out.println("Download Exception : " + e.getMessage());
	         }
	      } else {
	         System.out.println("Download Error : downFile Error [" + downFile + "]");
	      }
	}
	
	//새 글 작성을 위한 요청을 처리
	@RequestMapping(value="/board/write", method=RequestMethod.GET)
	public String write(Model model){
		
		model.addAttribute("boardVO", new BoardVO());
		return "/board/write";
	}
	
	//새 글 등록을 위한 요청을 처리
	@RequestMapping(value="/board/write", method=RequestMethod.POST)
	public String write(@Valid BoardVO boardVO, BindingResult bindingResult) throws IOException {
		String fileName = null;
		MultipartFile uploadFile = boardVO.getUploadFile();
		//파일업로드 처리
		if(bindingResult.hasErrors()) {
			return "/board/write";
		}
		
		if(!uploadFile.isEmpty()) {
			String originalFileName = uploadFile.getOriginalFilename();
			String ext = FilenameUtils.getExtension(originalFileName);
			//확장자 구하기
//			UUID uuid = UUID.randomUUID();	//UUID 구하기
//			fileName = uuid + "." + ext;	//fileName 랜덤
			fileName = uploadFile.getOriginalFilename();
			uploadFile.transferTo(new File("C:\\upload\\" + fileName));
		}
		boardVO.setFileName(fileName);
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
			@Valid @ModelAttribute BoardVO boardVO,
			BindingResult result,
			int pwd,
			SessionStatus sessionStatus,
			Model model) {
		if(result.hasErrors()) {
			return "/board/edit";
		}
		else {
			if(boardVO.getPassword() == pwd) {
				boardService.edit(boardVO);
				sessionStatus.setComplete();
				return "redirect:/board/list";
			}
		}
		model.addAttribute("msg", "비밀번호가 일치하지 않습니다.");
		return "/board/edit";
	}
	
	//글 삭제 요청을 처리할 메서드
	@RequestMapping(value="/board/delete/{seq}", method=RequestMethod.GET)
	public String delete(@PathVariable int seq, Model model) {
		model.addAttribute("seq", seq);
		return "/board/delete";
	}
	@RequestMapping(value="/board/delete", method=RequestMethod.POST)
	public String delete(int seq, int pwd, Model model) {
		int rowCount;
		BoardVO boardVO = new BoardVO();
		boardVO.setSeq(seq);
		boardVO.setPassword(pwd);
		
		rowCount = boardService.delete(boardVO);
		
		if(rowCount == 0) {
			model.addAttribute("seq", seq);
			model.addAttribute("msg", "비밀번호가 일치하지 않습니다.");
			return "/board/delete";
		}
		else {
			return "redirect:/board/list";
		}
	}
}
