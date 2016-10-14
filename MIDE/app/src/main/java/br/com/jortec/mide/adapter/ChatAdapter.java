package br.com.jortec.mide.adapter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import br.com.jortec.mide.R;
import br.com.jortec.mide.dominio.Chat;
import br.com.jortec.mide.dominio.OrdemServico;
import br.com.jortec.mide.interfaces.RecyclerViewOnclickListener;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;

/**
 * Created by Jorliano on 04/03/2016.
 */
public class ChatAdapter extends RealmBasedRecyclerViewAdapter<Chat, ChatAdapter.ViewHolder> {

    private RecyclerViewOnclickListener recyclerViewOnclickListener;
    Context context;


    public ChatAdapter(Context ccontext, RealmResults<Chat> realmResults,
                       boolean automaticUpdate, boolean animacaoTipo) {

        super(ccontext, realmResults, automaticUpdate, animacaoTipo);
        this.context = context;
    }

    public class ViewHolder extends RealmViewHolder implements View.OnClickListener {

        LinearLayout llContainerDireita;
        TextView tvHoraDireita;
        TextView tvMensageDireita;
        ImageView imCheckDireita;

        LinearLayout llContainerEsquerda;
        TextView tvHoraEsquerda;
        TextView tvMensageEsquerda;
        ImageView imCheckEsquerda;

        public ViewHolder(LinearLayout container) {
            super(container);

            llContainerEsquerda = (LinearLayout) container.findViewById(R.id.ll_container_esquerda);
            tvHoraEsquerda = (TextView)container.findViewById(R.id.tv_chate_hora_esquerda);
            tvMensageEsquerda = (TextView)container.findViewById(R.id.tv_chate_mensage_esquerda);

            llContainerDireita = (LinearLayout) container.findViewById(R.id.ll_container_direita);
            tvHoraDireita = (TextView)container.findViewById(R.id.tv_chate_hora_direita);
            tvMensageDireita = (TextView)container.findViewById(R.id.tv_chate_mensage_direita);
            imCheckDireita = (ImageView) container.findViewById(R.id.iv_read_icon_direita);

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

        View v = inflater.inflate(R.layout.item_menssage, viewGroup, false);
        ViewHolder vh = new ViewHolder((LinearLayout) v);

        return vh;
    }


    @Override
    public void onBindRealmViewHolder(ViewHolder viewHolder, int position) {


        Chat chat = realmResults.get(position);



        if( chat.getRemetente().equals("sistema") ){
            viewHolder.llContainerEsquerda.setVisibility(View.VISIBLE);
            viewHolder.llContainerDireita.setVisibility(View.GONE);
           // viewHolder.tvNicknameLeft.setText(m.getUserFrom().getNickname());
            viewHolder.tvHoraEsquerda.setText(chat.getHora());
            viewHolder.tvMensageEsquerda.setText( chat.getMensage() );


        }
        else{
            viewHolder.llContainerDireita.setVisibility(View.VISIBLE);
            viewHolder.llContainerEsquerda.setVisibility(View.GONE);
            //viewHolder.tvNicknameRight.setText(m.getUserFrom().getNickname());
            viewHolder.tvHoraDireita.setText(chat.getHora());
            viewHolder.tvMensageDireita.setText( chat.getMensage() );

            viewHolder
                    .imCheckDireita
                    .setImageResource(chat.getEstatus().equals("desmarcada") ? R.drawable.timer : R.drawable.check_msg);
        }



    }
}
