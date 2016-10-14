package br.com.jortec.mide.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import br.com.jortec.mide.FecharOsActivity;
import br.com.jortec.mide.R;
import br.com.jortec.mide.Util.Formate;
import br.com.jortec.mide.dominio.OrdemServico;
import br.com.jortec.mide.interfaces.Comunicador;
import de.greenrobot.event.EventBus;

/**
 * Created by Jorliano on 17/04/2016.
 */
public class OrdemServicoFragment extends Fragment implements View.OnClickListener, Comunicador{

    private TextView tvNome;
    private TextView tvEndereco;
    private TextView tvBairro;
    private TextView tvCidade;
    private TextView tvTelefone;
    private TextView tvAutenticacao;
    private TextView tvDadosAcesso;
    private TextView tvDescricao;
    private TextView tvContato;
    private TextView tvData;
    private TextView tvTipoOs;
    private FloatingActionMenu fab;
    private FloatingActionButton fabNavegar;
    private FloatingActionButton fabEncerrar;

    private OrdemServico os;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_ordem_servico, null);

        iniciarViews(v);


        return v;
    }

    public void iniciarViews(View v) {

        tvNome = (TextView) v.findViewById(R.id.tv_nome_os);
        tvEndereco = (TextView) v.findViewById(R.id.tv_endereco_os);
        tvBairro = (TextView) v.findViewById(R.id.tv_bairro_os);
        tvCidade = (TextView) v.findViewById(R.id.tv_cidade_os);
        tvTelefone = (TextView) v.findViewById(R.id.tv_telefone_os);
        tvAutenticacao = (TextView) v.findViewById(R.id.tv_autenticacao);
        tvDadosAcesso = (TextView) v.findViewById(R.id.tv_dados_acesso);
        tvDescricao = (TextView) v.findViewById(R.id.tv_descricao_os);
        tvContato = (TextView) v.findViewById(R.id.tv_contato_os);
        tvData = (TextView) v.findViewById(R.id.tv_data_os);
        tvTipoOs = (TextView) v.findViewById(R.id.tv_tipo_os);


        fab = (FloatingActionMenu) v.findViewById(R.id.fab);
        fabNavegar = (FloatingActionButton) v.findViewById(R.id.fab_navegar);
        fabEncerrar = (FloatingActionButton) v.findViewById(R.id.fab_encerrar);

        fabNavegar.setOnClickListener(this);
        fabEncerrar.setOnClickListener(this);
        fab.setClosedOnTouchOutside(true);

    }

    public void preenceherDados() {
        tvTipoOs.setText(os.getTipo());
        tvContato.setText(os.getContato());
        tvNome.setText(os.getNomeCliente());
        tvEndereco.setText(os.getEndereco());
        tvBairro.setText(os.getBairro());
        tvCidade.setText(os.getCidade());
        tvTelefone.setText(os.getTelefone());
        tvAutenticacao.setText(os.getAutenticacao());
        tvDadosAcesso.setText(os.getDadosAcesso());
        tvDescricao.setText(os.getDescricao());
        tvData.setText(Formate.dataParaString(os.getData()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_navegar:
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + tvEndereco.getText().toString() + " "
                        + tvBairro.getText().toString() + ",+"
                        + tvCidade.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");

                if (mapIntent.resolveActivity(v.getContext().getPackageManager()) != null) {
                    startActivity(mapIntent);
                } else {
                    Toast.makeText(v.getContext(), "Aplicativo da GoogleMaps não esta instalado", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.fab_encerrar:
                Log.i("LOG", " id do serviço no bundle" + os.getIdServico());
                startActivityForResult(new Intent(v.getContext(),FecharOsActivity.class).
                        putExtra(OrdemServico.FECHAMENTO_OS, os.getId())
                        .putExtra("id_servico", os.getIdServico()), OrdemServico.BUNDLE_ONRESULT);


                break;
        }
    }

    /*LISTENER
    public void onEvent(OrdemServico os) {
      this.os = os;
      preenceherDados();
    }*/

    @Override
    public void responde(Object obj) {
        this.os = (OrdemServico) obj;
        preenceherDados();
    }


}
