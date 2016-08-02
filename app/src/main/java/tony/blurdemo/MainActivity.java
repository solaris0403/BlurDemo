package tony.blurdemo;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.commit451.nativestackblur.NativeStackBlur;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private String[] mImages = new String[]{
            "http://g.hiphotos.baidu.com/image/pic/item/241f95cad1c8a7866f726fe06309c93d71cf5087.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/728da9773912b31bf31c3afb8218367adbb4e1f4.jpg",
            "http://f.hiphotos.baidu.com/image/pic/item/1c950a7b02087bf457dba03bf6d3572c11dfcf29.jpg",
            "http://a.hiphotos.baidu.com/image/pic/item/8d5494eef01f3a29fdfb221c9d25bc315c607c35.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/562c11dfa9ec8a13f075f10cf303918fa1ecc0eb.jpg",

            "http://f.hiphotos.baidu.com/image/pic/item/4e4a20a4462309f735600bfe760e0cf3d6cad6cb.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/cb8065380cd7912344a13298a9345982b3b7809d.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/83025aafa40f4bfba09238b8074f78f0f636189f.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/d788d43f8794a4c2474741c70af41bd5ac6e39f1.jpg",
            "http://b.hiphotos.baidu.com/image/pic/item/4a36acaf2edda3cc9de31ecc05e93901203f92d3.jpg",

    };
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(new ImageAdapter());
    }

    class ImageAdapter extends PagerAdapter {
        private ExecutorService newCachedThreadPool = Executors.newCachedThreadPool();


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            FrameLayout frameLayout = new FrameLayout(MainActivity.this);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            final ImageView imageView = new ImageView(MainActivity.this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(MainActivity.this).load(mImages[position % mImages.length]).asBitmap().dontAnimate().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    Log.i(TAG, "width:" + resource.getWidth() + ",height:" + resource.getHeight());
                    int scaleRatio = Math.max((resource.getWidth() * resource.getHeight()) / (1920 * 1080 / 2), 1);
                    final Bitmap bitmap = Bitmap.createScaledBitmap(resource, resource.getWidth() / scaleRatio, resource.getHeight() / scaleRatio, false);
                    newCachedThreadPool.execute(new Runnable() {
                        private int blurRadius = 25;
                        @Override
                        public void run() {
                            long startTime = System.currentTimeMillis();
                            final Bitmap target = NativeStackBlur.process(bitmap, blurRadius);
                            Log.i(TAG, "BlurTime:" + (System.currentTimeMillis() - startTime));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageView.setImageBitmap(target);
                                }
                            });
                        }
                    });
                }
            });
            frameLayout.addView(imageView);

            ImageView imgSmall = new ImageView(MainActivity.this);
            imgSmall.setLayoutParams(new ViewGroup.LayoutParams(240, 480));
            imgSmall.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Glide.with(MainActivity.this).load(mImages[position % mImages.length]).asBitmap().dontAnimate().into(imgSmall);
            frameLayout.addView(imgSmall);
            container.addView(frameLayout);
            return frameLayout;
        }

        @Override
        public int getCount() {
            //简单处理可以让viewpager可以一直滚动
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            object = null;
        }
    }
}
