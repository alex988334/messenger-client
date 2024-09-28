package com.orion.messenger;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.orion.messenger.databinding.ActivityMainBinding;
import com.orion.messenger.ui.chat.ChatHandler;
import com.orion.messenger.ui.chat.ChatHandlerProperty;
import com.orion.messenger.ui.chat.ChatViewModel;
import com.orion.messenger.ui.chat.ConstWSThread;
import com.orion.messenger.ui.chat.IGenerateUI;
import com.orion.messenger.ui.chat.ResponseServer;
import com.orion.messenger.ui.data.IModel;
import com.orion.messenger.ui.message.MessageViewModel;
import com.orion.messenger.ui.settings.SettingsHandler;
import com.orion.messenger.ui.user.RegistrationFragment;
import com.orion.messenger.ui.user.RegistrationViewModel;

import java.security.PrivateKey;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements IGenerateUI {

    public static MainActivity activvity;

    private SettingsHandler settingsHandler;
    public ChatHandler chat;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activvity = this;

        createChatHandler();
        settingsHandler = new SettingsHandler(getApplicationContext());
        chat.setSettings(settingsHandler.readSettingsFile());

        startChatHandler();

      /*  ChatHandlerProperty s = new ChatHandlerProperty();
        s.authKey = "qwerty";
        s.password = "AlbatroS";
        s.userId = 5;
        s.login = "Max";
        s.alias = "Mad Max";
        s.currentChat = 6;
        s.currentMessage = 7;

        Gson g = new Gson();
        String data = g.toJson(s);

        settingsHandler.writeSettingsToFile(s);

        ChatHandlerProperty readed = settingsHandler.readSettingsFile();

        String dataReaded = g.toJson(readed);

        Log.d(ConstWSThread.LOG_TAG, "Required: " + data + "\n" +
            "Rezult:  " + dataReaded + "\nEquals data: " + data.equals(dataReaded));
*/

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);
   //     плавающая кнопка добавления
      /*  binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });*/
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_chat, R.id.nav_about_user, R.id.nav_autorizate,
                R.id.nav_registration, R.id.nav_settings)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {

                int dest = navDestination.getId();

                if (dest == R.id.nav_chat) {
                    chat.getListChats();
                }
            }
        });

        Log.e(ConstWSThread.LOG_TAG, "<<<<<<<<ONCREATE QUIT");
    }



    private void ResetUserData() {
        chat.setSettings(new ChatHandlerProperty());
    }

    public void showMessage(String msg){
        Log.e(ConstWSThread.LOG_TAG, msg);
        Snackbar.make(binding.navView, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
      //  ResetUserData();
    //    startChatHandler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(ConstWSThread.LOG_TAG, "------>ONRESUME<-----");
  //      startChatHandler();

    }

    private void createChatHandler() {
        chat = new ChatHandler(this);
    }

    @Override
    protected void onStop() {
        settingsHandler.writeSettingsToFile(chat.getSettings());
        super.onStop();
    }

    public void showMessages(ResponseServer responseServer){
        MessageViewModel viewModel = new ViewModelProvider(this).get(MessageViewModel.class);

        viewModel.setResponseServer(responseServer);
    }

    public void showChats(ResponseServer responseServer){
        ChatViewModel viewModel = new ViewModelProvider(this).get(ChatViewModel.class);
        viewModel.setResponseServer(responseServer);
    }

    private void startChatHandler() {
        if (chat == null) {
            createChatHandler();
        }
        chat.startHandler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void createChatUi() {
        navController.navigate(R.id.nav_chat);
    }

    @Override
    public void createAutorizateUI() {
        navController.navigate(R.id.nav_autorizate);
    }

    @Override
    public void createRegisterUI() {
        navController.navigate(R.id.nav_registration);
    }
}