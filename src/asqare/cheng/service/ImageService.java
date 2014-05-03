package asqare.cheng.service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import asqare.cheng.main.R;
import asqare.cheng.model.PieceImage;

/**
 * ������Ϸ��ͼƬ�Ĺ�����
 * 
 * @author ganchengkai
 * 
 */
public class ImageService {
	// ��������������ͼƬ��Դֵdrawable�ļ����µ���ԴId
	private static List<Integer> imageValues = getImageValues();

	/**
	 * ͨ�������ȡ��candy_��ͷ����ԴͼƬ
	 * 
	 * @return
	 */
	public static List<Integer> getImageValues() {
		try {
			Field[] drawableFields = R.drawable.class.getFields();
			List<Integer> resourceValues = new ArrayList<Integer>();
			for (Field field : drawableFields) {
				// �����Field��������candy_��ͷ
				if (field.getName().indexOf("candy_") == 0) {
					resourceValues.add(field.getInt(R.drawable.class));
				}
			}
			return resourceValues;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * �����sourceValues�ļ����л�ȡsize��ͼƬId
	 * 
	 * @param sourceValues
	 *            ���л�ȡ�ļ���
	 * @param size
	 *            ��Ҫ��ȡ�ĸ���
	 * @return size��ͼƬId�ļ���
	 */
	public static List<Integer> getRandomValues(List<Integer> sourceValues,
			int size) {
		Random random = new Random();
		List<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < size; i++) {
			try {
				// sizeΪ0ʱ���׳��Ƿ������쳣
				int index = random.nextInt(sourceValues.size());
				Integer image = sourceValues.get(index);
				result.add(image);
			} catch (IndexOutOfBoundsException e) {
				return result;
			}
		}
		return result;
	}

	/**
	 * ��drawableĿ¼���л�ȡsize��ͼƬ��ԴId(��candy_Ϊǰ׺����Դ����)
	 * 
	 * @param size
	 *            ��Ҫ��ȡ��ͼƬId������
	 * @return size��ͼƬID�ļ���
	 */
	public static List<Integer> getPlayValues(int size) {
		// �����ż��
		if (size % 2 != 0) {
			size += 1;
		}
		List<Integer> playImageValues = getRandomValues(imageValues, size / 2);
		playImageValues.addAll(playImageValues);
		// ����
		Collections.shuffle(playImageValues);
		return playImageValues;
	}

	/**
	 * ��id��λͼ��װ��PieceImage����
	 * 
	 * @param context
	 * @param resourceValues
	 * @return size��PieceImage����ļ���
	 */
	public static List<PieceImage> getPlayImages(Context context, int size) {
		List<Integer> resourceValues = getPlayValues(size);
		List<PieceImage> result = new ArrayList<PieceImage>();
		for (Integer value : resourceValues) {
			Bitmap src = BitmapFactory.decodeResource(context.getResources(),
					value);
			// �����ƶ���С�ķ���ͼƬ
			Bitmap des = Bitmap.createBitmap(src, 0, 0, GameConf.PIECE_WIDTH,
					GameConf.PIECE_HEIGHT);
			// ��װͼƬID��ͼƬ����
			PieceImage pieceImage = new PieceImage(des, value);
			result.add(pieceImage);
		}
		return result;
	}

	/**
	 * ��ȡѡ�б�ʶ��ͼƬ
	 * 
	 * @param context
	 * @return ���ر�ѡ��λͼ��־
	 */
	public static Bitmap getSelectImage(Context context) {
		Bitmap bm = BitmapFactory.decodeResource(context.getResources(),
				R.drawable.selected);
		return bm;
	}
}
