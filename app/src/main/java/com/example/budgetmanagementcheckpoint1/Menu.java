package com.example.budgetmanagementcheckpoint1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.example.budgetmanagementcheckpoint1.databinding.ActivityMenuBinding;
import com.example.budgetmanagementcheckpoint1.fragments.HomeFragment;
import com.example.budgetmanagementcheckpoint1.fragments.BudgetPlanFragment;
import com.example.budgetmanagementcheckpoint1.fragments.ComparisonsFragment;
import com.example.budgetmanagementcheckpoint1.fragments.StatsFragment;

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
              case R.id.budgetPlan:
                  replaceFragment( new BudgetPlanFragment());
                  break;
              case R.id.comparison:
                  replaceFragment( new ComparisonsFragment());
                  break;
              case R.id.stats:
                  replaceFragment( new StatsFragment());
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