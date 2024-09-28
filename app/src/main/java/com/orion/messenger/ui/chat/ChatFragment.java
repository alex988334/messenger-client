package com.orion.messenger.ui.chat;

import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;

import com.orion.messenger.MainActivity;
import com.orion.messenger.R;
import com.orion.messenger.databinding.FragmentChatBinding;
import com.orion.messenger.ui.data.IModel;
import com.orion.messenger.ui.user.RegistrationViewModel;

import java.util.List;
import java.util.Map;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private ChatViewModel model;
    private LayoutInflater inflater;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        this.inflater = inflater;
        model = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);

        binding = FragmentChatBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        if (model.responseServer == null) {
            Log.e(ConstWSThread.LOG_TAG, "model.responseServer == null");
        }

        binding.createChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNewChatDialog();
            }
        });

        binding.spisok.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int chatId = (Integer) view.getTag();
                MainActivity.activvity.chat.setCurrentChat(chatId);
                MainActivity.activvity.chat.getListMessages();
            }
        });

        model.responseServer.observe(getViewLifecycleOwner(), responseServer -> {

            AdapterListChats adapter = new AdapterListChats(getLayoutInflater());
            adapter.setData(responseServer);
            binding.spisok.setAdapter(adapter);
        });

        return root;
    }

    private void createNewChatDialog(){

        ChatFragment fr = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.create_chat_title);
        View v = inflater.inflate(R.layout.create_chat, null);
        EditText t = v.findViewById(R.id.create_chat_name);

        builder.setView(v);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String name = t.getText().toString();
                if (!name.isEmpty()) {
                    MainActivity.activvity.chat.createNewChat(name);
                }
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        builder.setIcon(R.drawable.ic_copyright);

        AlertDialog dialog = builder.create();
        dialog.show();
    }


/*
    public void showChats(ResponseServer responseServer){

        NotificationManager notificationManager = (NotificationManager) head.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Config.APPLICATION_ID);                                          //

        Map<String, List<IModel>> response = (Map<String, List<IModel>>) obj;                       //  приводим объект с данными к нужному типу

        for (int i=0; i<response.size(); i++) {                                                     //  пробегаемся по коллекции данных
            List<IModel> map = response.get(i);
            map.put("alarm", "0");                                                            //  добавляем новый параметр, который отвечает за отображение колокольчика, если > 0 то отображение
            if (map != null && map.get(DBConnect.KEY_ID_MESSAGE) != null){                          //  проверяем последнее доступное сообщение для данного чата
                if (Integer.parseInt(map.get(DBConnect.KEY_ID_USER)) != Integer.parseInt(head.getIdMastera())
                        && !map.get(DBConnect.KEY_STATUS).equalsIgnoreCase(ConstWSThread.MESSAGE_READED)) {
                    //  проверяем что сообщение не является вашим и его статус не является - прочтено
                    response.add(0, response.remove(i));                                         //  добавляем наш  чат в начало списка чатов
                }
            }
        }

        AdapterListChats chatAdapter = new AdapterListChats(head, response);                        //  создаем адаптер чатов
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) head.getLayoutInflater().inflate(R.layout.chat, roditel, false);
        //  получаем представление нашего списока чатов
        roditel.addView(coordinatorLayout);                                                         //  добавляем в родительское представление

        FloatingActionButton fab = (FloatingActionButton) head.findViewById(R.id.create_chat);      //  находим кнопку создания новых чатов
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showGeneratorChats(view);                                                           //  вешаем обработчик
            }
        });

        ListView spisok = (ListView) roditel.findViewById(R.id.spisok);                             //  ищем представление списка чатов
        spisok.setAdapter(chatAdapter);                                                             //  устанавливаем для него адаптер

        spisok.setOnItemClickListener(new AdapterView.OnItemClickListener() {                       //  создаем слушателя нажатия на элемент списка
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {                              //  вешаем обработчик на элементы списка
                head.setOperation(ConstHead.CHAT_MESSAGES);                                         //  устанавливаем нужный тип операции
                chatNumber = Integer.valueOf(view.getTag().toString());                             //  устанавливаем номер чата
                chatName = ((TextView) view.findViewById(R.id.name)).getText().toString();          //  запоминаем название выбранного чата
                wsThread.getChatUsers();                                                            //  обновляем список пользователей чата, исключая возможность появления сообщений от новых пользователей чата не внесенных в локальную бд
                getMessages();                                                                      //  запрашиваем переход к списку сообщений
            }
        });
    }

*/
@Override
public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    model = new ViewModelProvider(this).get(ChatViewModel.class);
    // TODO: Use the ViewModel
}

/*
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }*/
}