package com.example.sellnbuy.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.sellnbuy.R;
import com.example.sellnbuy.activity.DetailActivity;
import com.example.sellnbuy.activity.InsertActivity;
import com.example.sellnbuy.activity.LoginActivity;
import com.example.sellnbuy.activity.ProfileActivity;
import com.example.sellnbuy.adapter.PostAdapter;
import com.example.sellnbuy.model.Post;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment implements FirebaseAuth.AuthStateListener, PostAdapter.PostListener {

    private static final String TAG = "HomeFragment";

    private View root;
    private CircleImageView profileImageView;
    private FloatingActionButton fab;
    private SearchView searchView;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private PostAdapter adapter;
    private Spinner spnCityFilter, spnCategoryFilter;
    private ImageView imgFilter;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);

        profileImageView = root.findViewById(R.id.image_profile);
        fab = root.findViewById(R.id.fab_insert);
        searchView = root.findViewById(R.id.searchView);
        toolbar = root.findViewById(R.id.toolbar_home);
        spnCityFilter = root.findViewById(R.id.spinner_city_filter);
        spnCategoryFilter = root.findViewById(R.id.spinner_category_filter);
        imgFilter = root.findViewById(R.id.image_filter);
        recyclerView = root.findViewById(R.id.recycler_post);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        ArrayAdapter spnCityAdapter = ArrayAdapter.createFromResource(getContext(), R.array.city_filter, android.R.layout.simple_spinner_item);
        spnCityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCityFilter.setAdapter(spnCityAdapter);
        ArrayAdapter spnCategoryAdapter = ArrayAdapter.createFromResource(getContext(), R.array.category_filter, android.R.layout.simple_spinner_item);
        spnCategoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategoryFilter.setAdapter(spnCategoryAdapter);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initRecyclerView();
        listenActions();

        return root;
    }



    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(this);
        adapter.startListening();
    }



    @Override
    public void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(this);

        if (adapter != null)
            adapter.stopListening();
    }



    private void listenActions() {
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ProfileActivity.class));
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), InsertActivity.class));
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignOutDialog();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchData(newText);
                return false;
            }
        });

        spnCityFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spnCityFilter.getSelectedItemPosition() != 0)
                    if (spnCategoryFilter.getSelectedItemPosition() != 0)
                        filterBoth();
                    else
                        filterCity();
                else
                    if (spnCategoryFilter.getSelectedItemPosition() != 0)
                        filterCategory();
                    else
                        initRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnCategoryFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spnCategoryFilter.getSelectedItemPosition() != 0)
                    if (spnCityFilter.getSelectedItemPosition() != 0)
                        filterBoth();
                    else
                        filterCategory();
                else
                    if (spnCityFilter.getSelectedItemPosition() != 0)
                        filterCity();
                    else
                        initRecyclerView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spnCityFilter.getSelectedItemPosition() != 0 || spnCategoryFilter.getSelectedItemPosition() != 0)
                    initRecyclerView();
            }
        });
    }



    private void searchData(String searchText) {
        if (searchText.isEmpty())
            initRecyclerView();
        else{
            Query query = db.collection(Post.COLLECTION_NAME)
                    .whereEqualTo(Post.IS_AVAILABLE, true)
                    .whereEqualTo(Post.TITLE, searchText);
//                    .orderBy(Post.TIME, Query.Direction.DESCENDING);

            FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                    .setQuery(query, Post.class)
                    .build();

            adapter = new PostAdapter(options, getContext(), this);
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }



    private void filterCity(){
        Query query = db.collection(Post.COLLECTION_NAME)
                .whereEqualTo(Post.IS_AVAILABLE, true)
                .whereEqualTo(Post.CITY, spnCityFilter.getSelectedItem())
                .orderBy(Post.TIME, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new PostAdapter(options, getContext(),  this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }



    private void filterCategory(){
        Query query = db.collection(Post.COLLECTION_NAME)
                .whereEqualTo(Post.IS_AVAILABLE, true)
                .whereEqualTo(Post.CATEGORY, spnCategoryFilter.getSelectedItem())
                .orderBy(Post.TIME, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new PostAdapter(options, getContext(),  this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }




    private void filterBoth(){
        Query query = db.collection(Post.COLLECTION_NAME)
                .whereEqualTo(Post.IS_AVAILABLE, true)
                .whereEqualTo(Post.CATEGORY, spnCategoryFilter.getSelectedItem())
                .whereEqualTo(Post.CITY, spnCityFilter.getSelectedItem())
                .orderBy(Post.TIME, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new PostAdapter(options, getContext(),  this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }




    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        if (firebaseAuth.getCurrentUser() == null) {
            startLoginActivity();
        } else {
            if (firebaseAuth.getCurrentUser().getPhotoUrl() != null) {
                Glide.with(this)
                        .load(firebaseAuth.getCurrentUser().getPhotoUrl())
                        .into(profileImageView);
            }
        }
    }



    private void showSignOutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.want_to_login)
                .setCancelable(false)
                .setIcon(R.drawable.ic_exit)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }



    private void initRecyclerView() {
        Query query = db.collection(Post.COLLECTION_NAME)
                .whereEqualTo(Post.IS_AVAILABLE, true);
//                .orderBy(Post.TIME, Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>()
                .setQuery(query, Post.class)
                .build();

        adapter = new PostAdapter(options, getContext(), this);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

        spnCityFilter.setSelection(0);
        spnCategoryFilter.setSelection(0);
    }



    private void startLoginActivity() {
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }




    @Override
    public void showPostDetails(DocumentSnapshot snapshot) {
        Post post = snapshot.toObject(Post.class);
        Intent intent = new Intent(getActivity(), DetailActivity.class);
        intent.putExtra(Post.COLLECTION_NAME, post);
        intent.putExtra(Post.POST_ID, snapshot.getReference().getId());
        startActivity(intent);
    }

}
