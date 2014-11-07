package mx.org.dabicho.mygallery.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlend;
import android.renderscript.ScriptIntrinsicBlur;
import android.renderscript.ScriptIntrinsicResize;

/**
 *
 */
public class RenderScriptUtils {
    private RenderScript mRenderScript;
    private Context mContext;
    private static RenderScriptUtils mRenderScriptUtils;

    private RenderScriptUtils(Context context) {
        mContext=context;
        mRenderScript=RenderScript.create(context);
    }

    public synchronized static RenderScriptUtils getInstance(Context context){
        if(mRenderScriptUtils==null)
            mRenderScriptUtils=new RenderScriptUtils(context);

        return mRenderScriptUtils;
    }


    public Bitmap blurBitmap(Bitmap src){
        Bitmap blurredBitmap = Bitmap.createBitmap(src.getWidth(),src.getHeight(),src.getConfig());

        ScriptIntrinsicBlur lIntrinsicBlur=ScriptIntrinsicBlur.create(mRenderScript, Element.U8_4(mRenderScript));
        Allocation tmpIn =Allocation.createFromBitmap(mRenderScript,src);
        Allocation tmpOut=Allocation.createFromBitmap(mRenderScript,blurredBitmap);

        lIntrinsicBlur.setRadius(25F);
        lIntrinsicBlur.setInput(tmpIn);
        lIntrinsicBlur.forEach(tmpOut);
        tmpOut.copyTo(blurredBitmap);
        src.recycle();

        return blurredBitmap;
    }
}
