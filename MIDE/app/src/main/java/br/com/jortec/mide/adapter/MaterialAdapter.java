package br.com.jortec.mide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import br.com.jortec.mide.R;
import br.com.jortec.mide.dominio.Material;

/**
 * Created by Jorliano on 04/03/2016.
 */
public class MaterialAdapter  extends RecyclerView.Adapter<MaterialAdapter.ViewHolder> {
    private ArrayList<Material> materials;
    Context c;
    private int[] colores;
    List<String> lista = new ArrayList<>();


    public MaterialAdapter(ArrayList<Material> materials, Context c) {
        this.materials = materials;
        this.c = c;
        colores = c.getResources().getIntArray(R.array.cores);
    }

    @Override
    public MaterialAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_recyclerview_material, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.tvDescricao.setText(materials.get(i).getDescricao());
        viewHolder.tvQuantidade.setText("Quantidade " + materials.get(i).getQuantidade());

        if(!lista.contains(viewHolder.tvDescricao.getText().toString())){
            viewHolder.icon.setBackgroundColor(cor());
            viewHolder.icon.setText(materials.get(i).getDescricao().substring(0,1));

            lista.add(viewHolder.tvDescricao.getText().toString());
        }



    }

    @Override
    public int getItemCount() {
        return materials.size();
    }

    public void addItem(Material material) {
        materials.add(material);
        notifyItemInserted(materials.size());
    }

    public void removeItem(int position) {
        materials.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, materials.size());
    }

    public int cor (){
        Random ale= new Random();
        int num= (int) (ale.nextInt(colores.length));
        return colores[num];
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView icon;
        TextView tvDescricao;
        TextView tvQuantidade;

        public ViewHolder(View view) {
            super(view);

            tvDescricao = (TextView)view.findViewById(R.id.tv_titulo_recycler_material);
            tvQuantidade = (TextView)view.findViewById(R.id.tv_subtitulo_recycler_material);
            icon = (TextView) view.findViewById(R.id.icon_recycler_material);
        }
    }
}