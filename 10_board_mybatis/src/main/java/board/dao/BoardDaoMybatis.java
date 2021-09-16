package board.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import board.domain.BoardVO;
import board.domain.Criteria;

@Repository
public class BoardDaoMybatis implements BoardDao{
	
	private SqlSessionTemplate sqlSessionTemplate;
	
	public BoardDaoMybatis(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}
	
	public void setSqlMapClient(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}
	
	@Override
	public List<BoardVO> list(Criteria cri) throws Exception {
		return sqlSessionTemplate.selectList("listPage", cri);
	}
	
	@Override
	public int listCount() throws Exception {
		return sqlSessionTemplate.selectOne("listCount");
	}
	
	@Override
	public int delete(BoardVO boardVO) {
		return sqlSessionTemplate.delete("delete", boardVO);
	}
	@Override
	public int deleteAll() {
		return sqlSessionTemplate.delete("deleteAll");
	}
	@Override
	public int update(BoardVO boardVO) {
		return sqlSessionTemplate.update("update", boardVO);
	}
	@Override
	public void insert(BoardVO boardVO) {
		sqlSessionTemplate.insert("insert", boardVO);
	}
	@Override
	public BoardVO select(int seq) {
		BoardVO vo = (BoardVO) sqlSessionTemplate.selectOne("select", seq);
		return vo;
	}
	@Override
	public int updateReadCount(int seq) {
		return sqlSessionTemplate.update("updateCount", seq);
	}
}
