package com.Senior_Proj_Fall_2015.Veterans_App_Navigation;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import android.app.Activity;
import android.os.Bundle;

import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapPane extends Activity implements GoogleMap.OnMarkerClickListener, View.OnClickListener, TextToSpeech.OnInitListener {

    private GoogleMap map;
    private Marker marker;
    private Marker curMarker;
    private MyMarker markers;
    private ArrayList<String> photos;
    private List<LatLng> listOfPoints;
    private Dialog dialog = null;
    private AlertDialog.Builder builder;
    private TextView loc;
    private TextView locInfo;
    private String input1;
    private String input2;
    private ImageButton picButton;
    private ImageButton speechButton;
    private ImageView image;
    private String mCurrentPhotoPath;
    private static File photoFile = null;
    private Button buttons[];
    private TextToSpeech myTTS;
    private int MY_DATA_CHECK_CODE = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private Button ok;
    private boolean picTaken;
    private Button ok_save;
    private LatLng currentLocation;
    private float zoomLevel = 16;
    public static Bitmap rotatedImage;
    private File curMap = null;
    private File[] fileList = MenuPage.listOfFiles();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity);
        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);
        markers = new MyMarker();
        photos = new ArrayList<>();
        listOfPoints = new ArrayList<>();
        //currentLocation = new LatLng(39.70815236, -75.1179564);
        currentLocation = new LatLng(getLocation().getLatitude(), getLocation().getLongitude());
        drawCurrentLocation(getLocation());
        try {
            String name = LoadRoute.getFileToLoad();
            loadFile(name);
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].getName().toLowerCase().equals(name.toLowerCase())) {
                    curMap = fileList[i];
                    break;
                }
            }
        }
        catch (NullPointerException e) {}
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        //send log to logCat for successful method
        return super.onCreateOptionsMenu(menu);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_marker:
                map.setOnMarkerClickListener(this);
                addDialog();
                break;
            case R.id.reset:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.clear_map_message)
                    .setTitle(R.string.clear_map_title);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        clearMap();
                        drawCurrentLocation(getLocation());
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.create();
                builder.show();
                break;
            case R.id.delete_map:
                final File[] listOF = MenuPage.listOfFiles();
                try {
                    if (listOF.length == 0) {
                        Toast.makeText(getApplicationContext(), R.string.no_maps_delete_error,
                            Toast.LENGTH_LONG).show();
                    }
                    if (listOF.length != 0) {
                        builder = new AlertDialog.Builder(this);
                        Context con = getApplicationContext();
                        LayoutInflater inf =
                            (LayoutInflater) con.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View deleteView = inf.inflate(R.layout.delete_map_dialog, null);
                        builder.setView(deleteView);
                        buttons = new Button[10];
                        buttons[0] = (Button) deleteView.findViewById(R.id.one);
                        buttons[1] = (Button) deleteView.findViewById(R.id.two);
                        buttons[2] = (Button) deleteView.findViewById(R.id.three);
                        buttons[3] = (Button) deleteView.findViewById(R.id.four);
                        buttons[4] = (Button) deleteView.findViewById(R.id.five);
                        buttons[5] = (Button) deleteView.findViewById(R.id.six);
                        buttons[6] = (Button) deleteView.findViewById(R.id.seven);
                        buttons[7] = (Button) deleteView.findViewById(R.id.eight);
                        buttons[8] = (Button) deleteView.findViewById(R.id.nine);
                        buttons[9] = (Button) deleteView.findViewById(R.id.ten);
                        for (int i = 0; i < buttons.length; i++) {
                            while (i < listOF.length) {
                                buttons[i].setText(listOF[i].getName());
                                i++;
                            }
                            buttons[i].setEnabled(false);
                            buttons[i].setVisibility(View.GONE);
                        }
                        for (int j = 0; j < buttons.length; j++) {
                            final int finalJ = j;
                            buttons[j].setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    if (buttons[finalJ].getText()
                                                       .equals(listOF[finalJ].getName())) {
                                        File toBeDeleted = listOF[finalJ];
                                        String mapName = "";
                                        if (toBeDeleted.equals(curMap)) {
                                            mapName = curMap.getName();
                                            clearMap();
                                        }
                                        String fileName = toBeDeleted.getName();
                                        boolean deleted = toBeDeleted.delete();
                                        System.out.println("deleted map:" + fileName
                                            + (mapName.equals("") ? "" : " and current map: " + mapName));
                                        Toast.makeText(getApplicationContext(),
                                            "File " + fileName + " was deleted.",
                                            Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                        dialog = builder.create();
                        dialog.show();
                    }
                }
                catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), R.string.no_maps_delete_error,
                        Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.save:
                builder = new AlertDialog.Builder(this);
                Context context = getApplicationContext();
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
                final View save_view = inflater.inflate(R.layout.save_dialog, null);
                builder.setView(save_view);
                final File[] allFiles = MenuPage.listOfFiles();
                ok_save = (Button) save_view.findViewById(R.id.ok_save);
                ok_save.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int filesLength;
                        try {
                            filesLength = allFiles.length;
                        }
                        catch (NullPointerException e) {
                            filesLength = 0;
                        }
                        if (filesLength < 10) {
                            EditText save = (EditText) save_view.findViewById(R.id.saveFileAs);
                            String saveInput = save.getText().toString();
                            try {
                                String root = Environment.getExternalStorageDirectory().toString();
                                File myDir = new File(root + "/saved_maps");
                                myDir.mkdirs();
                                File file = new File(myDir, saveInput + ".txt");
                                BufferedWriter bw = new BufferedWriter((new FileWriter(file, false)));
                                PrintWriter pw = new PrintWriter(bw, false);
                                int i = 0;
                                for (LatLng point : listOfPoints) {
                                    pw.print(point.latitude + "," + point.longitude + "\r\n");
                                    pw.print(markers.getLocName(i) + "\r\n");
                                    pw.print(markers.getLocInfo(i) + "\r\n");
                                    pw.print(photos.get(i) + "\r\n");
                                    i++;
                                    Log.v("write", point.latitude + "," + point.longitude);
                                }
                                pw.flush(); // Flush stream ...
                                pw.close(); // ... and close.
                                curMap = file;
                                System.out.println("current map " + curMap.getName());
                                Toast.makeText(getApplicationContext(), "File: " + saveInput + " was saved!",
                                    Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            } catch (IOException exc) {
                                exc.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.max_file_limit,
                                Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;
            case R.id.load:
                final File[] lof = MenuPage.listOfFiles();
                try {
                    if (lof.length == 0) {
                        Toast.makeText(getApplicationContext(), R.string.no_maps_load_error,
                            Toast.LENGTH_LONG).show();
                    }
                    if (lof.length != 0) {
                        builder = new AlertDialog.Builder(this);
                        Context c = getApplicationContext();
                        LayoutInflater in =
                            (LayoutInflater) c.getSystemService(LAYOUT_INFLATER_SERVICE);
                        View view = in.inflate(R.layout.load_dialog, null);
                        builder.setView(view);
                        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                ;//do nothing. Close the app.
                            }
                        });
                        buttons = new Button[10];
                        buttons[0] = (Button) view.findViewById(R.id.button1);
                        buttons[1] = (Button) view.findViewById(R.id.button2);
                        buttons[2] = (Button) view.findViewById(R.id.button3);
                        buttons[3] = (Button) view.findViewById(R.id.button4);
                        buttons[4] = (Button) view.findViewById(R.id.button5);
                        buttons[5] = (Button) view.findViewById(R.id.button6);
                        buttons[6] = (Button) view.findViewById(R.id.button7);
                        buttons[7] = (Button) view.findViewById(R.id.button8);
                        buttons[8] = (Button) view.findViewById(R.id.button9);
                        buttons[9] = (Button) view.findViewById(R.id.button10);

                        for (int i = 0; i < buttons.length; i++) {
                            while (i < lof.length) {
                                buttons[i].setText(lof[i].getName());
                                i++;
                            }
                            buttons[i].setEnabled(false);
                            buttons[i].setVisibility(View.GONE);
                        }

                        for (int j = 0; j < buttons.length; j++) {
                            final int finalJ = j;
                            buttons[j].setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    if (buttons[finalJ].getText().equals(lof[finalJ].getName())) {
                                        if (map != null) {
                                            clearMap();
                                        }
                                        loadFile(lof[finalJ].getName());
                                        curMap = lof[finalJ];
                                        System.out.println("loaded map:" + lof[finalJ].getName()
                                            + " and current map " + curMap.getName());
                                        dialog.dismiss();
                                    }
                                }
                            });
                        }
                        dialog = builder.create();
                        dialog.show();
                    }
                }
                catch (NullPointerException e) {
                    Toast.makeText(getApplicationContext(), R.string.no_maps_load_error,
                        Toast.LENGTH_LONG).show();
                }
                break;
        }
        return true;
    }

    private GoogleMap.OnMyLocationChangeListener myLocationChangeListener = new GoogleMap.OnMyLocationChangeListener() {
        @Override
        public void onMyLocationChange(Location location) {
            drawCurrentLocation(location);
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());

        }
    };

    public Location getLocation() {
        MapFragment fm = (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        // Getting GoogleMap object from the fragment
        map = fm.getMap();

        // Enabling MyLocation Layer of Google Map
        map.setMyLocationEnabled(true);

        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            private float currentZoom = -1;

            @Override
            public void onCameraChange(CameraPosition pos) {
                if (pos.zoom != currentZoom) {
                    currentZoom = pos.zoom;
                    zoomLevel = currentZoom;
                    try {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel));
                    } catch (NullPointerException e) {
                    }
                }
            }
        });

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider
        Criteria criteria = new Criteria();

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null) {
            location = new Location(LOCATION_SERVICE);
            location.setLatitude(39.70815236);
            location.setLongitude(-75.1179564);
        }

        map.setOnMyLocationChangeListener(myLocationChangeListener);
        return location;
    }

    public void addDialog() {
        builder = new AlertDialog.Builder(this);
        final Context context = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.marker_dialog, null);
        picTaken = false;
        picButton = (ImageButton) layout.findViewById(R.id.map_imageButton);
        picButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });
        builder.setView(layout);
        ok = (Button) layout.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
                                  public void onClick(View v) {
                                      EditText inputLoc = (EditText) layout.findViewById(R.id.location_name);
                                      EditText inputInfo = (EditText) layout.findViewById(R.id.location_info);
                                      input1 = inputLoc.getText().toString();
                                      input2 = inputInfo.getText().toString();
                                      if (input1.equals("")) {
                                          Toast.makeText(getApplicationContext(), R.string.loc_error,
                                              Toast.LENGTH_LONG).show();
                                      } else if (input2.equals("")) {
                                          Toast.makeText(getApplicationContext(), R.string.info_error,
                                              Toast.LENGTH_LONG).show();
                                      } else if (picTaken == false) {
                                          Toast.makeText(getApplicationContext(), R.string.pic_error,
                                              Toast.LENGTH_LONG).show();
                                      } else {
                                          LatLng cur = currentLocation;
                                          listOfPoints.add(cur);
                                          marker = map.addMarker(new MarkerOptions()
                                                  .position(cur));
                                          map.moveCamera(CameraUpdateFactory.newLatLngZoom(cur, zoomLevel));
                                          markers.add(marker, input1, input2);
                                          photos.add(mCurrentPhotoPath);
                                          curMarker.remove();
                                          dialog.cancel();
                                      }
                                  }
                              }

        );
        dialog = builder.create();
        dialog.show();
    }



    @Override
    public boolean onMarkerClick(final Marker m) {

        if (m.equals(markers.getId(m))) {
            builder = new AlertDialog.Builder(this);
            Context context = getApplicationContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
            View layout = inflater.inflate(R.layout.marker_info, null);
            builder.setView(layout);
            loc = (TextView) layout.findViewById(R.id.loc_name);
            locInfo = (TextView) layout.findViewById(R.id.loc_info);
            loc.setText(markers.getLocName(markers.getCount(m)));
            locInfo.setText(markers.getLocInfo(markers.getCount(m)));
            String path = photos.get(markers.getCount(m));
            image = (ImageView) layout.findViewById(R.id.location_imageView);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            int imageHeight = options.outHeight;
            int imageWidth = options.outWidth;
            String imageType = options.outMimeType;
            if (imageWidth > imageHeight) {
                options.inSampleSize = calculateInSampleSize(options, 512, 256);//if     landscape
            } else {
                options.inSampleSize = calculateInSampleSize(options, 256, 512);//if     portrait
            }
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(path, options);
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            rotatedImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            image.setImageBitmap(rotatedImage);
            image.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent intent = new Intent(MapPane.this, ImageActivity.class);
                    startActivity(intent);
                }
            });
            speechButton = (ImageButton) layout.findViewById(R.id.speech);
            speechButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    LatLng temp = listOfPoints.get(markers.getCount(m));
                    Location locDes = new Location(LocationManager.GPS_PROVIDER);
                    locDes.setLatitude(temp.latitude);
                    locDes.setLongitude(temp.longitude);
                    float distance = getDistanceBetween(locDes);
                    double miles = (distance * 0.000621371192);
                    DecimalFormat df = new DecimalFormat("#.##");
                    df.setRoundingMode(RoundingMode.CEILING);
                    String dist = df.format(miles);
                    if (Double.parseDouble(dist) >= 1) {
                        speakWords("You are " + dist + " miles away from " + markers.getLocName(markers.getCount(m)));
                    } else if (Double.parseDouble(dist) <= 0.1) {
                        speakWords("You are at this location");
                    } else {
                        speakWords("You are less than one mile away from " + markers.getLocName(markers.getCount(m)));
                    }
                }
            });
            builder.setPositiveButton(R.string.ok, null);
            dialog = builder.create();
            dialog.show();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MapPane.this, CameraActivity.class);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                    Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                picTaken = true;
            }
        }
    }

    public static File getPhotoFile() {
        return photoFile;
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = "JPEG_" + timeStamp + ".jpg";
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/saved_files");
        myDir.mkdirs();
        File image = new File(myDir, filename);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void clearMap() {
        map.clear();
        markers = new MyMarker();
        photos = new ArrayList<>();
        listOfPoints = new ArrayList<>();
        drawCurrentLocation(getLocation());
        marker = null;
    }

    public void loadFile(String filename) {
        try {
            String currentLine;
            String root = Environment.getExternalStorageDirectory().toString();
            String filepath = root + "/saved_maps/";
            BufferedReader br = new BufferedReader(new FileReader(filepath + filename));
            int count = 1;
            String tempLocName = "";
            String tempLocInfo = "";
            String tempPath = "";
            while ((currentLine = br.readLine()) != null) {
                if ((count % 4) == 1) {
                    String[] latlng = currentLine.split(",");
                    double lat = Double.parseDouble(latlng[0]);
                    double lng = Double.parseDouble(latlng[1]);
                    LatLng pos = new LatLng(lat, lng);
                    listOfPoints.add(pos);
                    map.setOnMarkerClickListener(this);
                    marker = map.addMarker(new MarkerOptions()
                        .position(pos));
                } else if ((count % 4) == 2) {
                    tempLocName = currentLine;
                } else if ((count % 4) == 3) {
                    tempLocInfo = currentLine;
                } else if ((count % 4) == 0) {
                    tempPath = currentLine;
                    markers.add(marker, tempLocName, tempLocInfo);
                    photos.add(tempPath);
                    curMarker.remove();
                }
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void speakWords(String s) {
        String speech = "This is a test speech output.";
        //speak straight away
        myTTS.speak(s, TextToSpeech.QUEUE_ADD, null);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                //the user has the necessary data - create the TTS
                myTTS = new TextToSpeech(this, this);
            } else {
                //no data - install it now
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    @Override
    public void onInit(int initStatus) {

        //check for successful instantiation
        if (initStatus == TextToSpeech.SUCCESS) {
            if (myTTS.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_AVAILABLE)
                myTTS.setLanguage(Locale.US);
        } else if (initStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Sorry! Text To Speech failed...", Toast.LENGTH_LONG).show();
        }
    }

    public float getDistanceBetween(Location l) {
        return l.distanceTo(getLocation());
    }

    public void drawCurrentLocation(Location l) {
        double lat = l.getLatitude();
        double lng = l.getLongitude();
        System.out.println("lat: " + lat + "lng: " + lng);
        LatLng latLng = new LatLng(lat, lng);
        if (curMarker != null) {
            curMarker.remove();
        }
        curMarker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                .title("Current Position")
                .snippet("Lat: " + lat + " " + "Lng: " + lng));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));

    }

    public static int calculateInSampleSize(
        BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and     width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will     guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}