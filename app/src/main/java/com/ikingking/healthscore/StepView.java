package com.ikingking.healthscore;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by lianchaoyun on 2017/11/21.
 */

public class StepView extends View {
    private String  healthDesc="健康状态良好";
    private int status=0;//0  正常  1   未登录   2  未完善信息
    private int maxScore=1000;
    private static int score=1;
    private int scoreView=1;
    private int angle=0;

    private String healthTime="评估时间：2017-10-11";
    private static int markCount=36;//刻度数量
    private  int spacing=30;//外边框与刻度边框距离
    private  int spacingOut=2;//外边框与刻度边框距离
    private int width;//控件宽度
    private int height;//控件高度
    public static final int[] SWEEP_GRADIENT_COLORS = new int[markCount];//刻度颜色渐变
    private int tableWidth = 5;//刻度线高度
    float radius;//半径
    private Paint paintOut;//外圆画笔
    private Paint mPaint;//刻度画笔
    private Paint paintText;//文本画笔
    private Paint rectPaint;//矩形画笔

    private Path pathOut;//外圆路径
    private Path mPath;//刻度路径
    //指针的路径
    private float mCurrentDegree = 60;  //当前角度
    private  RectF rectout;//外矩形


    private RectF mTableRectF;
    //把路径分成虚线段的
    private DashPathEffect dashPathEffect;
    //给路径上色
    private SweepGradient mColorShader;


    Bitmap bmp;
    private RectF rectbmp;


    public StepView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res=getResources();
         bmp=BitmapFactory.decodeResource(res, R.mipmap.pointer);
        rectbmp=new RectF(0,0,bmp.getWidth(),bmp.getHeight());
        initColor();


        paintOut=new  Paint();
        paintOut.setStyle(Paint.Style.STROKE);
        paintOut.setStrokeWidth(3);
        paintOut.setColor(Color.WHITE);



        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);


        rectPaint=new  Paint();
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(3);
        rectPaint.setColor(Color.WHITE);


        paintText=new Paint();;
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setColor(Color.WHITE);

        pathOut= new Path();;

        mPath = new Path();
        //startAnimator();
    }




    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height =h;

        rectout=new RectF(0+spacingOut, 0+spacingOut, width-spacingOut, height-spacingOut);
        pathOut.reset();
        pathOut.addArc(rectout, 60, 240);

        //油表的位置方框
        mTableRectF = new RectF(0, 0, width-spacing*2, height-spacing*2);
        mPath.reset();
        //在油表路径中增加一个从起始弧度
        mPath.addArc(mTableRectF, 60, 240);
        //计算路径的长度
        PathMeasure pathMeasure = new PathMeasure(mPath, false);
        float length = pathMeasure.getLength();
        float step = length / markCount;
        dashPathEffect = new DashPathEffect(new float[]{step / 3, step * 2 / 3}, 0);

         radius = width / 2;

        initColor();
        //设置指针的路径位置

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
      //  canvas.drawColor(Color.parseColor("#2B79E5"));//绘制背景
        drawOutArc(canvas);
        drawScalemark(canvas);
        drawCursor(canvas);
        drawText(canvas);
    }



    public void drawOutArc(Canvas canvas){
        canvas.save();
        canvas.translate(spacingOut,spacingOut);
        canvas.rotate(90, rectout.width() / 2, rectout.height() / 2);
        canvas.drawPath(pathOut,paintOut);
        canvas.restore();
    }

    public void drawScalemark(Canvas canvas){
        canvas.save();
        //把油表的方框平移到正中间
        canvas.translate(spacing,spacing);
        //旋转画布
        canvas.rotate(90, mTableRectF.width() / 2, mTableRectF.height() / 2);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(tableWidth);
        mPaint.setPathEffect(dashPathEffect);
        mPaint.setShader(mColorShader);
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    public void drawCursor(Canvas canvas){
        mPaint.setPathEffect(null);
        mPaint.setShader(null);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(tableWidth / 10);
        canvas.save();
        canvas.rotate(150 + mCurrentDegree, mTableRectF.width() / 2+spacing, mTableRectF.height() / 2+spacing);

        float rate=(float)mCurrentDegree/angle;
        scoreView=(int)((float)score*rate);
        canvas.translate(width/2+radius-spacing-tableWidth-spacing-8,height/2);
        canvas.drawBitmap(bmp,null,rectbmp,null);
        canvas.restore();

    }
    public void drawText(Canvas canvas){



        if(status == 1){
            canvas.save();

            Paint.FontMetrics fm = paintText.getFontMetrics();
            int textheight= (int) Math.ceil(fm.descent - fm.top) + 2;
            canvas.drawRect(width/2-200,height/2-60-textheight/3,width/2+200,height/2+60-textheight/3,rectPaint);
            paintText.setTextSize(16*3);
            canvas.drawText("点击登录",width/2,height/2,paintText);
            canvas.restore();
        }else if(status == 2 ){
            canvas.save();
            Paint.FontMetrics fm = paintText.getFontMetrics();
            int textheight= (int) Math.ceil(fm.descent - fm.top) + 2;
            canvas.drawRect(width/2-220,height/2-60-textheight/3,width/2+220,height/2+60-textheight/3,rectPaint);
            paintText.setTextSize(16*3);
            canvas.drawText("点击完善身份信息",width/2,height/2,paintText);
            canvas.restore();
        }else{
            canvas.save();
            paintText.setTextSize(36*3);
            canvas.drawText(""+scoreView,width/2,height/2,paintText);
            paintText.setTextSize(14*3);
            canvas.drawText(""+healthDesc,width/2,height/2-120,paintText);
            canvas.drawText(""+healthTime,width/2,height/2+70,paintText);
            canvas.restore();
        }
    }



    public void initColor(){  //0 step  36
        if(score<1){
            score=1;
        }
        int step=(int)(score/maxScore*markCount);
        int tail=markCount-step;
        if(tail<1){
            tail=1;
        }
        int alpstep=(int)255/tail;
        for(int i=0;i<markCount;i++){
            if(i<step){
                SWEEP_GRADIENT_COLORS[i]=Color.argb(255,255,255,255);;
            }else{
                int a=Math.abs(255- (i-step)*alpstep);
                if(a>255){
                    a=255;
                }
                SWEEP_GRADIENT_COLORS[i]=Color.argb(a,255,255,255);;
            }
        }

        mColorShader = new SweepGradient(radius, radius,SWEEP_GRADIENT_COLORS,null);
    }

    public void startAnimator() {
        angle=(int)(240*((float)score/(float)maxScore));
       // Utils.log("startAnimator"+angle+","+score+","+maxScore);
        ValueAnimator animator = ValueAnimator.ofFloat(0, angle);
        animator.setDuration(4000);
        //animator.setRepeatCount(ValueAnimator.INFINITE);
        //animator.setRepeatMode(ValueAnimator.RESTART);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentDegree = (int) (0 + (Float) animation.getAnimatedValue());
                invalidate();
            }
        });
        animator.start();

    }

    public void setValue(int score){
        /*
        if(!UserUtils.getInstance().isLogin()){
            status = 1;
            this.score=0;
        }else if(!UserUtils.getInstance().isInfo()){
            status=2;
            this.score=0;
        }else{

        }

        */
            status =0;
           // User user=UserUtils.getInstance().getUser();
          //  this.score=user.getHealthScore();
            this.score=score;
            if(this.score<499){
                this.healthDesc="健康较差";
            }else if(this.score<599){

                this.healthDesc="健康一般";

            }else if(this.score<7999){
                this.healthDesc="健康良好";

            }else{
                this.healthDesc="非常健康";
            }
            this.healthTime="评估时间："+ parseToString(System.currentTimeMillis(),yyyyMMdd);


        initColor();
        startAnimator();
    }
    public static final String yyyyMMdd = "yyyy-MM-dd";

    public static String parseToString(long curentTime, String style) {
        Calendar now = Calendar.getInstance();
        now.setTimeInMillis(curentTime);
        SimpleDateFormat formatter = new SimpleDateFormat(style);
        String str = formatter.format(now.getTime());
        return str;
    }

}
