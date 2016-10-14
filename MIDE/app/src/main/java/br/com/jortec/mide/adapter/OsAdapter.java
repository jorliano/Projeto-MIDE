package br.com.jortec.mide.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.jortec.mide.R;
import br.com.jortec.mide.dominio.OrdemServico;
import br.com.jortec.mide.interfaces.RecyclerViewOnclickListener;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by Jorliano on 03/03/2016.
 */
public class OsAdapter  extends RealmBasedRecyclerViewAdapter<OrdemServico,
        OsAdapter.ViewHolder> {

    private RecyclerViewOnclickListener recyclerViewOnclickListener;
    Context context;

    public OsAdapter(Context ccontext, RealmResults<OrdemServico> realmResults,
                        boolean automaticUpdate, boolean animacaoTipo) {

        super(ccontext, realmResults, automaticUpdate, animacaoTipo);
        this.context = context;
    }

    public class ViewHolder extends RealmViewHolder implements View.OnClickListener {

        public ImageView imStatus;
        public TextView tvTitulo;
        public TextView tvDescricao;
        public TextView tvHora;


        public ViewHolder(LinearLayout container) {
            super(container);
            this.imStatus = (ImageView) container.findViewById(R.id.im_recycler);
            this.tvTitulo = (TextView) container.findViewById(R.id.tv_titulo_recycler);
            this.tvDescricao = (TextView) container.findViewById(R.id.tv_descricao_recycler);
            this.tvHora = (TextView) container.findViewById(R.id.tv_hora_recycler);

            container.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (recyclerViewOnclickListener != null) {
                recyclerViewOnclickListener.onclickListener(v, getPosition());
            }
        }
    }

    public void setRecyclerViewOnclickListener(RecyclerViewOnclickListener r) {
        recyclerViewOnclickListener = r;
    }

    @Override
    public ViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int viewType) {
        View v = inflater.inflate(R.layout.item_recyclerview_os, viewGroup, false);

        ViewHolder vh = new ViewHolder((LinearLayout) v);
        return vh;
    }


    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {

        int tamanho = realmResults.size();

        OrdemServico ordemServico = realmResults.get(position);

        viewHolder.tvTitulo.setText(ordemServico.getNomeCliente());
        viewHolder.tvDescricao.setText(ordemServico.getDescricao());
        viewHolder.tvHora.setText(ordemServico.getHora());


        if(ordemServico.getTipo().equals("instalacao")){
            viewHolder.imStatus.setImageResource(R.drawable.install);
        }else
            if(ordemServico.getTipo().equals("manutencao")){
                viewHolder.imStatus.setImageResource(R.drawable.manutencion);
        }else{
                viewHolder.imStatus.setImageResource(R.drawable.remove);
            }


    }
}
