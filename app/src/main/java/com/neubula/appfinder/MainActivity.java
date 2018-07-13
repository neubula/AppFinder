package com.neubula.appfinder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends Activity {
	
//	private static final String TAG = "Main Activity--";
	

	public Context context;

	@BindView(R.id.div_mic)
	RelativeLayout div_mic;
	@BindView(R.id.editTextApp)
	EditText editTextApp;
	@BindView(R.id.listViewApp)
	RecyclerView listViewApp;

	AppListAdapter appListAdapter;
	

	PackageManager pm;
	//get a list of installed apps.
	public static List<ApplicationInfo> allPackages;
	public List<ApplicationInfo> packages;
	
    private final int REQ_CODE_SPEECH_INPUT = 100;
	private final String APP_PREFERENCE = "com.neubula.appPref";
	private final String IS_ICON_CREATED = "IS_ICON_CREATED";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		ButterKnife.bind(this);
		
		context = MainActivity.this;

		/*if(!getSharedPreferences(APP_PREFERENCE, Activity.MODE_PRIVATE).getBoolean(IS_ICON_CREATED, false)){
			addShortcut();
			getSharedPreferences(APP_PREFERENCE, Activity.MODE_PRIVATE).edit().putBoolean(IS_ICON_CREATED, true).commit();
		}*/

		pm = getPackageManager();
		allPackages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
		packages = new LinkedList<>();

		intializePackage();
		
//		div_mic = (RelativeLayout) findViewById(R.id.div_mic);
//		editTextApp = (EditText) findViewById(R.id.editTextApp);
//		listViewApp = (ListView) findViewById(R.id.listViewApp);

//		Intent intent = new Intent(Intent.ACTION_DELETE);
//		intent.setData(Uri.parse("package:com.example.mypackage"));
//		startActivity(intent);

		appListAdapter = new AppListAdapter(context, packages);

		listViewApp.setLayoutManager(new GridLayoutManager(context,1));

		listViewApp.setHasFixedSize(false);

		listViewApp.setAdapter(appListAdapter);

	}
	
	@Override
	protected void onResume() {
		super.onResume();

//		listViewApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//				ApplicationInfo packageInfo = packages.get(position);
//
//				if (packageInfo != null) {
//					if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
//						Intent intent = pm.getLaunchIntentForPackage(packageInfo.packageName);
//						startActivity(intent);
//					} else {
//						Toast.makeText(context, "App will not start. No name.", Toast.LENGTH_SHORT).show();
//					}
//				} else {
//					Toast.makeText(context, "App will not start. No package.", Toast.LENGTH_SHORT).show();
//				}
//			}
//		});

		editTextApp.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
//				 When user changed the Text
				 appListAdapter.getFilter().filter(cs);

//				List<ApplicationInfo> newPackages = new LinkedList<ApplicationInfo>();
				/*for (ApplicationInfo packageInfo : allPackages) {
					if (!packages.contains(packageInfo))
						packages.add(packageInfo);
				}*/

//				intializePackage();
//
//				ListIterator<ApplicationInfo> litr = packages.listIterator();
//				while(litr.hasNext()) {
//					ApplicationInfo packageInfo = litr.next();
//					if (pm.getApplicationLabel(packageInfo) != null) {
//						String dataConstraint = cs.toString();
//						String data = pm.getApplicationLabel(packageInfo).toString();
//						if (!data.toLowerCase().startsWith(dataConstraint)) {
//							litr.remove();
//						}
//					}
//				}

				/*for (ApplicationInfo packageInfo : packages) {

				}*/

				/*if (newPackages.size() > 0) {
					packages = newPackages;
				} else {
					packages = allPackages;
				}*/

//				if (appListAdapter == null) {
//					appListAdapter = new AppListAdapter(context, packages);
//					listViewApp.setAdapter(appListAdapter);
//				} else {
//					appListAdapter.notifyDataSetChanged();
//				}

//				Toast.makeText(context, cs.toString(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
										  int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	private void intializePackage() {

		for (ApplicationInfo packageInfo : allPackages) {
			if (!packages.contains(packageInfo)) {
				packages.add(packageInfo);
			}
		}
	}

	private void addShortcut() {
		//Adding shortcut for MainActivity
		//on Home screen
		Intent shortcutIntent = new Intent(getApplicationContext(),
				MainActivity.class);

		shortcutIntent.setAction(Intent.ACTION_MAIN);

		Intent addIntent = new Intent();
		addIntent
				.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(getApplicationContext(),
						R.drawable.ic_launcher));

		addIntent
				.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		getApplicationContext().sendBroadcast(addIntent);
	}
	
	/**
     * Showing google speech input dialog
     * */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak app name");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Speach not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }
 
    /**
     * Receiving speech input
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
 
        switch (requestCode) {
        case REQ_CODE_SPEECH_INPUT: {
            if (resultCode == RESULT_OK && null != data) {
 
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                new StartIntentTask(context, result).execute();
            }
            break;
        }
 
        }
    }
    
    private class StartIntentTask extends AsyncTask<Void, Void, Intent> {
    	
    	Context context;
    	ArrayList<String> activity_names;
    	ProgressDialog pd;
    	
    	public StartIntentTask(Context context, ArrayList<String> activity_names) {
    		this.context = context;
    		this.activity_names = activity_names;
    	}
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		pd = new ProgressDialog(context);
    		pd.setMessage("Processing...");
    		pd.show();
    	}
    	
    	@Override
    	protected void onPostExecute(Intent result) {
    		super.onPostExecute(result);
    		if (pd != null)
    			pd.cancel();
    		if (result != null) {
    			startActivity(result);
    		} else {
    			Toast.makeText(context, "Sorry !!! Unable to find your app. Please try again", Toast.LENGTH_LONG).show();
    		}
    	}
    	
    	@Override
    	protected Intent doInBackground(Void... params) {
    		for (ApplicationInfo packageInfo : packages) {
    			if (pm.getApplicationLabel(packageInfo) != null) {
    				for (String activity_name : activity_names) {
    					if (((String) pm.getApplicationLabel(packageInfo)).equalsIgnoreCase(activity_name)) {
    						if (pm.getLaunchIntentForPackage(packageInfo.packageName) != null) {
    							return pm.getLaunchIntentForPackage(packageInfo.packageName);
    						}
    					}
    				}
    				
    			}
    		}
    		return null;
    	}
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
