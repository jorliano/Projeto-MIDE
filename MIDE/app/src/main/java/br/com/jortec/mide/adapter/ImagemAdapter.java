package br.com.jortec.mide.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import br.com.jortec.mide.R;
import br.com.jortec.mide.interfaces.RecyclerViewOnclickListener;
import br.com.jortec.mide.service.SerializableBitmap;

/**
 * Created by Jorliano on 03/03/2016.
 */
public class ImagemAdapter extends RecyclerView.Adapter<ImagemAdapter.MyViewHolder> {
    private List<Uri> imagens;
    private LayoutInflater inflater;
    private RecyclerViewOnclickListener recyclerViewOnclickListener;
    Context c;

    public ImagemAdapter(Context c, List l){
        imagens = l;
        this.c = c;
        inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = inflater.inflate(R.layout.item_recyclerview_card, viewGroup, false);
        MyViewHolder mvh= new MyViewHolder(v);
        return mvh;
    }

    public void setRecyclerViewOnclickListener(RecyclerViewOnclickListener r) {
        recyclerViewOnclickListener = r;
    }

    public void removeItem(int position) {
        Log.i("LOG", "imagem a ser removida " + imagens.get(position));

        File file = new File(imagens.get(position).getPath());
        if (file.isFile())
            file.delete();

        imagens.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, imagens.size());
    }

  /*  public void desmarcarItem(int position) {
        ImageView iv = new ImageView(c);
        iv.setImageBitmap(imagens.get(position));
        iv.setBackgroundColor(c.getResources().getColor(R.color.md_white_1000));

        imagens.remove(position);
        notifyItemRemoved(position);
        imagens.add(iv);
        notifyItemInserted(imagens.size());
    }*/

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {


       // viewHolder.imagem.setImageBitmap(imagens.get(position).getBitmap());
        Log.i("LOG"," Caminho da imagem "+imagens.get(position));

        Picasso picasso = new Picasso.Builder(c)
                .listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                       exception.printStackTrace();
                    }
                })
                .build();
        picasso.load(imagens.get(position))
                .fit()
                .centerCrop()
                .into(viewHolder.imagem);

        viewHolder.imagemMarcador.setImageResource(0);
    }
    @Override
    public int getItemCount() {
        return imagens.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        public ImageView imagem;
        public ImageView imagemMarcador;

        public MyViewHolder(View itemView) {
            super(itemView);
            imagem = (ImageView) itemView.findViewById(R.id.imagem_card);
            imagemMarcador = (ImageView) itemView.findViewById(R.id.imagem_marcador);

            if(imagemMarcador != null ){
                imagemMarcador.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (recyclerViewOnclickListener != null) {
                recyclerViewOnclickListener.onclickListener(v, getPosition());
            }
        }
    }
}


