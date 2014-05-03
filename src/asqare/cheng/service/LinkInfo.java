package asqare.cheng.service;

import java.util.List;
import java.util.ArrayList;

import android.graphics.Point;

/**
 * ��������������ӵ����---3�����
 * 
 * @author ganchengkai
 * 
 */
public class LinkInfo {
	// ����һ���������ڱ������ӵ�
	private List<Point> points = new ArrayList<Point>();

	/**
	 * ֱ�����������
	 * 
	 * @param p1
	 * @param p2
	 */
	public LinkInfo(Point p1, Point p2) {
		// �ӵ�������ȥ
		points.add(p1);
		points.add(p2);
	}

	/**
	 * һ��ת�۵�����
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 */
	public LinkInfo(Point p1, Point p2, Point p3) {
		points.add(p1);
		points.add(p2);
		points.add(p3);
	}

	/**
	 * ������ת�۵�����
	 * 
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param p4
	 */
	public LinkInfo(Point p1, Point p2, Point p3, Point p4) {
		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
	}

	/**
	 * �������Ӽ���
	 * 
	 * @return
	 */
	public List<Point> getLinkPoints() {
		return points;
	}
}
