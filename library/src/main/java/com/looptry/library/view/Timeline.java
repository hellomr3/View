package com.looptry.library.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import com.looptry.library.R;

import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import kotlin.Pair;

/**
 * Author: xiao
 * Date: 2019/12/6 15:43
 * Desc: 视频回放时间轴
 * Modify By:
 * Modify Date:
 */
public class Timeline extends View {
    private static final int MIDDLE_LINE_COLOR = 0xff089a5a;
    private static final int DEFAULT_TIME_LINE_COLOR = Color.DKGRAY;
    private static final int DEFAULT_USABLE_COLOR = Color.parseColor("#E3E9FF");
    private static final int DEFAULT_TIME_LINE_HEIGT = 10; //dp
    private static final int DEFAULT_TIME_LINE_WIDTH = 1;//dp

    private static final int DEFAULT_TEXT_SIZE = 12;//sp
    private static final int DEFAULT_TEXT_COLOR = Color.parseColor("#333333");

    private static final int DEFAULT_HEIGHT = 40;//dp
    private static final int DEFAULT_TEXT_PADDING = 10;
    private static final int DEFAULT_DIVISOR_WIDTH = 100;

    /**
     * 5分钟时刻线
     */
    public static final int FIVE_MINUTES_MODE = 0x01;

    /**
     * 10分钟时刻线
     */
    public static final int TEN_MINUTES_MODE = 0x02;

    /**
     * 30分钟时刻线
     */
    public static final int THIRTY_MINUTES_MODE = 0x03;

    /**
     * 60分钟时刻线
     */
    public static final int SIXTY_MINUTES_MODE = 0x04;

    /**
     * 5分钟毫秒值
     */
    public static final float FIVE_MINUTES = 5 * 60 * 1000;

    /**
     * 10分钟毫秒值
     */
    public static final float TEN_MINUTES = 10 * 60 * 1000;

    /**
     * 30分钟毫秒值
     */
    public static final float THIRTY_MINUTES = 30 * 60 * 1000;

    /**
     * 1小时代表的毫秒值
     */
    public static final float SIXTY_MINUTES = 60 * 60 * 1000;

    /**
     * 中间点时间毫秒值
     */
    private long mMiddleLineMillis = 0;

    /**
     * 移动敏感度，过小会导致刷新太频繁
     */
    private static final float MOVE_SENSITIVE = 0.2f;

    /**
     * 每一小格的最低宽度
     */
    private float mMinDivisorWidth = 50;

    /**
     * 每一小格的最大宽度
     */
    private float mMaxDivisorWidth = 200;

    /**
     * 每一小格的宽度,默认值为80像素
     */
    private float mDivisorWidth = DEFAULT_DIVISOR_WIDTH;

    /**
     * 时刻线初始Y坐标
     */
    private float mTimelineStartPositionY = 0;

    /**
     * 上一次触摸的X坐标
     */
    private float mLastTouchX = 0f;

    /**
     * 是否有移动
     */
    private boolean mTouchMoved = false;

    /**
     * 按下标志,按下时相应外部输入
     */
    private boolean mTouchDownFlag = false;

    /**
     * 时间条被冻结标志，冻结后时间条不能被移动
     */
    private boolean mIsFrozen = false;

    /**
     * 是否双击标识
     */
    private boolean isTwoFinger;

    /**
     * 双指之前的距离
     */
    private float beforeDistance;

    /**
     * 双指移动后的距离
     */
    private float afterDistance;

    /**
     * 时刻线缩放比例
     */
    private float mScaleRate = 1f;

    /**
     * 屏幕左测距中心点时长
     */
    private long mMiddleLineDuration;

    /**
     * 屏幕最左测时间
     */
    private long mLeftTimeOnScreen;

    /**
     * 时间轴拖动回调对象
     */
    private TimePickedCallBack onTimeBarMoveListener;

    /**
     * 有录像回放时，进行自动向前播放
     */
    public Timer mTimer;

    /**
     * 是否有回放录像记录
     */
    public boolean isHasVideoRecord = false;

    /**
     * 有录像记录的列表
     */
    public List<Pair<Long, Long>> recordList;

    private int mMiddleLineColor = MIDDLE_LINE_COLOR;
    private int mUsableAreaColor = DEFAULT_USABLE_COLOR;
    private int mTimelineColor = DEFAULT_TIME_LINE_COLOR;
    private int mTimelineHeight = DEFAULT_TIME_LINE_HEIGT;
    private int mTextSize = DEFAULT_TEXT_SIZE;
    private int mTextColor = DEFAULT_TEXT_COLOR;
    private int mTimelineWidth = DEFAULT_TIME_LINE_WIDTH;
    private int mWidth;
    private int mHeight;

    /**
     * 当前时刻线显示模式
     */
    private int mCurrentTimelineMode = SIXTY_MINUTES_MODE;

    /**
     * 时刻线偏移量
     */
    private int mCurrentOffset = 0;

    /**
     * 时间值偏移量
     */
    private int mCurrentTimeOffset = 0;

    /**
     * 时刻单位： 时长/每像素
     */
    private float mCurrentTimeUnit = 1f;

    private Paint mMiddleLinePaint = new Paint();
    private Paint mTimelinePaint = new Paint();
    private Paint mUsableAreaPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTextPaint = new Paint();

    public Timeline(Context context) {
        this(context, null);
    }

    public Timeline(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public Timeline(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.Timeline, defStyleAttr, 0);
        mMiddleLineColor = ta.getColor(R.styleable.Timeline_middle_line_color, MIDDLE_LINE_COLOR);
        mTimelineColor = ta.getColor(R.styleable.Timeline_time_line_color, DEFAULT_TIME_LINE_COLOR);
        mUsableAreaColor = ta.getColor(R.styleable.Timeline_usable_area_color, DEFAULT_USABLE_COLOR);

        DisplayMetrics dm = Resources.getSystem().getDisplayMetrics();

        mTimelineHeight = ta.getDimensionPixelSize(R.styleable.Timeline_time_line_height,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIME_LINE_HEIGT, dm));
        mTimelineWidth = ta.getDimensionPixelSize(R.styleable.Timeline_time_line_width,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TIME_LINE_WIDTH, dm));

        mTextSize = ta.getDimensionPixelSize(R.styleable.Timeline_android_textSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE, dm));
        mTextColor = ta.getColor(R.styleable.Timeline_android_textColor, DEFAULT_TEXT_COLOR);

        mHeight = TypedValue.complexToDimensionPixelSize(DEFAULT_HEIGHT, dm);

        ta.recycle();
        init();
    }

    private void init() {
        mMiddleLinePaint.setColor(mMiddleLineColor);
        mMiddleLinePaint.setDither(true);
        mMiddleLinePaint.setAntiAlias(true);
        mMiddleLinePaint.setStrokeWidth(mTimelineWidth);

        mTimelinePaint.setColor(mTimelineColor);
        mTimelinePaint.setDither(true);
        mTimelinePaint.setAntiAlias(true);
        mTimelinePaint.setStrokeWidth(mTimelineWidth);

        mUsableAreaPaint.setColor(mUsableAreaColor);
        mUsableAreaPaint.setDither(true);
        mUsableAreaPaint.setStyle(Paint.Style.FILL);

        mTextPaint.setColor(mTextColor);
        mTextPaint.setDither(true);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(mTextSize);


        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        mMiddleLineMillis = calendar.getTimeInMillis();

        //初始化
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        Log.d("Timeline", "初始化 mMiddleLineMillis = " + format.format(calendar.getTime()));
        Log.d("Timeline", "初始化 mMiddleLineMillis = " + mMiddleLineMillis);

        //保存文件记录
        recordList = new ArrayList<>();
    }

    /**
     * 有录像回放时，启动计时器自动向前播放
     */
    public void startTimer() {
        if (isHasVideoRecord) {
            if (mTimer == null) {
                mTimer = new Timer();
            }

            mTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (isHasVideoRecord) {
                        mMiddleLineMillis = System.currentTimeMillis();

                        //重新计算偏移量
                        calculationOffset();

                        //控件重绘
                        postInvalidate();

                        if (onTimeBarMoveListener != null) {
                            onTimeBarMoveListener.onBarMoving(mMiddleLineMillis);
                        }
                    } else {
                        mTimer.cancel();
                    }
                }
            }, 0, 1000);
        }
    }

    /**
     * 停止计时器
     */
    public void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (height > mHeight) {
            mHeight = height;
        }

        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mTimelineStartPositionY = mHeight - mTimelineHeight;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mWidth = w;
        calculationOffset();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mLastTouchX = event.getX();
                mTouchDownFlag = true;

                stopTimer();
                break;
            case MotionEvent.ACTION_UP:
                if (isTwoFinger) {
                    //双指抬起
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mIsFrozen = false;
                        }
                    }, 100);
                }

                if (!isTwoFinger && mTouchMoved) {
                    //拖动结束后回调当前中线的时间戳
                    if (onTimeBarMoveListener != null) {
                        onTimeBarMoveListener.onTimePickedCallback(mMiddleLineMillis);
                    }

                    //重新开启计时器
                    startTimer();
                }

                mTouchMoved = false;
                mTouchDownFlag = false;
                isTwoFinger = false;
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerCount() == 2 && isTwoFinger) {
                    //双指移动
                    afterDistance = calculationDistance(event);

                    if (afterDistance == 0) {
                        beforeDistance = afterDistance;
                    }

                    //变化的长度
                    float moveDistance = afterDistance - beforeDistance;
                    if (Math.abs(moveDistance) > 5f) {
                        //缩放比例
                        mScaleRate = afterDistance / beforeDistance;
                        beforeDistance = afterDistance;

                        onZooming();
                    }
                } else if (event.getPointerCount() == 1) {
                    onActionMove(event);
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() == 2) {
                    //双指按下
                    mIsFrozen = true;
                    beforeDistance = calculationDistance(event);
                    isTwoFinger = true;
                }
                break;
        }


        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制控件底部标识线
        canvas.drawLine(0, mHeight, mWidth, mHeight, mTimelinePaint);
        Log.d("TAG", "onDraw");
        //绘制可用区域
        drawUsableArea(canvas);
        //绘制时刻修改
        drawTimeLine(canvas);
        //绘制中间线
        canvas.drawLine((mWidth - mTimelineWidth) / 2, 0, (mWidth + mTimelineWidth) / 2, mHeight, mMiddleLinePaint);
    }

    /**
     * 绘制时间尺刻度
     *
     * @param canvas
     */
    private void drawTimeLine(Canvas canvas) {
        //计算可视区域绘制的时刻数量
        int timeLines = (int) (mWidth / mDivisorWidth) + 2;

        for (int i = 0; i < timeLines; i++) {
            canvas.drawLine(mCurrentOffset + i * mDivisorWidth, mHeight, mCurrentOffset + i * mDivisorWidth, mTimelineStartPositionY, mTimelinePaint);

            if (i % 2 == 0) {
                long time = (long) (i * mDivisorWidth * mCurrentTimeUnit);
                String timeTxt = getHourMinute(time + mLeftTimeOnScreen + mCurrentTimeOffset);
                float timeTxtWidth = mTextPaint.measureText(timeTxt);

                int txtStartX = (int) (mCurrentOffset + i * mDivisorWidth - timeTxtWidth / 2);
                canvas.drawText(timeTxt, txtStartX, mTimelineStartPositionY - DEFAULT_TEXT_PADDING, mTextPaint);
            }
        }
    }

    /**
     * 绘制可用区域
     */
    private void drawUsableArea(Canvas canvas) {
        //已知左侧时间(mLeftTimeOnScreen)、左侧到中间侧时间(mMiddleLineDuration)
        for (Pair<Long, Long> item : recordList) {
            drawOneArea(item.getFirst(), item.getSecond(), canvas);
        }
    }

    private void drawOneArea(Long start, Long end, Canvas canvas) {
        Long realStart = start;
        Long realEnd = end;
        //屏幕的左右侧时间
        Long leftTime = mLeftTimeOnScreen;
        Long rightTime = mLeftTimeOnScreen + 2 * mMiddleLineDuration;
        //开始时间>右侧时间 或结束时间<左侧时间 不处理
        if (start > rightTime || end < leftTime) {
            return;
        }
        //开始时间小于左侧时间时只绘制左侧到结束
        if (start < leftTime) {
            realStart = leftTime;
        }
        //结束时间大于右侧时间只绘制至右侧
        if (end > rightTime) {
            realEnd = rightTime;
        }
        //区域长度
        float mAreaWidth = (float) ((double) (realEnd - realStart) /
                (rightTime - leftTime)) *
                (mWidth);
        //左侧位置距离开始位置的长度
        float leftWidth = (float) ((double) (realStart - leftTime) /
                (rightTime - leftTime) *
                mWidth);

        canvas.drawRect(leftWidth, 0, leftWidth + mAreaWidth, mHeight - mTimelineWidth * 0.8f, mUsableAreaPaint);
    }

    /**
     * 缩放
     */
    private void onZooming() {
        mDivisorWidth = Math.round(mDivisorWidth * mScaleRate);

        //放大
        if (mScaleRate > 1) {
            switch (mCurrentTimelineMode) {
                case SIXTY_MINUTES_MODE: {
                    if (mDivisorWidth > mMaxDivisorWidth) {
                        mCurrentTimelineMode = THIRTY_MINUTES_MODE;
                        mDivisorWidth = DEFAULT_DIVISOR_WIDTH;
                    }
                }
                break;
                case THIRTY_MINUTES_MODE:
                    if (mDivisorWidth > mMaxDivisorWidth) {
                        mCurrentTimelineMode = TEN_MINUTES_MODE;
                        mDivisorWidth = DEFAULT_DIVISOR_WIDTH;
                    }
                    break;
                case TEN_MINUTES_MODE:
                    if (mDivisorWidth > mMaxDivisorWidth) {
                        mCurrentTimelineMode = FIVE_MINUTES_MODE;
                        mDivisorWidth = DEFAULT_DIVISOR_WIDTH;
                    }
                    break;
                case FIVE_MINUTES_MODE:
                    if (mDivisorWidth > DEFAULT_DIVISOR_WIDTH) {
                        mDivisorWidth = DEFAULT_DIVISOR_WIDTH;
                    }
                    break;
            }
        } else {
            //缩小
            switch (mCurrentTimelineMode) {
                case SIXTY_MINUTES_MODE: {
                    if (mDivisorWidth < DEFAULT_DIVISOR_WIDTH) {
                        mDivisorWidth = DEFAULT_DIVISOR_WIDTH;
                    }
                }
                break;
                case THIRTY_MINUTES_MODE:
                    if (mDivisorWidth < mMinDivisorWidth) {
                        mCurrentTimelineMode = SIXTY_MINUTES_MODE;
                        mDivisorWidth = DEFAULT_DIVISOR_WIDTH;
                    }
                    break;
                case TEN_MINUTES_MODE:
                    if (mDivisorWidth < mMinDivisorWidth) {
                        mCurrentTimelineMode = THIRTY_MINUTES_MODE;
                        mDivisorWidth = DEFAULT_DIVISOR_WIDTH;
                    }
                    break;
                case FIVE_MINUTES_MODE:
                    if (mDivisorWidth < mMinDivisorWidth) {
                        mCurrentTimelineMode = TEN_MINUTES_MODE;
                        mDivisorWidth = DEFAULT_DIVISOR_WIDTH;
                    }
                    break;
            }
        }

        //重新计算偏移量
        calculationOffset();

        //重新绘制
        invalidate();
    }

    /**
     * 单指滑动
     */
    private void onActionMove(MotionEvent event) {
        float movedX = event.getX() - mLastTouchX;
        if (Math.abs(movedX) < MOVE_SENSITIVE) {
            return;
        }
        // 更新上一次按下的X坐标
        mLastTouchX = event.getX();

        //禁止移动时，返回
        if (mIsFrozen) {
            return;
        }

        mTouchMoved = true;

        //更新当前中线代表的时间
        switch (mCurrentTimelineMode) {
            case FIVE_MINUTES_MODE:
                mMiddleLineMillis = mMiddleLineMillis - (long) (movedX * (FIVE_MINUTES / mDivisorWidth));
                break;
            case TEN_MINUTES_MODE:
                mMiddleLineMillis = mMiddleLineMillis - (long) (movedX * (TEN_MINUTES / mDivisorWidth));
                break;
            case THIRTY_MINUTES_MODE:
                mMiddleLineMillis = mMiddleLineMillis - (long) (movedX * (THIRTY_MINUTES / mDivisorWidth));
                break;
            case SIXTY_MINUTES_MODE:
                mMiddleLineMillis = mMiddleLineMillis - (long) (movedX * (SIXTY_MINUTES / mDivisorWidth));
                break;
        }

        //重新计算偏移量
        calculationOffset();

        //控件重绘
        invalidate();

        //时间轴被拖动的时候回调
        if (onTimeBarMoveListener != null) {
            onTimeBarMoveListener.onMoveTimeCallback(mMiddleLineMillis);
        }
    }

    /**
     * 计算偏移量
     */
    private void calculationOffset() {
        Calendar calendar = Calendar.getInstance();

        //计算中心点距屏幕左测时长
        switch (mCurrentTimelineMode) {
            case FIVE_MINUTES_MODE:
                //单位： (FIVE_MINUTES / mDivisorWidth))  时长/每像素
                mCurrentTimeUnit = FIVE_MINUTES / mDivisorWidth;
                mMiddleLineDuration = (long) ((mWidth / 2f) * mCurrentTimeUnit);

                //计算屏幕最左边的时间
                mLeftTimeOnScreen = mMiddleLineMillis - mMiddleLineDuration;
                calendar.setTimeInMillis(mLeftTimeOnScreen);
                mCurrentTimeOffset = 5 * 60 - ((calendar.get(Calendar.MINUTE) % 5) * 60 + calendar.get(Calendar.SECOND));
                break;
            case TEN_MINUTES_MODE:
                mCurrentTimeUnit = TEN_MINUTES / mDivisorWidth;
                mMiddleLineDuration = (long) ((mWidth / 2f) * mCurrentTimeUnit);

                //计算屏幕最左边的时间
                mLeftTimeOnScreen = mMiddleLineMillis - mMiddleLineDuration;
                calendar.setTimeInMillis(mLeftTimeOnScreen);

                mCurrentTimeOffset = 10 * 60 - ((calendar.get(Calendar.MINUTE) % 10) * 60 + calendar.get(Calendar.SECOND));
                break;
            case THIRTY_MINUTES_MODE:
                mCurrentTimeUnit = THIRTY_MINUTES / mDivisorWidth;
                mMiddleLineDuration = (long) ((mWidth / 2f) * mCurrentTimeUnit);

                //计算屏幕最左边的时间
                mLeftTimeOnScreen = mMiddleLineMillis - mMiddleLineDuration;
                calendar.setTimeInMillis(mLeftTimeOnScreen);
                mCurrentTimeOffset = 30 * 60 - ((calendar.get(Calendar.MINUTE) % 30) * 60 + calendar.get(Calendar.SECOND));

                break;
            case SIXTY_MINUTES_MODE:
                mCurrentTimeUnit = SIXTY_MINUTES / mDivisorWidth;
                mMiddleLineDuration = (long) ((mWidth / 2f) * mCurrentTimeUnit);

                //计算屏幕最左边的时间
                mLeftTimeOnScreen = mMiddleLineMillis - mMiddleLineDuration;
                calendar.setTimeInMillis(mLeftTimeOnScreen);
                mCurrentTimeOffset = 60 * 60 - ((calendar.get(Calendar.MINUTE) % 60) * 60 + calendar.get(Calendar.SECOND));

                break;
        }

        mCurrentTimeOffset *= 1000;
        mCurrentOffset = Math.round(mCurrentTimeOffset / mCurrentTimeUnit);
    }

    /**
     * 计算距离(待优化)
     *
     * @param event
     * @return
     */
    private float calculationDistance(MotionEvent event) {
        //TODO  (待优化)
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * 根据下标获取HH:mm格式的时间
     */
    public String getHourMinute(long timeMills) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return format.format(new Date(timeMills));
    }

    /**
     * 设置当前时间
     *
     * @param timeMills
     */
    public void setCurrentTimeMills(long timeMills) {
        if (timeMills <= 0 || mTouchDownFlag) {
            return;
        }

        this.mMiddleLineMillis = timeMills;
        //重新计算偏移量
        calculationOffset();
        postInvalidate();
        //时间轴自动移动的时候回调
        if (onTimeBarMoveListener != null) {
            onTimeBarMoveListener.onBarMoving(mMiddleLineMillis);
        }
    }

    /**
     * 设置可用录像列表 Pair<Long,Long> 开始，结束时间
     */
    public void setRecordList(List<Pair<Long, Long>> recordList) {
        this.recordList = recordList;
        postInvalidate();
    }

    /**
     * 设置移动监听
     */
    public void setTimeBarCallback(TimePickedCallBack onBarMoveListener) {
        onTimeBarMoveListener = onBarMoveListener;
    }

    /**
     * 时间轴移动、拖动的回调
     */
    public interface TimePickedCallBack {
        /**
         * 当时间轴被拖动的时候回调
         *
         * @param currentTime 拖动到的中线时间
         */
        void onMoveTimeCallback(long currentTime);

        /**
         * 当时间轴自动移动的时候回调
         *
         * @param currentTime 当前时间
         */
        void onBarMoving(long currentTime);

        /**
         * 当拖动完成时回调
         *
         * @param currentTime 拖动结束时的时间
         */
        void onTimePickedCallback(long currentTime);
    }
}
