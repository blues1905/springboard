package board.dao;

import java.util.List;

import board.domain.BoardVO;
import board.domain.Criteria;

public interface BoardDao {
	public abstract List<BoardVO> list(Criteria cri) throws Exception;
	
	public abstract int listCount() throws Exception;
	
	public abstract int delete(BoardVO boardVO);
	
	public abstract int deleteAll();
	
	public abstract int update(BoardVO boardVO);
	
	public abstract void insert(BoardVO boardVO);
	
	public abstract BoardVO select(int seq);
	
	public abstract int updateReadCount(int seq);
}
