package com.parse.starter;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.util.List;

public class UserStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_feed);

        Button btn = (Button) findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                String activeUsername = intent.getStringExtra("username");
                setTitle(activeUsername + "'s Smile");

                ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Image");
                query.whereEqualTo("username", activeUsername);
                query.orderByDescending("createdAt");
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if (e == null) {
                            if (objects.size() > 0) {
                                ParseObject object = objects.get(0);
                                ParseFile file = (ParseFile) object.get("image");
                                file.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                    if (e == null && data != null) {
                                        ImageView myImageView = (ImageView) findViewById(R.id.imgview);


                                        BitmapFactory.Options options = new BitmapFactory.Options();
                                        options.inMutable = true;

                                        //                Bitmap myBitmap = BitmapFactory.decodeResource(
                                        //                        getApplicationContext().getResources(),
                                        //                        R.drawable.face,
                                        //                        options);

                                        Bitmap myBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                                        Paint myRectPaint = new Paint();
                                        myRectPaint.setStrokeWidth(5);
                                        myRectPaint.setColor(Color.RED);
                                        myRectPaint.setStyle(Paint.Style.STROKE);

                                        Paint paintFont = new Paint();
                                        paintFont.setColor(Color.RED);
                                        paintFont.setTextSize(60);

                                        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                                        Canvas tempCanvas = new Canvas(tempBitmap);
                                        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                                        FaceDetector faceDetector = new
                                                FaceDetector.Builder(getApplicationContext())
                                                .setTrackingEnabled(false)
                                                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                                                .build();
//                                            if (!faceDetector.isOperational()) {
//                                                new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
//                                                return;
//                                            }

                                        Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                                        SparseArray<Face> faces = faceDetector.detect(frame);

                                        for (int i = 0; i < faces.size(); i++) {
                                            Face thisFace = faces.valueAt(i);
                                            float x1 = thisFace.getPosition().x;
                                            float y1 = thisFace.getPosition().y;
                                            float x2 = x1 + thisFace.getWidth();
                                            float y2 = y1 + thisFace.getHeight();
                                            tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);

                                            double smile = thisFace.getIsSmilingProbability();
                                            tempCanvas.drawText(String.format("Smile: %.2f", smile), x1, y2, paintFont);

                                        }
                                        myImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
                                    }
                                    }
                                });
                            }
                        }
                    }
                });
            }
        });
    }
}




