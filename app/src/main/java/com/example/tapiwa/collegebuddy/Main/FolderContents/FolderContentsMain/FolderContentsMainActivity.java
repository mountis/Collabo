package com.example.tapiwa.collegebuddy.Main.FolderContents.FolderContentsMain;


        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Color;
        import android.net.Uri;
        import android.os.Environment;
        import android.os.Vibrator;
        import android.support.design.widget.FloatingActionButton;
        import android.support.design.widget.TabLayout;
        import android.support.v4.view.MenuItemCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.support.v7.widget.SearchView;
        import android.support.v7.widget.Toolbar;


        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.FragmentPagerAdapter;
        import android.support.v4.view.ViewPager;
        import android.os.Bundle;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.Toast;

        import com.eightbitlab.bottomnavigationbar.BottomBarItem;
        import com.eightbitlab.bottomnavigationbar.BottomNavigationBar;
        import com.example.tapiwa.collegebuddy.Analytics.AppUsageAnalytics;
        import com.example.tapiwa.collegebuddy.R;
        import com.example.tapiwa.collegebuddy.CameraGalleryUploads.CameraGalleryUpload;
        import com.example.tapiwa.collegebuddy.Main.FolderContents.Images.ImagesFragment;
        import com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NoteStackItem;
        import com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NotesListAdapter;
        import com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.StackCardsActivity;
        import com.example.tapiwa.collegebuddy.Main.FolderContents.StackImages.StackCardsImages;
        import com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NewNote;
        import com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NotesFragment;
        import com.example.tapiwa.collegebuddy.Miscellaneous.GenericMethods;
        import com.facebook.FacebookSdk;
        import com.facebook.appevents.AppEventsLogger;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.auth.FirebaseUser;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.iid.FirebaseInstanceId;
        import com.google.firebase.messaging.FirebaseMessaging;
        import com.google.firebase.storage.FirebaseStorage;
        import com.google.firebase.storage.StorageReference;

        import java.io.File;
        import java.io.IOException;
        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Date;

        import cn.pedant.SweetAlert.SweetAlertDialog;

        import static com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NotesFragment.dbHelper;
        import static com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NotesFragment.notesList;
        import static com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NotesFragment.listview;
        import static com.example.tapiwa.collegebuddy.Main.FolderContents.Notes.NotesFragment.notesAdapter;


public class FolderContentsMainActivity extends AppCompatActivity {

    private ViewPagerAdapter mViewPagerAdapter;
    private ViewPager mViewPager;
    private FirebaseUser mCurrentUser;
    public static String uid, className, projectKey;
    public static FloatingActionButton actionButton, stackCardsfab;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth mAuth;
    public static StorageReference mPrivateFullImageStorageRef;
    public static StorageReference privateThumbNailsStorageRef;
    public static DatabaseReference mPrivateFullImageDatabaseRef;
    public static DatabaseReference mFolderPropertiesDBRef;

    private Menu mMenu;
    private SearchView searchView;
    private FirebaseStorage mStorage;
    public static Activity activity;

    public static final String PRIVATE_FOLDERS_CONTENTS = "Private_Folders_Contents";
    public static String PRIVATE_IMAGES_THUMBNAILS = "Private_Images_Thumbnails";
    public static final String PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH = "Private_Folders_Photos";
    private final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1002;
    private final int SELECT_FILE_FROM_SYSTEM = 1236;

    private final int PICK_IMAGE = 1001;
    private final int REQUEST_IMAGE_CAPTURE = 1;

    private ProgressDialog mProgress;
    private String image_tag, thumb_download_url, user;
    public static File photoFile = null;
    public static Uri resultfileUri;
    public static File thumb_file_path;
    private Vibrator vibrate;
    private int pageNumber = 0;
    private BottomNavigationBar bottomNavigationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_contents);

        className = getIntent().getStringExtra("projectName");
        projectKey = getIntent().getStringExtra("projectKey");
        activity = this;

        AppUsageAnalytics.incrementPageVisitCount(getString(R.string.images_fragment));

        initializeViews();
        firebaseInitialization();
        initializeListeners();

        //Connect to Facebook analytics
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        //Subscribe to topic and get token from firebase
        FirebaseMessaging.getInstance().subscribeToTopic("test");
        FirebaseInstanceId.getInstance().getToken();

    }

    private void initializeViews() {

        //toolbar setup
        Toolbar toolbar = (Toolbar) findViewById(R.id.class_contents_toolbar);
        toolbar.setTitle(className);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //viewpager setup
        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mViewPagerAdapter);

        //floatingBtn
        actionButton = (FloatingActionButton) findViewById(R.id.fragment_action);
        actionButton.setImageResource(R.drawable.fab_add);
        actionButton.show();


        //bottom navigation
        bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.class_contents_bottom_bar);
        BottomBarItem cameraIcon = new BottomBarItem(R.drawable.ic_photo_cameraa);
        bottomNavigationBar.addTab(cameraIcon);



        //tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.WHITE);

        vibrate = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
        mProgress = new ProgressDialog(getApplicationContext());

    }

    private void initializeListeners() {

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0) {
                    //privates fragment
                    actionButton.setImageResource(R.drawable.ic_perm_media_white_24px);
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            CameraGalleryUpload.chooseImageFromGallery(FolderContentsMainActivity.this);
                        }
                    });
                    actionButton.show();
                    pageNumber = 0;
                }

                if (position == 1) {
                    //my notes fragment
                    actionButton.setImageResource(R.drawable.ic_note_add_white_24px);
                    actionButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent writeNote = new Intent(getApplicationContext(), NewNote.class);
                            startActivity(writeNote);
                        }
                    });
                    actionButton.show();
                    pageNumber = 1;
                }

                switch (position) {
                    case 0:
                        AppUsageAnalytics.incrementPageVisitCount("Images_Fragment");
                    case 1:
                        AppUsageAnalytics.incrementPageVisitCount("Notes_Fragment");
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationBar.setOnReselectListener(new BottomNavigationBar.OnReselectListener() {
            @Override
            public void onReselect(int position) {
                switch (position) {
                    case 0:
                        CameraGalleryUpload.takePicture(FolderContentsMainActivity.this,
                                "ClassContentsMainActivity");
                        break;
                }
            }
        });

        bottomNavigationBar.setOnSelectListener(new BottomNavigationBar.OnSelectListener() {
            @Override
            public void onSelect(int position) {

                switch (position) {
                    case 0:
                        CameraGalleryUpload.takePicture(FolderContentsMainActivity.this,
                                "ClassContentsMainActivity");
                        break;
                }

            }
        });



    }

    private void chooseFileToUpload() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    SELECT_FILE_FROM_SYSTEM);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }




    private void openStackCardsNotes() {
        String classname = FolderContentsMainActivity.className;
        ArrayList<NoteStackItem> noteCards = new ArrayList<>();

        for(String str : notesList) {
            String noteContents = dbHelper.getNoteContents(classname, str);
            String noteColor = dbHelper.getNoteColor(classname,str);
            NoteStackItem noteStackItem = new NoteStackItem(str,noteContents,noteColor);
            noteCards.add(noteStackItem);
        }

        Intent intent = new Intent(FolderContentsMainActivity.this, StackCardsActivity.class);
        Bundle args = new Bundle();
        args.putSerializable("ARRAYLIST", noteCards);
        intent.putExtra("BUNDLE",args);
        startActivity(intent);
    }

    public void openImagesStackCards() {
        Intent openStackCards = new Intent(getApplicationContext(), StackCardsImages.class);
        startActivity(openStackCards);
    }

    private void firebaseInitialization() {
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        user = mAuth.getCurrentUser().getUid().toString();
        mStorage = FirebaseStorage.getInstance();

        mPrivateFullImageStorageRef = mStorage
                .getReference(PRIVATE_FOLDER_CONTENTS_IMAGE_STORAGE_PATH)
                .child(user);
        privateThumbNailsStorageRef = FirebaseStorage
                .getInstance()
                .getReference()
                .child(PRIVATE_IMAGES_THUMBNAILS)
                .child(user)
                .child(projectKey);

        mFolderPropertiesDBRef = FirebaseDatabase.getInstance().getReference(PRIVATE_FOLDERS_CONTENTS)
                .child(user)
                .child(projectKey);

        mPrivateFullImageDatabaseRef = FirebaseDatabase
                .getInstance()
                .getReference(PRIVATE_FOLDERS_CONTENTS)
                .child(user)
                .child(projectKey);

        mPrivateFullImageDatabaseRef.keepSynced(true);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        CameraGalleryUpload uploadImages = new CameraGalleryUpload(projectKey);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            uploadImages.connectFirebaseCloud(getString(R.string.images_fragment));
            uploadImages.attemptImageUpload(photoFile,
                    resultfileUri,
                    getApplicationContext(),
                    this.getString(R.string.images_fragment));
            return;
        }

        if(requestCode == SELECT_FILE_FROM_SYSTEM) {
            GenericMethods.uploadFiletoFireBase(data.getData(),
                    FolderContentsMainActivity.projectKey,
                    getApplicationContext());
        }

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                photoFile = createImageFile();
                resultfileUri = data.getData();

                uploadImages.galleryImage = true;
                uploadImages.connectFirebaseCloud(getString(R.string.images_fragment));
                uploadImages.attemptImageUpload(photoFile,
                        resultfileUri,
                        getApplicationContext(),
                        getString(R.string.images_fragment));

            } catch (Exception e) {
            }
        }
    }

    public  void search(String title) {

        //if current focus is on the images fragment
        if(pageNumber == 0) {
            //search image in firebase
            ImagesFragment.searchImage(title);
            return;
        }

        //if current focus is on the notesFragment
        if(pageNumber == 1) {
            //search note in notes sqlite database
            notesList.clear();
            AppUsageAnalytics.incrementPageVisitCount("Search_Note");

            if (title.equals("")) {
                notesList = dbHelper.getAllTitles(className);
                Collections.reverse(notesList);
            } else {
                notesList = dbHelper.searchNote(className, title);
            }

            notesAdapter = new NotesListAdapter(getApplicationContext(),
                    R.layout.item_note_list,
                    notesList,
                    className);

            listview.setAdapter(notesAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.class_contents_menu, menu);


        MenuItem searchItem = menu.findItem(R.id.class_contents_search);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

         searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

           @Override
            public boolean onQueryTextChange(String query) {
               search(query);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Main/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        //// TODO: 8/1/17 Change these settings to custom settings


        if (id == R.id.revision_cards) {
            openStackCardsNotes();
        }

        if (id == R.id.revision_images) {
          openImagesStackCards();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInfomation(int pageNumber) {
        if (pageNumber == 0) {
            imagesPageDialogueInformation();
            AppUsageAnalytics.incrementPageVisitCount("Images_Fragment_Information");
        } else if (pageNumber == 1) {
            notesPageDialogueInformation();
            AppUsageAnalytics.incrementPageVisitCount("Notes_Fragment_Information");
        }
            return;
    }

    private void imagesPageDialogueInformation() {
        SweetAlertDialog sdg = new SweetAlertDialog(FolderContentsMainActivity.this,
                SweetAlertDialog.NORMAL_TYPE);

        sdg.setTitleText("Usage info");
        sdg.setContentText(getResources().getString(R.string.images_page_information));
        sdg.setCancelable(true);
        sdg.show();
    }


    private void notesPageDialogueInformation() {

        SweetAlertDialog sdg = new SweetAlertDialog(FolderContentsMainActivity.this,
                SweetAlertDialog.NORMAL_TYPE);

        sdg.setTitleText("Usage info");
        sdg.setContentText(getResources().getString(R.string.notes_page_information));
        sdg.setCancelable(true);
        sdg.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // do your stuff
                } else {
                    Toast.makeText(FolderContentsMainActivity.this, "Denied Access",
                            Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onBackPressed() {

        if(!searchView.isIconified()) {
        searchView.onActionViewCollapsed();
            return;
        } else if(pageNumber > 0) {
          mViewPager.setCurrentItem(0);
            return;
        }

        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0 :
                    // PrivatesFragment privatesFragment = new PrivatesFragment();
                    ImagesFragment imagesFragment = new ImagesFragment();
                    return imagesFragment;
                case 1:
                    NotesFragment notesFragment = new NotesFragment();
                    return notesFragment;

                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SNAPS";
                case 1:
                    return "NOTE CARDS";
            }
            return null;
        }
    }

}
