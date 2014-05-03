package asqare.cheng.widget;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import asqare.cheng.model.Piece;
import asqare.cheng.service.GameService;
import asqare.cheng.service.ImageService;
import asqare.cheng.service.LinkInfo;

/**
 * ��Ϸ��Ⱦ��
 * 
 * @author ganchengkai
 * 
 */
public class GameView extends View {
	private GameService gameService;
	// ��ǰ�Ѿ���ѡ�еķ���
	private Piece selectedPiece;
	// ���������������Ϣ
	private LinkInfo linkInfo;
	private Paint paint;
	// ѡ�б�ʶ��ͼƬ����
	private Bitmap selectImage;

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.paint = new Paint();
		this.paint.setColor(Color.RED);
		this.paint.setStrokeWidth(3);
		this.selectImage = ImageService.getSelectImage(context);
	}

	public void setLinkInfo(LinkInfo linkInfo) {
		this.linkInfo = linkInfo;
	}

	public void setGameService(GameService gameService) {
		this.gameService = gameService;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (this.gameService == null)
			return;
		Piece[][] pieces = gameService.getPieces();
		if (pieces != null) {
			// ����pieces��ά����
			for (int i = 0; i < pieces.length; i++) {
				for (int j = 0; j < pieces[i].length; j++) {
					// �����ά�����и�Ԫ�ز�Ϊ�գ����з��飩������������ͼƬ������
					if (pieces[i][j] != null) {
						// �õ����Piece����
						Piece piece = pieces[i][j];
						// ���ݷ������Ͻ�X��Y������Ʒ���
						canvas.drawBitmap(piece.getImage().getImage(),
								piece.getBeginX(), piece.getBeginY(), null);
					}
				}
			}
		}
		// �����ǰ��������linkInfo����, ��������Ϣ
		if (this.linkInfo != null) {
			// ����������
			drawLine(this.linkInfo, canvas);
			// ����������linkInfo����
			this.linkInfo = null;
		}
		// ��ѡ�б�ʶ��ͼƬ
		if (this.selectedPiece != null) {
			canvas.drawBitmap(this.selectImage, this.selectedPiece.getBeginX(),
					this.selectedPiece.getBeginY(), null);
		}
	}

	// ����LinkInfo���������ߵķ�����---������Ļ�ϵĵ��������
	private void drawLine(LinkInfo linkInfo, Canvas canvas) {
		List<Point> points = linkInfo.getLinkPoints();
		for (int i = 0; i < points.size() - 1; i++) {
			Point currentPoint = points.get(i);
			Point nextPoint = points.get(i + 1);
			canvas.drawLine(currentPoint.x, currentPoint.y, nextPoint.x,
					nextPoint.y, this.paint);
		}
	}

	/**
	 * ���õ�ǰѡ�еķ���
	 * 
	 * @param piece
	 */
	public void setSelectedPiece(Piece piece) {
		this.selectedPiece = piece;
	}

	/**
	 * ��ʼ��Ϸ
	 */
	public void startGame() {
		this.gameService.start();
		// ֪ͨ�ػ�
		this.postInvalidate();
	}
}
