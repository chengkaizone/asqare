package asqare.cheng.board;

import java.util.List;

import asqare.cheng.model.Piece;
import asqare.cheng.model.PieceImage;
import asqare.cheng.service.GameConf;
import asqare.cheng.service.ImageService;

/**
 * ������Ϸ�ڷŵĻ���---��Ϸ�Ѷ���������չ
 * 
 * @author ganchengkai
 * 
 */
public abstract class Board {
	/**
	 * ������Ϸ�������---������ʵ��---��չ�Ѷ�
	 * 
	 * @param config
	 * @param pieces
	 * @return �������е�
	 */
	protected abstract List<Piece> createPieces(GameConf config,
			Piece[][] pieces);

	/**
	 * ������Ϸ�еķ���
	 * 
	 * @param config
	 * @return
	 */
	public Piece[][] create(GameConf config) {
		Piece[][] pieces = new Piece[config.getXSize()][config.getYSize()];
		List<Piece> notNullPieces = createPieces(config, pieces);
		List<PieceImage> playImages = ImageService.getPlayImages(
				config.getContext(), notNullPieces.size());
		int imageWidth = playImages.get(0).getImage().getWidth();
		int imageHeight = playImages.get(0).getImage().getHeight();
		for (int i = 0; i < notNullPieces.size(); i++) {
			Piece piece = notNullPieces.get(i);
			piece.setImage(playImages.get(i));
			piece.setBeginX(piece.getIndexX() * imageWidth
					+ config.getBeginImageX());
			piece.setBeginY(piece.getIndexY() * imageHeight
					+ config.getBeginImageY());
			pieces[piece.getIndexX()][piece.getIndexY()] = piece;
		}
		return pieces;
	}
}
