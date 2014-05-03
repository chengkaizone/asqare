package asqare.cheng.board.impl;

import java.util.ArrayList;
import java.util.List;

import asqare.cheng.board.Board;
import asqare.cheng.model.Piece;
import asqare.cheng.service.GameConf;

/**
 * 垂直摆放方块模板
 * 
 * @author ganchengkai
 * 
 */
public class VerticalBoard extends Board {
	protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {
		List<Piece> notNullPieces = new ArrayList<Piece>();
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[i].length; j++) {
				if (i % 2 == 0) {
					Piece piece = new Piece(i, j);
					notNullPieces.add(piece);
				}
			}
		}
		return notNullPieces;
	}
}
