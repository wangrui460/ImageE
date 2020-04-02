package com.wangrui.imagee.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

import com.wangrui.imagee.utils.PointUtils;

import java.util.ArrayList;

/**
 * 标签操作控件
 * @author panyi
 */
public class LabelTextView extends AppCompatTextView {

	private static int STATUS_IDLE = 0;
	private static int STATUS_MOVE = 1;		// 移动状态
	private static int STATUS_DELETE = 2;	// 删除状态
	private static int STATUS_ROTATE = 3;	// 图片旋转状态

	// 当前状态
	private int currentStatus;
	// 当前操作的贴图数据
	private TextItem currentItem;
	private float oldx, oldy;

	// 存贮每层贴图数据
	private ArrayList<TextItem> banks = new ArrayList<>();

	public LabelTextView(Context context) {
		this(context, null);
	}

	public LabelTextView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		currentStatus = STATUS_IDLE;
	}

	public void addText(Typeface typeface, String text) {
		TextItem item = new TextItem(this.getContext());
		item.init(text, typeface, this);
		if (currentItem != null) {
			currentItem.isDrawHelpTool = false;
		}
		banks.add(item);
		// 重绘视图
		invalidate();
	}

	/**
	 * 绘制客户页面
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		for (TextItem item : banks) {
			item.draw(canvas);
		}
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// 是否向下传递事件标志 true为消耗
		boolean ret = super.onTouchEvent(event);

		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
				for (TextItem item : banks) {
					// 矫正触摸位置，适应旋转后的文字
					PointF point = new PointF(x, y);
					point = PointUtils.rotatePoint(point, new PointF(item.targetRect.centerX, item.targetRect.centerY), -item.targetRect.rotate);
					boolean isDelete = item.generateDeleteRect().contains(point.x, point.y);
					boolean isRotate = item.generateRotateRect().contains(point.x, point.y);
					boolean isMove = item.targetRect.contains(point.x, point.y);
					if (isDelete || isRotate || isMove) {
						ret = true;
						if (currentItem != null) {
							currentItem.isDrawHelpTool = false;
						}
						currentItem = item;
						currentItem.isDrawHelpTool = true;
						oldx = x;
						oldy = y;
					}
					if (isDelete) {
						// 删除模式
						currentStatus = STATUS_DELETE;
					} else if (isRotate) {
						// 点击了旋转按钮
						currentStatus = STATUS_ROTATE;
					} else if (isMove) {
						// 移动模式/被选中一张贴图
						currentStatus = STATUS_MOVE;
					}
				}

				if (!ret && currentItem != null && currentStatus == STATUS_IDLE) {
					// 没有贴图被选择
					currentItem.isDrawHelpTool = false;
					currentItem = null;
					invalidate();
				}

				if (currentItem != null && currentStatus == STATUS_DELETE) {
					// 删除选定贴图
					banks.remove(currentItem);
					currentItem.isDrawHelpTool = false;
					currentItem = null;
					currentStatus = STATUS_IDLE;// 返回空闲状态
					invalidate();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				ret = true;
				if (currentStatus == STATUS_MOVE) {
					// 移动贴图
					float dx = x - oldx;
					float dy = y - oldy;
					if (currentItem != null) {
						currentItem.updatePos(dx, dy);
						invalidate();
					}// end if
					oldx = x;
					oldy = y;
				} else if (currentStatus == STATUS_ROTATE) {
					// 旋转 缩放图片操作
					float dx = x - oldx;
					float dy = y - oldy;
					if (currentItem != null) {
						currentItem.updateRotateAndScale(dx, dy);// 旋转
						invalidate();
					}// end if
					oldx = x;
					oldy = y;
				}
				break;
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP:
				ret = false;
				currentStatus = STATUS_IDLE;
				break;
		}
		return ret;
	}

	public ArrayList<TextItem> getBanks() {
		return banks;
	}

	public void clear() {
		banks.clear();
		this.invalidate();
	}
}
