package br.com.jortec.mide.fragments;

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
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.AbstractList;
import java.util.ArrayList;

import br.com.jortec.mide.FecharOsActivity;
import br.com.jortec.mide.R;
import br.com.jortec.mide.Util.Formate;
import br.com.jortec.mide.adapter.MaterialAdapter;
import br.com.jortec.mide.dominio.Material;
import br.com.jortec.mide.dominio.OrdemServico;
import br.com.jortec.mide.interfaces.Comunicador;
import de.greenrobot.event.EventBus;

/**
 * Created by Jorliano on 17/04/2016.
 */
public class MaterialFragment extends Fragment implements Comunicador{

    private Toolbar toolbar;
    private Spinner spMaterial;
    private EditText edtQuantidade;
    private ArrayList<Material> materiais = new ArrayList<>();
    private MaterialAdapter adapter;
    private RecyclerView recyclerView;
    private RelativeLayout relativeLayout;
    private View view;
    private ImageButton btAdd;
    private Paint p = new Paint();
    int itesnSalvos = 0;

    private Material material ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        view = inflater.inflate(R.layout.fragment_material, null);

        iniciarViews();
        verificarLista();
        initSwipe();


        return view;
    }

    public boolean isValido() {

        if (edtQuantidade.getText().toString().equals("")) {
            return false;
        }

        material = new Material();
        material.setQuantidade(Integer.parseInt(edtQuantidade.getText().toString()));
        material.setDescricao(spMaterial.getSelectedItem().toString());

        for (int i = 0; i < materiais.size(); i++) {
            if (materiais.get(i).getDescricao().equals(material.getDescricao())) {
                Toast.makeText(view.getContext(), "Material já existe na lista", Toast.LENGTH_SHORT).show();
                return false;
            }
        }
        return true;
    }

    public void iniciarViews() {

        toolbar = (Toolbar) view.findViewById(R.id.material_toolbar);
        spMaterial = (Spinner) view.findViewById(R.id.sp_material);
        btAdd = (ImageButton) view.findViewById(R.id.ib_add);
        recyclerView = (RecyclerView) view.findViewById(R.id.card_recycler_view);
        relativeLayout = (RelativeLayout) view.findViewById(R.id.rl_material);

        toolbar.setTitle(R.string.tituloBarraOsMaterial);
        toolbar.inflateMenu(R.menu.menu_material);
        toolbar.getMenu().getItem(0).setVisible(false);


        spMaterial.setAdapter(new ArrayAdapter<String>(view.getContext(), R.layout.item_sp, getResources().getStringArray(R.array.itens_material)));
        spMaterial.setSelection(0);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(view.getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        adapter = new MaterialAdapter(materiais, view.getContext());
        recyclerView.setAdapter(adapter);

        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtQuantidade = new EditText(view.getContext());
                edtQuantidade.setInputType(InputType.TYPE_CLASS_NUMBER);
                edtQuantidade.setPadding(20, 0, 0, 20);

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Quantidade")
                        .setMessage("Digite a quantidade")
                        .setView(edtQuantidade)
                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (isValido()) {
                                    materiais.add(material);
                                    verificarLista();
                                    adapter.notifyDataSetChanged();
                                    EventBus.getDefault().post(material);

                                } else {
                                    Toast.makeText(view.getContext(), "Dados invalidos", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton(R.string.nao, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();


            }
        });
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
                EventBus.getDefault().post(materiais.get(position));
                adapter.removeItem(position);
                verificarLista();
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
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    public void verificarLista() {
        if (materiais.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            TextView tv = new TextView(view.getContext());
            tv.setText("Não existe itens na lista");
            tv.setId(1);
            tv.setTextColor(getResources().getColor(R.color.primary_text));
            tv.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            tv.setGravity(Gravity.CENTER);
            relativeLayout.addView(tv);
        } else if (relativeLayout.findViewById(1) != null) {
            recyclerView.setVisibility(View.VISIBLE);
            relativeLayout.removeView(relativeLayout.findViewById(1));
        }
    }

    @Override
    public void responde(Object obj) {
       Material material = (Material) obj;

       if(materiais != null)
           materiais.add(material);
    }


}
