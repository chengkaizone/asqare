/**
 * 
 */
package asqare.cheng.model;

import android.graphics.Bitmap;

/**
 * ��װ�����λͼ��idֵ
 * 
 * @author ganchengkai
 * 
 */
public class PieceImage {
	private Bitmap image;
	private int imageId;

	/**
	 * ��������ͼƬ
	 * 
	 * @param image
	 * @param imageId
	 */
	public PieceImage(Bitmap image, int imageId) {
		super();
		this.image = image;
		this.imageId = imageId;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public int getImageId() {
		return imageId;
	}

	public void setImageId(int imageId) {
		this.imageId = imageId;
	}
}
