package com.example.budgetmanagementcheckpoint1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.budgetmanagementcheckpoint1.databinding.ActivityMenuBinding;
import com.example.budgetmanagementcheckpoint1.fragments.HomeFragment;
import com.example.budgetmanagementcheckpoint1.fragments.PersonFragment;
import com.example.budgetmanagementcheckpoint1.fragments.SearchFragment;
import com.example.budgetmanagementcheckpoint1.fragments.SettingsFragment;

public class Menu extends AppCompatActivity {

    ActivityMenuBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        binding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment( new HomeFragment());

      binding.bottomNavigationView.setOnItemSelectedListener(item -> {
          switch(item.getItemId()){


              case R.id.home:
                  replaceFragment( new HomeFragment());
                  break;
              case R.id.person:
                  replaceFragment( new PersonFragment());
                  break;
              case R.id.search:
                  replaceFragment( new SearchFragment());
                  break;
              case R.id.settings:
                  replaceFragment( new SettingsFragment());
                  break;


          }
          return true;
      });
    }

    private void replaceFragment(Fragment fragment){

        FragmentManager fragementManager =getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragementManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();


    }
}