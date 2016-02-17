package jam.mbarakat.com.myshares.anim;

import android.animation.ObjectAnimator;
import android.support.v7.widget.RecyclerView;

/**
 * Created by MBARAKAT on 2/16/2016.
 */
public class AnimationUtils {
    public static void animate(RecyclerView.ViewHolder holder, boolean goesDown){
        ObjectAnimator animatorTranslationY = ObjectAnimator.ofFloat(holder.itemView,"translationY"
                ,goesDown?300:-300
                ,0);
        animatorTranslationY.setDuration(1000);
        animatorTranslationY.start();
    }
}
