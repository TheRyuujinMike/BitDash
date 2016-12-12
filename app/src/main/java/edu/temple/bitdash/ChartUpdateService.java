package edu.temple.bitdash;

import android.app.IntentService;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ChartUpdateService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_GETCHART = "edu.temple.bitdash.action.GETCHART";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "edu.temple.bitdash.extra.days";

    public ChartUpdateService() {
        super("ChartUpdateService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionUpdateChart(Context context) {
        Intent intent = new Intent(context, ChartUpdateService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {

            Timer timer = new Timer();



            TimerTask task = new TimerTask() {
                @Override
                public void run() {

                    handleActionUpdateChart();

                }
            };

            timer.scheduleAtFixedRate(task, 0, (1000 * 15));

        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateChart() {

        //Log.d("Service", "Service was called.");

        Bitmap image = null;

        URL imageUrl1d = null;
        URL imageUrl5d = null;

        try {
                imageUrl1d = new URL("http://chart.yahoo.com/z?s=BTCUSD=X&t=1d");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


        try {
                imageUrl5d = new URL("http://chart.yahoo.com/z?s=BTCUSD=X&t=5d");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

        try {
            image = BitmapFactory.decodeStream(((InputStream) imageUrl1d.getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream outStream = null;

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory,"chart_1d.png");

        try {
            outStream = new FileOutputStream(myPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (image != null) {

            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);

        }

        try {
            image = BitmapFactory.decodeStream(((InputStream) imageUrl5d.getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        myPath = new File(directory,"chart_5d.png");

        try {
            outStream = new FileOutputStream(myPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (image != null) {

            image.compress(Bitmap.CompressFormat.PNG, 100, outStream);

        }

        try {
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000 * 15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        handleActionUpdateChart();

    }

}
