package com.orion.messenger;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;


import com.google.android.material.snackbar.Snackbar;
import com.orion.messenger.databinding.ActivityMainBinding;
import com.orion.messenger.ui.about.AboutProjectFragment;
import com.orion.messenger.ui.about.AboutProjectViewModel;
import com.orion.messenger.ui.chat.ChatFragment;
import com.orion.messenger.ui.chat.ChatHandler;
import com.orion.messenger.ui.chat.ChatHandlerProperty;
import com.orion.messenger.ui.chat.ChatViewModel;
import com.orion.messenger.ui.chat.ChatsContainerFragment;
import com.orion.messenger.ui.chat.ConstWSThread;
import com.orion.messenger.ui.chat.ISupportOperation;
import com.orion.messenger.ui.chat.IGetterViewModels;
import com.orion.messenger.ui.chat.ResponseServer;
import com.orion.messenger.ui.data.Message;
import com.orion.messenger.ui.data.User;
import com.orion.messenger.ui.message.MessageFragment;
import com.orion.messenger.ui.message.MessageViewModel;
import com.orion.messenger.ui.settings.SettingsHandler;
import com.orion.messenger.ui.user.AboutUserFragment;
import com.orion.messenger.ui.user.AboutUserViewModel;
import com.orion.messenger.ui.user.AppendUserFragment;
import com.orion.messenger.ui.user.AppendUserViewModel;
import com.orion.messenger.ui.user.AutorizateFragment;
import com.orion.messenger.ui.user.AutorizateViewModel;
import com.orion.messenger.ui.user.ListUsersFragment;
import com.orion.messenger.ui.user.ListUsersViewModel;
import com.orion.messenger.ui.user.RegistrationFragment;
import com.orion.messenger.ui.user.RegistrationViewModel;


public class MainActivity extends AppCompatActivity implements ISupportOperation {

    private SettingsHandler settingsHandler;
    public ChatHandler chatHandler;

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    private NavController navController;
    private ProgressBar progressBar = null;
    private ProgressThread progressThread = new ProgressThread();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createChatHandler();
        settingsHandler = new SettingsHandler(getApplicationContext());
        ChatHandlerProperty settings = settingsHandler.readSettingsFile();
        chatHandler.setSettings(settings);

        startChatHandler();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarMain.toolbar);

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_chat, R.id.nav_about_user, R.id.nav_autorizate,
                R.id.nav_registration, R.id.nav_about_project/*R.id.nav_settings*/)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {

            private int firstStart = 0;

            @Override
            public void onDestinationChanged(@NonNull NavController navController, @NonNull NavDestination navDestination, @Nullable Bundle bundle) {

                int dest = navDestination.getId();

                //  TODO implement normal handling of the first launch with server unavailability
                if (firstStart >= 1 && !chatHandler.isConnected()) {
                    showMessage("Failed to connect to server!");
                }
                if (firstStart > 1000) {
                    firstStart = 2;
                } else {
                    firstStart++;
                }
                //  TODO end

                if (dest == R.id.nav_about_user) {
                    chatHandler.setCurrentFragment(AboutUserFragment.ID);
                    invalidateOptionsMenu();
                }
                if (dest == R.id.nav_about_project) {
                    chatHandler.setCurrentFragment(AboutProjectFragment.ID);
                    invalidateOptionsMenu();
                    chatHandler.getDescriptionProject();
                }
            }
        });

        ViewModelProvider provider = new ViewModelProvider(this);
        FragmentManager resultListenFragManag = getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_main)
                .getChildFragmentManager();

        loadSettingsToModels(settings, provider);

        setListenerGetChatsList(resultListenFragManag);
        setListenerGetMessageList(resultListenFragManag, provider);
        setListenerGetUsersList(resultListenFragManag, provider);
        setListenerShowMessages(resultListenFragManag, provider);
        setListenerShowChats(resultListenFragManag);
        setListenerShowUsers(resultListenFragManag);
        setListenerNewChat(resultListenFragManag);
        setListenerNewMessage(resultListenFragManag, provider);

        setListenerShowNotification(resultListenFragManag);
        setListenerAutorization(resultListenFragManag, provider);
        setListenerRegistration(resultListenFragManag, provider);

        setListenerAppendUser(resultListenFragManag);
        setListenerRemoveUser(resultListenFragManag);
        setListenerSearchdUser(resultListenFragManag);
        setListenerShowAppendUsers(resultListenFragManag);
        setListenerExitChat(resultListenFragManag);

        progressBar = findViewById(R.id.progress_bar);
    }

    class ProgressThread extends Thread {

        private boolean running = false;
        public int progress;
        @Override
        public void run() {
            progress = 0;
            running = true;

            while (running) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progress += 2;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setProgress(progress);
                    }
                });
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }

        public void stopThread() {
            running = false;
        }
        public boolean isRunning() {
            return running;
        }
    }

    public void runProgress() {

        if (progressThread != null) {
            progressThread.stopThread();
            progressThread = new ProgressThread();
        }
        progressBar.setVisibility(View.VISIBLE);
        progressThread.start();
    }

    public void stopProgress(){
        progressThread.stopThread();
    }

    public void loadModelsToSettings(ChatHandlerProperty settings) {

        ViewModelProvider provider = new ViewModelProvider(this);

        settings.loadFromViewModel(provider.get(AutorizateViewModel.class));
        settings.loadFromViewModel(provider.get(AboutUserViewModel.class));
        settings.loadFromViewModel(provider.get(RegistrationViewModel.class));
    }

    @Override
    public void onRegisteration() {
        ChatHandlerProperty sett = chatHandler.getSettings();
        loadModelsToSettings(sett);
        loadSettingsToModels(sett, new ViewModelProvider(this));
        settingsHandler.writeSettingsToFile(sett);
    }

    @Override
    public void onAutorization() {
        ChatHandlerProperty sett = chatHandler.getSettings();
        loadSettingsToModels(sett, new ViewModelProvider(this));
        loadModelsToSettings(chatHandler.getSettings());
        settingsHandler.writeSettingsToFile(chatHandler.getSettings());
    }

    private void loadSettingsToModels(ChatHandlerProperty settings, ViewModelProvider provider) {

        AutorizateViewModel mAutorizate = provider.get(AutorizateViewModel.class);
        AboutUserViewModel mAbout = provider.get(AboutUserViewModel.class);

        if (settings.userId >0) {
            mAbout.setUserId(settings.userId);
        }
        if (!settings.login.isEmpty()) {
            mAutorizate.setLogin(settings.login);
            mAbout.setLogin(settings.login);
        }
        if (!settings.password.isEmpty()) {
            mAutorizate.setPassword(settings.password);
            mAbout.setPassword(settings.password);
        }
        if (!settings.alias.isEmpty()) {
            mAbout.setAlias(settings.alias);
        }
        if (!settings.email.isEmpty()) {
            mAbout.setEmail(settings.email);
        }
    }

   /* private void setListenerChangeMessageStatus(FragmentManager manager){

        manager.setFragmentResultListener(ChatsContainerFragment.CHANGE_MESSAGE_STATUS,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        String status = result.getString(ChatsContainerFragment.PARAM_MESSAGE_STATUS);
                        chatHandler.changeMessageStatus(status);
                    }
                });
    }*/

    private void setListenerSearchdUser(FragmentManager manager){

        manager.setFragmentResultListener(ChatsContainerFragment.SEARCH_USER,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        chatHandler.searchUser();
                    }
                });
    }

    private void setListenerExitChat(FragmentManager manager){

        manager.setFragmentResultListener(ChatsContainerFragment.EXIT_CHAT,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        chatHandler.exitChat();
                    }
                });
    }

    private void setListenerAppendUser(FragmentManager manager){

        manager.setFragmentResultListener(ChatsContainerFragment.APPEND_USER,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        chatHandler.appendUserInChat();
                    }
                });
    }

    private void setListenerRemoveUser(FragmentManager manager){

        manager.setFragmentResultListener(ChatsContainerFragment.REMOVE_USER,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        chatHandler.removeUserFromChat();
                    }
                });
    }

    private void setListenerRegistration(FragmentManager manager, ViewModelProvider provider) {

        manager.setFragmentResultListener(ChatsContainerFragment.REGISTRATION,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        chatHandler.registrationUser();
                    }
                });
    }

    private void setListenerAutorization(FragmentManager manager, ViewModelProvider provider) {

        manager.setFragmentResultListener(ChatsContainerFragment.AUTORIZATE,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        chatHandler.autorizationUser();
                    }
                });
    }

    private void setListenerShowNotification(FragmentManager manager) {

        manager.setFragmentResultListener(ChatsContainerFragment.SHOW_NOTIFICATION,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        showMessage(result.getString(ChatsContainerFragment.PARAM_MESSAGE_TEXT));
                    }
                });
    }

    private void setListenerShowAppendUsers(FragmentManager manager) {

        manager.setFragmentResultListener(ChatsContainerFragment.SHOW_APPEND_USERS,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        chatHandler.setCurrentFragment(AppendUserFragment.ID);
                        invalidateOptionsMenu();
                    }
                });
    }

    private void setListenerShowUsers(FragmentManager manager) {

        manager.setFragmentResultListener(ChatsContainerFragment.SHOW_CHAT_USERS,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        chatHandler.setCurrentFragment(ListUsersFragment.ID);
                        invalidateOptionsMenu();
                    }
                });
    }

    private void setListenerShowChats(FragmentManager manager) {

        manager.setFragmentResultListener(ChatsContainerFragment.SHOW_CHATS,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        getSupportActionBar().setTitle("Chats");

                        chatHandler.setCurrentFragment(ChatFragment.ID);
                        invalidateOptionsMenu();
                    }
                });
    }

    private void setListenerShowMessages(FragmentManager manager, ViewModelProvider provider) {

        manager.setFragmentResultListener(ChatsContainerFragment.SHOW_MESSAGES,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        String chatName = provider.get(ChatViewModel.class).getCurrentChatName();
                        getSupportActionBar().setTitle(chatName);

                        chatHandler.setCurrentFragment(MessageFragment.ID);
                        invalidateOptionsMenu();
                    }
                });
    }

    private void setListenerGetChatsList(FragmentManager manager) {

        manager.setFragmentResultListener(ChatsContainerFragment.GET_LIST_CHATS,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        chatHandler.getListChats();
                    }
                });
    }

    private void setListenerGetMessageList(FragmentManager manager, ViewModelProvider provider) {

        manager.setFragmentResultListener(ChatsContainerFragment.GET_LIST_MESSAGES,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        chatHandler.getPreviousListMessages();
                    }
                });
    }

    private void setListenerGetUsersList(FragmentManager manager, ViewModelProvider provider) {

        manager.setFragmentResultListener(ChatsContainerFragment.GET_LIST_USERS,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                      /*  MessageViewModel model = provider.get(MessageViewModel.class);
                        ChatViewModel mChat = provider.get(ChatViewModel.class);
                        chatHandler.setCurrentChatId(mChat.currentChat.getValue());
*/
                        chatHandler.getListChatUsers();
                    }
                });
    }

    private void setListenerNewChat(FragmentManager manager) {

        manager.setFragmentResultListener(ChatsContainerFragment.CREATE_NEW_CHAT,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {
                        chatHandler.createNewChat(result.getString(ChatsContainerFragment.PARAM_CHAT_NAME));
                    }
                });
    }

    private void setListenerNewMessage(FragmentManager manager, ViewModelProvider provider) {

        manager.setFragmentResultListener(ChatsContainerFragment.CREATE_NEW_MESSAGE,
                this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

                        String msg = result.getString(ChatsContainerFragment.PARAM_MESSAGE_TEXT);
                        Long parent = result.getLong(ChatsContainerFragment.PARAM_PARENT_MESSAGE);
                        chatHandler.createNewMessage(msg, parent, "");
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int menuId = item.getItemId();
        if (menuId == R.id.action_list_users) {

            ViewModelProvider provider= new ViewModelProvider(this);
            ListUsersViewModel mUsers = provider.get(ListUsersViewModel.class);

            if (mUsers.needUpdate()) {
                chatHandler.getListChatUsers();
            }
            createUsersUI();
            return super.onOptionsItemSelected(item);
        }
        if (menuId == R.id.action_remove_chat) {
            chatHandler.removeChat();
            return super.onOptionsItemSelected(item);
        }

       /* if (menuId == R.id.action_add_user) {
            createAppendUsersUI();
            return super.onOptionsItemSelected(item);
        }*/
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        int currentFragment = chatHandler.getCurrentFragment();
        if (currentFragment == 0) {
            return super.onPrepareOptionsMenu(menu);
        }

        for (int i=0; i<menu.size(); i++) {
            menu.getItem(i).setVisible(false);
            menu.getItem(i).setEnabled(false);
        }

        switch (currentFragment) {
            case MessageFragment.ID:
                menu.getItem(0).setEnabled(true);
                menu.getItem(0).setVisible(true);
                menu.getItem(1).setEnabled(true);
                menu.getItem(1).setVisible(true);
                break;
         /*   case ListUsersFragment.ID:
                menu.getItem(1).setEnabled(true);
                menu.getItem(1).setVisible(true);
                break;*/
        }

        return super.onPrepareOptionsMenu(menu);
    }

    public void showMessage(String msg){
        Log.e(ConstWSThread.LOG_TAG, msg);
        Snackbar.make(binding.navView, msg, 3000).show();
    }

    private void createChatHandler() {
        chatHandler = new ChatHandler(this);
    }

    @Override
    protected void onStop() {
        saveSettings();
        super.onStop();
    }

    public void saveSettings() {
        settingsHandler.writeSettingsToFile(chatHandler.getSettings());
    }

    public void createAppendUsersUI(){
        getChatContainerFragment().createAppendUsersUI();
    }

    public void createUsersUI(){
        getChatContainerFragment().createChatUsersUI();
    }

    private ChatsContainerFragment getChatContainerFragment(){

        Fragment fr1 = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        ChatsContainerFragment f = (ChatsContainerFragment) fr1.getChildFragmentManager().findFragmentById(R.id.nav_host_fragment_content_main);
        return f;
    }



    private void startChatHandler() {
        if (chatHandler == null) {
            createChatHandler();
        }
        chatHandler.startHandler();
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

    public void createChatUI() {
        chatHandler.setCurrentFragment(ChatFragment.ID);
        invalidateOptionsMenu();
        navController.navigate(R.id.nav_chat);
    }

    @Override
    public void createAutorizateUI() {
        chatHandler.setCurrentFragment(AutorizateFragment.ID);
        invalidateOptionsMenu();
        navController.navigate(R.id.nav_autorizate);
    }

    @Override
    public void createRegisterUI() {
        chatHandler.setCurrentFragment(RegistrationFragment.ID);
        invalidateOptionsMenu();
        navController.navigate(R.id.nav_registration);
    }


    @Override
    public ViewModel getViewModel(String className) {

        if (className.equals(AboutUserViewModel.class.getName())) {
            return new ViewModelProvider(this).get(AboutUserViewModel.class);
        }
        if (className.equals(RegistrationViewModel.class.getName())) {
            return new ViewModelProvider(this).get(RegistrationViewModel.class);
        }
        if (className.equals(AutorizateViewModel.class.getName())) {
            return new ViewModelProvider(this).get(AutorizateViewModel.class);
        }
        if (className.equals(ChatViewModel.class.getName())) {
            return new ViewModelProvider(this).get(ChatViewModel.class);
        }
        if (className.equals(MessageViewModel.class.getName())) {
            return new ViewModelProvider(this).get(MessageViewModel.class);
        }
        if (className.equals(ListUsersViewModel.class.getName())) {
            return new ViewModelProvider(this).get(ListUsersViewModel.class);
        }
        if (className.equals(AppendUserViewModel.class.getName())) {
            return new ViewModelProvider(this).get(AppendUserViewModel.class);
        }
        if (className.equals(AboutProjectViewModel.class.getName())) {
            return new ViewModelProvider(this).get(AboutProjectViewModel.class);
        }

        return null;
    }
}