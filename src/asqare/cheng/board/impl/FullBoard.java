package asqare.cheng.board.impl;

import java.util.ArrayList;
import java.util.List;

import asqare.cheng.board.Board;
import asqare.cheng.model.Piece;
import asqare.cheng.service.GameConf;

/**
 * 实体填充方块模板
 * 
 * @author ganchengkai
 * 
 */
public class FullBoard extends Board {
	@Override
	protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {
		List<Piece> notNullPieces = new ArrayList<Piece>();
		for (int i = 1; i < pieces.length - 1; i++) {
			for (int j = 1; j < pieces[i].length - 1; j++) {
				Piece piece = new Piece(i, j);
				notNullPieces.add(piece);
			}
		}
		return notNullPieces;
	}
}
