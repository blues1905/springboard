package board.service;

import java.util.List;

import board.domain.BoardVO;
import board.domain.Criteria;

public interface BoardService {
	public abstract List<BoardVO> list(Criteria cri) throws Exception;
	
	public abstract int listCount() throws Exception;
	
	public abstract int delete(BoardVO boardVO);
	
	public abstract int edit(BoardVO boardVO);
	
	public abstract void write(BoardVO boardVO);
	
	public abstract BoardVO read(int seq);
}
