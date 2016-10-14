package br.com.jortec.mide;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.PersistableBundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.jortec.mide.adapter.MaterialAdapter;
import br.com.jortec.mide.dominio.Material;
import br.com.jortec.mide.dominio.Servico;
import br.com.jortec.mide.dominio.listarImagens;
import br.com.jortec.mide.interfaces.Comunicador;
import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmResults;

public class MaterialActivity extends AppCompatActivity implements Comunicador{

    private Toolbar toolbar;
    private Spinner spMaterial;
    private EditText edtQuantidade;
    private ArrayList<Material> materiais = new ArrayList<>();
    private MaterialAdapter adapter;
    private RecyclerView recyclerView;
    private CoordinatorLayout coordinatorLayout;
    private View view;
    private ImageButton btAdd;
    private Paint p = new Paint();
    private String selecionado;
    int itesnSalvos = 0;



    Material material;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_material);

        if (savedInstanceState != null) {
            Log.i("LOG", "itens " + savedInstanceState.getInt(Material.BUNDLE_KEY));

             materiais = (ArrayList<Material>) savedInstanceState.getSerializable("material");
        }

        if (getIntent() != null && getIntent().getLongExtra(Material.BUNDLE_DADOS, 0) > 0  ){
            Realm realm = Realm.getDefaultInstance();
            RealmResults<Servico> servicos = realm.where(Servico.class).findAll();


            Servico s =  servicos.where().equalTo("id", getIntent().getLongExtra(Material.BUNDLE_DADOS, 0)).findFirst();
            if(s != null){

                for (Material m : s.getMaterias()){
                    materiais.add(m);
                }

            }
        }

        iniciarViews();

        toolbar.setTitle(R.string.tituloBarraOsMaterial);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spMaterial.setAdapter(new ArrayAdapter<String>(this, R.layout.item_sp, getResources().getStringArray(R.array.itens_material)));
        spMaterial.setSelection(0);
        selecionado = spMaterial.getSelectedItem().toString();

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new MaterialAdapter(materiais, this);
        recyclerView.setAdapter(adapter);


        //CASO A LISTA ESTEJA VAZIA
        verificarLista();

        initSwipe();

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                spMaterial.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selecionado = spMaterial.getSelectedItem().toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                if (isValido()) {
                    materiais.add(material);
                    verificarLista();
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(v.getContext(), "Dados invalidos", Toast.LENGTH_SHORT).show();
                }

            }
        });

       /* if (!materiais.isEmpty()){
            removeView();
        }*/
    }

    public boolean isValido() {

        if (edtQuantidade.getText().toString().equals("")) {
            return false;
        }

        material = new Material();
        material.setQuantidade(Integer.parseInt(edtQuantidade.getText().toString()));
        material.setDescricao(selecionado);

        for (int i = 0; i < materiais.size(); i++) {
            if (materiais.get(i).getDescricao().equals(material.getDescricao())) {
                Toast.makeText(this, "Material já existe na lista", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void iniciarViews() {
        toolbar = (Toolbar) findViewById(R.id.material_toolbar);
        spMaterial = (Spinner) findViewById(R.id.sp_material);
        edtQuantidade = (EditText) findViewById(R.id.edt_quantidade);
        btAdd = (ImageButton) findViewById(R.id.ib_add);
        recyclerView = (RecyclerView) findViewById(R.id.card_recycler_view);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cl_material);

    }

    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                adapter.removeItem(position);
                verificarLista();

               /* if (direction == ItemTouchHelper.LEFT){
                    adapter.removeItem(position);
                } else {
                    removeView();
                   /* edit_position = position;
                    alertDialog.setTitle("Edit Country");
                    et_country.setText(countries.get(position));
                    alertDialog.show();
                }*/
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;


                    p.setColor(Color.parseColor("#D32F2F"));
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.delete);
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);


                  /*  if(dX > 0){

                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                        c.drawRect(background,p);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    } else {

                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background,p);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                        c.drawBitmap(icon,null,icon_dest,p);
                    }*/
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void removeView() {
        if (view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_material, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        switch (item.getItemId()){
            case android.R.id.home:
                if(!materiais.isEmpty()){
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.titulo_dialogo)
                            .setMessage(R.string.contexto_dialogo)
                            .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    salvarEstado();
                                }
                            })
                            .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }else
                finish();
                break;
            case R.id.action_savar:
                salvarEstado();
                break;
        }

        return true;
    }


    public void salvarEstado(){

        for (Material m : materiais) {
            EventBus.getDefault().post(m);
        }
        finish();
    }

    public void verificarLista(){
        if(materiais.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            TextView tv = new TextView(this);
            tv.setText("Não existe itens na lista");
            tv.setId(1);
            tv.setTextColor(getResources().getColor(R.color.primary_text));
            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            tv.setGravity(Gravity.CENTER);
            coordinatorLayout.addView(tv);
        }else if(coordinatorLayout.findViewById(1) != null){
            recyclerView.setVisibility( View.VISIBLE);
            coordinatorLayout.removeView(coordinatorLayout.findViewById(1));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable("material", materiais);

    }


    @Override
    public void responde(Object obj) {

    }
}
