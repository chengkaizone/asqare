package asqare.cheng.board.impl;

import java.util.ArrayList;
import java.util.List;

import asqare.cheng.board.Board;
import asqare.cheng.model.Piece;
import asqare.cheng.service.GameConf;

/**
 * ˮƽ����ڷŷ���ģ��
 * 
 * @author ganchengkai
 * 
 */
public class HorizontalBoard extends Board {
	protected List<Piece> createPieces(GameConf config, Piece[][] pieces) {
		List<Piece> notNullPieces = new ArrayList<Piece>();
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[i].length; j++) {
				if (j % 2 == 0) {
					Piece piece = new Piece(i, j);
					notNullPieces.add(piece);
				}
			}
		}
		return notNullPieces;
	}
}
