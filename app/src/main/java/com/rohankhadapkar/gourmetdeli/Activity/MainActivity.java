package com.rohankhadapkar.gourmetdeli.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rohankhadapkar.gourmetdeli.Fragment.MenuFragment;
import com.rohankhadapkar.gourmetdeli.Fragment.NotificationFragment;
import com.rohankhadapkar.gourmetdeli.Fragment.OrdersFragment;
import com.rohankhadapkar.gourmetdeli.R;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationView navigationView;
    private View navHeader;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String photoUrl;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private TextView textName, textEmailId;
    private ImageView profilePic;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = findViewById(R.id.nav_view);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        navHeader = navigationView.getHeaderView(0);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        textName = navHeader.findViewById(R.id.txtName);
        textEmailId = navHeader.findViewById(R.id.txtEmailId);
        profilePic = navHeader.findViewById(R.id.profilePicture);

        loadNavigationHeader();

        MenuFragment menuFragment = new MenuFragment();
        setFragment(menuFragment);
        setTitle("Menu");

    }

    protected void loadNavigationHeader() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_drawer);
            textName.setText(user.getDisplayName());
            textName.setTextSize(15);
            textEmailId.setVisibility(View.VISIBLE);
            textEmailId.setText(user.getEmail());
            photoUrl = user.getPhotoUrl().toString();
            photoUrl = photoUrl.replace("s96", "s250");
            Glide.with(this).load(Uri.parse(photoUrl)).into(profilePic);
        } else {
            navigationView.getMenu().clear();
            navigationView.inflateMenu(R.menu.activity_main_signout_drawer);
            textName.setText("Sign In");
            textName.setTextSize(25);
            textEmailId.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.profile_placeholder).into(profilePic);
            textName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, AuthUiActivity.class);
                    intent.putExtra("Account", "SignIn");
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        /*if (id == R.id.action_settings) {
            return true;
        }*/

        if (id == R.id.cart) {
            Intent intent = new Intent(MainActivity.this, Cart.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            MenuFragment menuFragment = new MenuFragment();
            setTitle("Menu");
            setFragment(menuFragment);
        } else if (id == R.id.nav_orders) {
            OrdersFragment ordersFragment = new OrdersFragment();
            setTitle("Orders");
            setFragment(ordersFragment);
        } else if (id == R.id.nav_notifications) {
            NotificationFragment notificationFragment = new NotificationFragment();
            setTitle("Notifications");
            setFragment(notificationFragment);
        } else if (id == R.id.nav_signOut) {
            Intent intent = new Intent(MainActivity.this, AuthUiActivity.class);
            intent.putExtra("Account", "SignOut");
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        } else if (id == R.id.nav_about_us) {
            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setFragment(android.support.v4.app.Fragment fragment) {
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNavigationHeader();
    }

}
