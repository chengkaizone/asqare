package asqare.cheng.main;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import asqare.cheng.model.Piece;
import asqare.cheng.service.GameConf;
import asqare.cheng.service.GameService;
import asqare.cheng.service.GameServiceImpl;
import asqare.cheng.service.LinkInfo;
import asqare.cheng.widget.GameView;

/**
 * ��ϷUI
 * 
 * @author ganchengkai
 * 
 */
public class AsqareActivity extends Activity implements View.OnTouchListener,
		View.OnClickListener {
	private GameConf config;
	private GameService gameService;
	private GameView gameView;
	private Button startButton;
	private TextView timeTextView;
	private AlertDialog.Builder fail;
	private AlertDialog.Builder win;
	private Timer timer = new Timer();
	// ��¼��Ϸ��ʣ��ʱ��
	private int gameTime;
	// ��¼�Ƿ�����Ϸ״̬
	private boolean isPlaying;
	private Vibrator vibrator;
	// ��¼�Ѿ�ѡ�еķ���
	private Piece selected = null;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0x123:
				timeTextView.setText("ʣ��ʱ�䣺 " + gameTime);
				gameTime--;
				// ʱ��С��0, ��Ϸʧ��
				if (gameTime < 0) {
					stopTimer();
					isPlaying = false;
					fail.show();
					return;
				}
				break;
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		init();
	}

	private void init() {
		config = new GameConf(8, 9, 0, 0, 100000, this);
		gameView = (GameView) findViewById(R.id.gameView);
		timeTextView = (TextView) findViewById(R.id.timeText);
		startButton = (Button) this.findViewById(R.id.startButton);
		vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
		gameService = new GameServiceImpl(this.config);
		gameView.setGameService(gameService);
		startButton.setOnClickListener(this);
		gameView.setOnTouchListener(this);
		fail = new AlertDialog.Builder(this).setTitle("fail!")
				.setMessage("��Ϸʧ��!���¿�ʼ!").setIcon(R.drawable.fail)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startGame(GameConf.DEFAULT_TIME);
					}
				}).setNegativeButton("ȡ��", null);
		win = new AlertDialog.Builder(this).setTitle("win!")
				.setMessage("��Ϸʤ��!���¿�ʼ!").setIcon(R.drawable.win)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						startGame(GameConf.DEFAULT_TIME);
					}
				}).setNegativeButton("ȡ��", null);
	}

	@Override
	protected void onPause() {
		stopTimer();
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (isPlaying) {
			startGame(gameTime);
		}
		super.onResume();
	}

	private void startGame(int gameTime) {
		if (this.timer != null) {
			stopTimer();
		}
		this.gameTime = gameTime;
		if (gameTime == GameConf.DEFAULT_TIME) {
			gameView.startGame();
		}
		isPlaying = true;
		this.timer = new Timer();
		this.timer.schedule(new TimerTask() {
			public void run() {
				handler.sendEmptyMessage(0x123);
			}
		}, 0, 1000);
		// ��ѡ�з�����Ϊnull��
		this.selected = null;
	}

	/**
	 * ���������ӵ����
	 * 
	 * @param linkInfo
	 * @param prePiece
	 *            ǰһ��ѡ�з���
	 * @param currentPiece
	 *            ��ǰѡ�񷽿�
	 * @param pieces
	 *            ϵͳ�л�ʣ��ȫ������
	 */
	private void handleSuccessLink(LinkInfo linkInfo, Piece prePiece,
			Piece currentPiece, Piece[][] pieces) {
		this.gameView.setLinkInfo(linkInfo);
		this.gameView.setSelectedPiece(null);
		this.gameView.postInvalidate();
		// ������Piece�����������ɾ��
		pieces[prePiece.getIndexX()][prePiece.getIndexY()] = null;
		pieces[currentPiece.getIndexX()][currentPiece.getIndexY()] = null;
		// ��ѡ�еķ�������null��
		this.selected = null;
		this.vibrator.vibrate(50);
		// �ж��Ƿ���ʣ�µķ���, ���û��, ��Ϸʤ��
		if (!this.gameService.hasPieces()) {
			this.win.show();
			stopTimer();
			isPlaying = false;
		}
	}

	private void stopTimer() {
		this.timer.cancel();
		this.timer = null;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (!isPlaying) {
			return false;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// ������ʼ������
			Piece[][] pieces = gameService.getPieces();
			// ��ȡ�û������x����
			float touchX = event.getX();
			// ��ȡ�û������y����
			float touchY = event.getY();
			// �����û�����������õ���Ӧ��Piece����
			Piece currentPiece = gameService.findPiece(touchX, touchY);
			// ���û��ѡ���κ�Piece����(��������ĵط�û��ͼƬ), ��������ִ��
			if (currentPiece == null)
				break;
			// ��gameView�е�ѡ�з�����Ϊ��ǰ����
			this.gameView.setSelectedPiece(currentPiece);
			// ��ʾ֮ǰû��ѡ���κ�һ��Piece
			if (this.selected == null) {
				// ����ǰ������Ϊ��ѡ�еķ���, ���½�GamePanel����, ����������ִ��
				this.selected = currentPiece;
				this.gameView.postInvalidate();
				break;
			}
			// ��ʾ֮ǰ�Ѿ�ѡ����һ��
			if (this.selected != null) {
				// �������Ҫ��currentPiece��prePiece�����жϲ���������
				LinkInfo linkInfo = this.gameService.link(this.selected,
						currentPiece);
				// ����Piece������, linkInfoΪnull
				if (linkInfo == null) {
					// ������Ӳ��ɹ�, ����ǰ������Ϊѡ�з���
					this.selected = currentPiece;
					this.gameView.postInvalidate();
				} else {
					// ����ɹ�����
					handleSuccessLink(linkInfo, this.selected, currentPiece,
							pieces);
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			gameView.postInvalidate();
			break;
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		startGame(GameConf.DEFAULT_TIME);
		;
	}
}