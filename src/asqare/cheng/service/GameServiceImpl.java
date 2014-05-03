package asqare.cheng.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.graphics.Point;
import asqare.cheng.board.Board;
import asqare.cheng.board.impl.FullBoard;
import asqare.cheng.board.impl.HorizontalBoard;
import asqare.cheng.board.impl.VerticalBoard;
import asqare.cheng.model.Piece;

/**
 * ��Ϸ�߼��ľ���ʵ��
 * 
 * @author ganchengkai
 * 
 */
public class GameServiceImpl implements GameService {
	// ��������
	private Piece[][] pieces;
	private GameConf config;

	public GameServiceImpl(GameConf config) {
		// ������Ϸ���ô�������
		this.config = config;
	}

	@Override
	public void start() {
		Board board = null;
		Random random = new Random();
		int index = random.nextInt(3);
		switch (index) {
		case 0:
			board = new VerticalBoard();
			break;
		case 1:
			board = new HorizontalBoard();
			break;
		default:
			board = new FullBoard();
		}
		this.pieces = board.create(config);
	}

	@Override
	public Piece[][] getPieces() {
		return this.pieces;
	}

	@Override
	public boolean hasPieces() {
		for (int i = 0; i < pieces.length; i++) {
			for (int j = 0; j < pieces[i].length; j++) {
				if (pieces[i][j] != null) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public Piece findPiece(float touchX, float touchY) {
		int relativeX = (int) touchX - this.config.getBeginImageX();
		int relativeY = (int) touchY - this.config.getBeginImageY();
		if (relativeX < 0 || relativeY < 0) {
			return null;
		}
		int indexX = getIndex(relativeX, GameConf.PIECE_WIDTH);
		// ��ȡrelativeY������Piece[][]�����еĵڶ�ά������ֵ
		int indexY = getIndex(relativeY, GameConf.PIECE_HEIGHT);
		// �����߽緵��null
		if (indexX < 0 || indexY < 0) {
			return null;
		}
		if (indexX >= this.config.getXSize()
				|| indexY >= this.config.getYSize()) {
			return null;
		}
		return this.pieces[indexX][indexY];
	}

	private int getIndex(int relative, int size) {
		int index = -1;
		if (relative % size == 0) {
			index = relative / size - 1;
		} else {
			index = relative / size;
		}
		return index;
	}

	@Override
	public LinkInfo link(Piece p1, Piece p2) {
		if (p1.equals(p2))
			return null;
		if (!p1.isSameImage(p2))
			return null;
		if (p2.getIndexX() < p1.getIndexX())
			return link(p2, p1);
		Point p1Point = p1.getCenter();
		Point p2Point = p2.getCenter();
		if (p1.getIndexY() == p2.getIndexY()) {
			if (!isXBlock(p1Point, p2Point, GameConf.PIECE_WIDTH)) {
				return new LinkInfo(p1Point, p2Point);
			}
		}
		// �������Piece��ͬһ��
		if (p1.getIndexX() == p2.getIndexX()) {
			if (!isYBlock(p1Point, p2Point, GameConf.PIECE_HEIGHT)) {
				// ����֮��û������ϰ�, û��ת�۵�
				return new LinkInfo(p1Point, p2Point);
			}
		}
		// ��һ��ת�۵�����
		Point cornerPoint = getCornerPoint(p1Point, p2Point,
				GameConf.PIECE_WIDTH, GameConf.PIECE_HEIGHT);
		if (cornerPoint != null) {
			return new LinkInfo(p1Point, cornerPoint, p2Point);
		}
		// ������ת�۵�����
		Map<Point, Point> turns = getLinkPoints(p1Point, p2Point,
				GameConf.PIECE_WIDTH, GameConf.PIECE_WIDTH);
		if (turns.size() != 0) {
			return getShortcut(p1Point, p2Point, turns,
					getDistance(p1Point, p2Point));
		}
		return null;
	}

	/**
	 * ��ȡ����ת�۵�����
	 * 
	 * @param point1
	 * @param point2
	 * @return ͨ������һЩ�����ص�ļ���
	 */
	private Map<Point, Point> getLinkPoints(Point point1, Point point2,
			int pieceWidth, int pieceHeight) {
		Map<Point, Point> result = new HashMap<Point, Point>();
		List<Point> p1UpChannel = getUpChannel(point1, point2.y, pieceHeight);
		List<Point> p1RightChannel = getRightChannel(point1, point2.x,
				pieceWidth);
		List<Point> p1DownChannel = getDownChannel(point1, point2.y,
				pieceHeight);
		List<Point> p2DownChannel = getDownChannel(point2, point1.y,
				pieceHeight);
		List<Point> p2LeftChannel = getLeftChannel(point2, point1.x, pieceWidth);
		List<Point> p2UpChannel = getUpChannel(point2, point1.y, pieceHeight);
		int heightMax = (this.config.getYSize() + 1) * pieceHeight
				+ this.config.getBeginImageY();
		int widthMax = (this.config.getXSize() + 1) * pieceWidth
				+ this.config.getBeginImageX();
		if (isLeftUp(point1, point2) || isLeftDown(point1, point2)) {
			return getLinkPoints(point2, point1, pieceWidth, pieceHeight);
		}
		if (point1.y == point2.y) {
			p1UpChannel = getUpChannel(point1, 0, pieceHeight);
			p2UpChannel = getUpChannel(point2, 0, pieceHeight);
			Map<Point, Point> upLinkPoints = getXLinkPoints(p1UpChannel,
					p2UpChannel, pieceHeight);
			p1DownChannel = getDownChannel(point1, heightMax, pieceHeight);
			p2DownChannel = getDownChannel(point2, heightMax, pieceHeight);
			Map<Point, Point> downLinkPoints = getXLinkPoints(p1DownChannel,
					p2DownChannel, pieceHeight);
			result.putAll(upLinkPoints);
			result.putAll(downLinkPoints);
		}
		if (point1.x == point2.x) {
			List<Point> p1LeftChannel = getLeftChannel(point1, 0, pieceWidth);
			p2LeftChannel = getLeftChannel(point2, 0, pieceWidth);
			Map<Point, Point> leftLinkPoints = getYLinkPoints(p1LeftChannel,
					p2LeftChannel, pieceWidth);
			p1RightChannel = getRightChannel(point1, widthMax, pieceWidth);
			List<Point> p2RightChannel = getRightChannel(point2, widthMax,
					pieceWidth);
			Map<Point, Point> rightLinkPoints = getYLinkPoints(p1RightChannel,
					p2RightChannel, pieceWidth);
			result.putAll(leftLinkPoints);
			result.putAll(rightLinkPoints);
		}
		if (isRightUp(point1, point2)) {
			Map<Point, Point> upDownLinkPoints = getXLinkPoints(p1UpChannel,
					p2DownChannel, pieceWidth);
			Map<Point, Point> rightLeftLinkPoints = getYLinkPoints(
					p1RightChannel, p2LeftChannel, pieceHeight);
			p1UpChannel = getUpChannel(point1, 0, pieceHeight);
			p2UpChannel = getUpChannel(point2, 0, pieceHeight);
			Map<Point, Point> upUpLinkPoints = getXLinkPoints(p1UpChannel,
					p2UpChannel, pieceWidth);
			p1DownChannel = getDownChannel(point1, heightMax, pieceHeight);
			p2DownChannel = getDownChannel(point2, heightMax, pieceHeight);
			Map<Point, Point> downDownLinkPoints = getXLinkPoints(
					p1DownChannel, p2DownChannel, pieceWidth);
			p1RightChannel = getRightChannel(point1, widthMax, pieceWidth);
			List<Point> p2RightChannel = getRightChannel(point2, widthMax,
					pieceWidth);
			Map<Point, Point> rightRightLinkPoints = getYLinkPoints(
					p1RightChannel, p2RightChannel, pieceHeight);
			List<Point> p1LeftChannel = getLeftChannel(point1, 0, pieceWidth);
			p2LeftChannel = getLeftChannel(point2, 0, pieceWidth);
			Map<Point, Point> leftLeftLinkPoints = getYLinkPoints(
					p1LeftChannel, p2LeftChannel, pieceHeight);
			result.putAll(upDownLinkPoints);
			result.putAll(rightLeftLinkPoints);
			result.putAll(upUpLinkPoints);
			result.putAll(downDownLinkPoints);
			result.putAll(rightRightLinkPoints);
			result.putAll(leftLeftLinkPoints);
		}
		if (isRightDown(point1, point2)) {
			Map<Point, Point> downUpLinkPoints = getXLinkPoints(p1DownChannel,
					p2UpChannel, pieceWidth);
			Map<Point, Point> rightLeftLinkPoints = getYLinkPoints(
					p1RightChannel, p2LeftChannel, pieceHeight);
			p1UpChannel = getUpChannel(point1, 0, pieceHeight);
			p2UpChannel = getUpChannel(point2, 0, pieceHeight);
			Map<Point, Point> upUpLinkPoints = getXLinkPoints(p1UpChannel,
					p2UpChannel, pieceWidth);
			p1DownChannel = getDownChannel(point1, heightMax, pieceHeight);
			p2DownChannel = getDownChannel(point2, heightMax, pieceHeight);
			Map<Point, Point> downDownLinkPoints = getXLinkPoints(
					p1DownChannel, p2DownChannel, pieceWidth);
			List<Point> p1LeftChannel = getLeftChannel(point1, 0, pieceWidth);
			p2LeftChannel = getLeftChannel(point2, 0, pieceWidth);
			Map<Point, Point> leftLeftLinkPoints = getYLinkPoints(
					p1LeftChannel, p2LeftChannel, pieceHeight);
			p1RightChannel = getRightChannel(point1, widthMax, pieceWidth);
			List<Point> p2RightChannel = getRightChannel(point2, widthMax,
					pieceWidth);
			Map<Point, Point> rightRightLinkPoints = getYLinkPoints(
					p1RightChannel, p2RightChannel, pieceHeight);
			result.putAll(downUpLinkPoints);
			result.putAll(rightLeftLinkPoints);
			result.putAll(upUpLinkPoints);
			result.putAll(downDownLinkPoints);
			result.putAll(leftLeftLinkPoints);
			result.putAll(rightRightLinkPoints);
		}
		return result;
	}

	/**
	 * ��ȡp1��p2֮����̵�������Ϣ
	 * 
	 * @param p1
	 * @param p2
	 * @param turns
	 *            ��ת�۵��map
	 * @param shortDistance
	 *            ����֮�����̾���
	 * @return p1��p2֮����̵�������Ϣ
	 */
	private LinkInfo getShortcut(Point p1, Point p2, Map<Point, Point> turns,
			int shortDistance) {
		List<LinkInfo> infos = new ArrayList<LinkInfo>();
		for (Point point1 : turns.keySet()) {
			Point point2 = turns.get(point1);
			infos.add(new LinkInfo(p1, point1, point2, p2));
		}
		return getShortcut(infos, shortDistance);
	}

	/**
	 * ��infos�л�ȡ��������̵��Ǹ�LinkInfo����
	 * 
	 * @param infos
	 * @return ��������̵��Ǹ�LinkInfo����
	 */
	private LinkInfo getShortcut(List<LinkInfo> infos, int shortDistance) {
		int temp1 = 0;
		LinkInfo result = null;
		for (int i = 0; i < infos.size(); i++) {
			LinkInfo info = infos.get(i);
			int distance = countAll(info.getLinkPoints());
			if (i == 0) {
				temp1 = distance - shortDistance;
				result = info;
			}
			if (distance - shortDistance < temp1) {
				temp1 = distance - shortDistance;
				result = info;
			}
		}
		return result;
	}

	/**
	 * ����List<Point>�����е�ľ����ܺ�
	 * 
	 * @param points
	 *            ��Ҫ��������ӵ�
	 * @return ���е�ľ�����ܺ�
	 */
	private int countAll(List<Point> points) {
		int result = 0;
		for (int i = 0; i < points.size() - 1; i++) {
			Point point1 = points.get(i);
			Point point2 = points.get(i + 1);
			result += getDistance(point1, point2);
		}
		return result;
	}

	/**
	 * ��ȡ����LinkPoint֮�����̾���
	 * 
	 * @param p1
	 *            ��һ����
	 * @param p2
	 *            �ڶ�����
	 * @return ������ľ�������ܺ�
	 */
	private int getDistance(Point p1, Point p2) {
		int xDistance = Math.abs(p1.x - p2.x);
		int yDistance = Math.abs(p1.y - p2.y);
		return xDistance + yDistance;
	}

	/**
	 * ������������, ���жϵ�һ�����ϵ�Ԫ�ص�x��������һ�������е�Ԫ��x������ͬ(����), �����ͬ, ����ͬһ��,
	 * ���ж��Ƿ����ϰ�,û����ӵ������Map��ȥ
	 * 
	 * @param p1Channel
	 * @param p2Channel
	 * @param pieceHeight
	 * @return
	 */
	private Map<Point, Point> getYLinkPoints(List<Point> p1Channel,
			List<Point> p2Channel, int pieceHeight) {
		Map<Point, Point> result = new HashMap<Point, Point>();
		for (int i = 0; i < p1Channel.size(); i++) {
			Point temp1 = p1Channel.get(i);
			for (int j = 0; j < p2Channel.size(); j++) {
				Point temp2 = p2Channel.get(j);
				if (temp1.x == temp2.x) {
					if (!isYBlock(temp1, temp2, pieceHeight)) {
						result.put(temp1, temp2);
					}
				}
			}
		}
		return result;
	}

	/**
	 * ������������, ���жϵ�һ�����ϵ�Ԫ�ص�y��������һ�������е�Ԫ��y������ͬ(����), �����ͬ, ����ͬһ��, ���ж��Ƿ����ϰ�,
	 * û����ӵ������map��ȥ
	 * 
	 * @param p1Channel
	 * @param p2Channel
	 * @param pieceWidth
	 * @return ��ſ��Ժ���ֱ�����ӵ����ӵ�ļ�ֵ��
	 */
	private Map<Point, Point> getXLinkPoints(List<Point> p1Channel,
			List<Point> p2Channel, int pieceWidth) {
		Map<Point, Point> result = new HashMap<Point, Point>();
		for (int i = 0; i < p1Channel.size(); i++) {
			Point temp1 = p1Channel.get(i);
			for (int j = 0; j < p2Channel.size(); j++) {
				Point temp2 = p2Channel.get(j);
				if (temp1.y == temp2.y) {
					if (!isXBlock(temp1, temp2, pieceWidth)) {
						result.put(temp1, temp2);
					}
				}
			}
		}
		return result;
	}

	/**
	 * �ж�point2�Ƿ���point1�����Ͻ�
	 * 
	 * @param point1
	 * @param point2
	 * @return p2λ��p1�����Ͻ�ʱ����true�����򷵻�false
	 */
	private boolean isLeftUp(Point point1, Point point2) {
		return (point2.x < point1.x && point2.y < point1.y);
	}

	/**
	 * �ж�point2�Ƿ���point1�����½�
	 * 
	 * @param point1
	 * @param point2
	 * @return p2λ��p1�����½�ʱ����true�����򷵻�false
	 */
	private boolean isLeftDown(Point point1, Point point2) {
		return (point2.x < point1.x && point2.y > point1.y);
	}

	/**
	 * �ж�point2�Ƿ���point1�����Ͻ�
	 * 
	 * @param point1
	 * @param point2
	 * @return p2λ��p1�����Ͻ�ʱ����true�����򷵻�false
	 */
	private boolean isRightUp(Point point1, Point point2) {
		return (point2.x > point1.x && point2.y < point1.y);
	}

	/**
	 * �ж�point2�Ƿ���point1�����½�
	 * 
	 * @param point1
	 * @param point2
	 * @return p2λ��p1�����½�ʱ����true�����򷵻�false
	 */
	private boolean isRightDown(Point point1, Point point2) {
		return (point2.x > point1.x && point2.y > point1.y);
	}

	/**
	 * ��ȡ��������ͬһ�л���ͬһ�е�������ֱ�����ӵ�, ��ֻ��һ��ת�۵�
	 * 
	 * @param point1
	 *            ��һ����
	 * @param point2
	 *            �ڶ�����
	 * @return ��������ͬһ�л���ͬһ�е�������ֱ�����ӵ�
	 */
	private Point getCornerPoint(Point point1, Point point2, int pieceWidth,
			int pieceHeight) {
		if (isLeftUp(point1, point2) || isLeftDown(point1, point2)) {
			return getCornerPoint(point2, point1, pieceWidth, pieceHeight);
		}
		List<Point> point1RightChannel = getRightChannel(point1, point2.x,
				pieceWidth);
		List<Point> point1UpChannel = getUpChannel(point1, point2.y,
				pieceHeight);
		List<Point> point1DownChannel = getDownChannel(point1, point2.y,
				pieceHeight);
		List<Point> point2DownChannel = getDownChannel(point2, point1.y,
				pieceHeight);
		List<Point> point2LeftChannel = getLeftChannel(point2, point1.x,
				pieceWidth);
		List<Point> point2UpChannel = getUpChannel(point2, point1.y,
				pieceHeight);
		if (isRightUp(point1, point2)) {
			Point linkPoint1 = getWrapPoint(point1RightChannel,
					point2DownChannel);
			Point linkPoint2 = getWrapPoint(point1UpChannel, point2LeftChannel);
			return (linkPoint1 == null) ? linkPoint2 : linkPoint1;
		}
		if (isRightDown(point1, point2)) {
			Point linkPoint1 = getWrapPoint(point1DownChannel,
					point2LeftChannel);
			Point linkPoint2 = getWrapPoint(point1RightChannel, point2UpChannel);
			return (linkPoint1 == null) ? linkPoint2 : linkPoint1;
		}
		return null;
	}

	/**
	 * ��������ͨ��, ��ȡ���ǵĽ���
	 * 
	 * @param p1Channel
	 *            ��һ�����ͨ��
	 * @param p2Channel
	 *            �ڶ������ͨ��
	 * @return ����ͨ���н��㣬���ؽ��㣬���򷵻�null
	 */
	private Point getWrapPoint(List<Point> p1Channel, List<Point> p2Channel) {
		for (int i = 0; i < p1Channel.size(); i++) {
			Point temp1 = p1Channel.get(i);
			for (int j = 0; j < p2Channel.size(); j++) {
				Point temp2 = p2Channel.get(j);
				if (temp1.equals(temp2)) {
					return temp1;
				}
			}
		}
		return null;
	}

	/**
	 * �ж�����y������ͬ�ĵ����֮���Ƿ����ϰ�, ��p1Ϊ�������ұ���
	 * 
	 * @param p1
	 * @param p2
	 * @param pieceWidth
	 * @return ����Piece֮�����ϰ�����true�����򷵻�false
	 */
	private boolean isXBlock(Point p1, Point p2, int pieceWidth) {
		if (p2.x < p1.x) {
			return isXBlock(p2, p1, pieceWidth);
		}
		for (int i = p1.x + pieceWidth; i < p2.x; i = i + pieceWidth) {
			if (hasPiece(i, p1.y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �ж�����x������ͬ�ĵ����֮���Ƿ����ϰ�, ��p1Ϊ�������±���
	 * 
	 * @param p1
	 * @param p2
	 * @param pieceHeight
	 * @return ����Piece֮�����ϰ�����true�����򷵻�false
	 */
	private boolean isYBlock(Point p1, Point p2, int pieceHeight) {
		if (p2.y < p1.y) {
			return isYBlock(p2, p1, pieceHeight);
		}
		for (int i = p1.y + pieceHeight; i < p2.y; i = i + pieceHeight) {
			if (hasPiece(p1.x, i)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * �ж�GamePanel�е�x, y�������Ƿ���Piece����
	 * 
	 * @param x
	 * @param y
	 * @return true ��ʾ�и�������piece���� false ��ʾû��
	 */
	private boolean hasPiece(int x, int y) {
		if (findPiece(x, y) == null)
			return false;
		return true;
	}

	/**
	 * ��һ��Point����,�����������ͨ��
	 * 
	 * @param p
	 * @param pieceWidth
	 *            pieceͼƬ�Ŀ�
	 * @param min
	 *            �������ʱ��С�Ľ���
	 * @return ����Point��ߵ�ͨ��
	 */
	private List<Point> getLeftChannel(Point p, int min, int pieceWidth) {
		List<Point> result = new ArrayList<Point>();
		for (int i = p.x - pieceWidth; i >= min; i = i - pieceWidth) {
			if (hasPiece(i, p.y)) {
				return result;
			}
			result.add(new Point(i, p.y));
		}
		return result;
	}

	/**
	 * ��һ��Point����, ���������ұ�ͨ��
	 * 
	 * @param p
	 * @param pieceWidth
	 * @param max
	 *            ����ʱ�����ҽ���
	 * @return ����Point�ұߵ�ͨ��
	 */
	private List<Point> getRightChannel(Point p, int max, int pieceWidth) {
		List<Point> result = new ArrayList<Point>();
		for (int i = p.x + pieceWidth; i <= max; i = i + pieceWidth) {
			if (hasPiece(i, p.y)) {
				return result;
			}
			result.add(new Point(i, p.y));
		}
		return result;
	}

	/**
	 * ��һ��Point����, ������������ͨ��
	 * 
	 * @param p
	 * @param min
	 *            ���ϱ���ʱ��С�Ľ���
	 * @param pieceHeight
	 * @return ����Point�����ͨ��
	 */
	private List<Point> getUpChannel(Point p, int min, int pieceHeight) {
		List<Point> result = new ArrayList<Point>();
		for (int i = p.y - pieceHeight; i >= min; i = i - pieceHeight) {
			if (hasPiece(p.x, i)) {
				return result;
			}
			result.add(new Point(p.x, i));
		}
		return result;
	}

	/**
	 * ��һ��Point����, ������������ͨ��
	 * 
	 * @param p
	 * @param max
	 *            ���ϱ���ʱ��������
	 * @return ����Point�����ͨ��
	 */
	private List<Point> getDownChannel(Point p, int max, int pieceHeight) {
		List<Point> result = new ArrayList<Point>();
		for (int i = p.y + pieceHeight; i <= max; i = i + pieceHeight) {
			if (hasPiece(p.x, i)) {
				return result;
			}
			result.add(new Point(p.x, i));
		}
		return result;
	}
}
