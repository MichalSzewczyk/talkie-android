package com.talkie.android.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.talkie.android.R;
import com.talkie.android.factories.ParsingServiceFactory;
import com.talkie.android.rest.tasks.ImageLoadTask;
import com.talkie.android.services.impl.MessageCachingServiceImpl;
import com.talkie.android.services.interfaces.MessageCachingService;
import com.talkie.dialect.messages.model.User;
import com.talkie.dialect.parser.interfaces.ParsingService;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import static com.talkie.android.ParsingServiceType.CUSTOM_SERVICE;

public class MessengerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ParsingService parsingService;
    private User user;
    private Toolbar toolbar;
    private Socket socket;
    private ListView messages;
    private TextView recipient;
    private static final int SERVER_PORT = 8080;
    private static final String SERVER_HOST = "10.0.2.2";
    private MessageCachingService messageCachingService;
    private ArrayAdapter<String> adapter;
    private Integer actualRecipient;
    private EditText messageText;
    private Button sendButton;

    public MessengerActivity() {
        this.parsingService = ParsingServiceFactory.getService(CUSTOM_SERVICE);
        this.messageCachingService = new MessageCachingServiceImpl(this, android.R.layout.simple_list_item_1);
    }

    private void refreshData() {

    }

    private void setActualRecipient(int recId){
        messages.setAdapter(messageCachingService.getHistory(recId));
        this.recipient.setText(String.valueOf(recId));
        this.actualRecipient = recId;
    }

    private void persistMessage(String message){
        adapter.add(message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messenger);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.messages = (ListView) findViewById(R.id.messages);
        this.recipient = (TextView) findViewById(R.id.recipient);
        this.user = parsingService.deserialize(getIntent().getStringExtra(getString(R.string.user_data)), User.class).orElse(null);
        this.messageText = (EditText) findViewById(R.id.message);
        this.sendButton = (Button) findViewById(R.id.send_message);
        sendButton.setOnClickListener(o ->{
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())),
                true);
                out.println(messageText.getText());
                messageText.setText("");
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
        messageCachingService.persist(user.getId(), 1, 2, "asd");

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        View header = navigationView.getHeaderView(0);
        Menu navMenu = navigationView.getMenu();
        TextView nameAndSurname = (TextView) header.findViewById(R.id.nameAndSurname);
        nameAndSurname.setText(user.getName() + " " + user.getLastName());
        TextView login = (TextView) header.findViewById(R.id.login);
        login.setText(user.getLogin());
        ImageView avatar = (ImageView) header.findViewById(R.id.avatar);
        String avatarUrl = user.getAvatar() == null ? "http://cdmlipowa.pl/wp-content/uploads/2014/03/Person-icon-grey.jpg" : user.getAvatar();
        ImageLoadTask imageLoadTask = new ImageLoadTask(avatarUrl, avatar);
        imageLoadTask.execute();
        avatar.setMaxWidth(20);
        avatar.setMaxHeight(20);
        int id = 0;
        for (User u : user.getFriends()) {
            MenuItem menuItem = navMenu.add(0, id++, 0, u.getName() + u.getLastName()).setShortcut('3', 'c').setIcon((!(u.getOnline() == null)) && u.getOnline() ? R.drawable.available_dot : R.drawable.not_available_dot);
            menuItem.setOnMenuItemClickListener(o -> {
                setActualRecipient(u.getId());
                return true;
            });
        }
        Thread clientThread = new Thread(new ClientThread());
        clientThread.setDaemon(true);
        clientThread.start();
    }

    private class ClientThread implements Runnable {

        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_HOST);
                socket = new Socket(serverAddr, SERVER_PORT);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        int id = 0;
        for (User u : user.getFriends()) {
            menu.add(0, id++, 0, u.getName() + u.getLastName()).setShortcut('3', 'c');
        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
