package hr.unipu.inf.ma.pametnigrad.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import hr.unipu.inf.ma.pametnigrad.GlobalVariables;
import hr.unipu.inf.ma.pametnigrad.R;
import hr.unipu.inf.ma.pametnigrad.user.User;

public class LoginFragment extends Fragment {

    Button loginButton;
    EditText username;
    EditText password;

    private static final String EXTRA_KEY = "Username";

    public LoginFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        FloatingActionButton fab= (FloatingActionButton) getActivity().findViewById(R.id.floatingActionButton);
        fab.setVisibility(View.GONE);

        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        username = (EditText) getView().findViewById(R.id.username);
        password = (EditText) getView().findViewById(R.id.password);

        final FragmentTransaction transaction = getFragmentManager().beginTransaction();

        loginButton = (Button) getView().findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usernameText = username.getText().toString();
                String passwordText = password.getText().toString();

                if(usernameText.matches("") || passwordText.matches("")){
                    Snackbar.make(view, "Krivi username ili password!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else{
                    Snackbar.make(view, "Prijavljeni ste kao:   " + username.getText(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    /*User pero = new User(usernameText, passwordText);

                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_KEY, pero.getUsername());*/

                    GlobalVariables.admin = true;

                    ViewFragment viewFragment = new ViewFragment();
                    //viewFragment.setArguments(bundle);
                    transaction.replace(R.id.mainContainer, viewFragment).commit();

                }
            }
        });
    }
}
