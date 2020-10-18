package com.aplavanzadas.medicalcenterappointment;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/* Esta clase sirve como adaptador para que cada item del spinner en la activity perfil medico
    tenga una apariencia personalizada */
public class AdapterLugar extends ArrayAdapter<LugarItem> {

    public AdapterLugar(@NonNull Context context, LugarItem[] listaLugares) {
        super(context, 0, listaLugares);
    }

    /* En este método establecemos la apariencia del item cuando está seleccionado utilizando el
        archivo spinner_lugar.xml */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent, R.layout.spinner_lugar);
    }

    /* En este método establecemos la apariencia del item cuando se despliega la lista
        utilizando el archivo spinner_dropdown_lugar.xml */
    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent, R.layout.spinner_dropdown_lugar);
    }

    private static class ViewHolder {
        private TextView tvSubtitle;
        private TextView tvAddress;
    }

    private View initView(int position, View convertView, ViewGroup parent, int idLayout) {
        ViewHolder holder;

        if (convertView == null) {
            // La vista no está creada, así que la crea. Cuando vuelva a comprobar
            //  si existe, reutilizará el objeto convertView para ahorrarse la creación de un nuevo objeto.
            convertView = LayoutInflater.from(getContext()).inflate(idLayout, parent, false);

            // Creamos un objeto de la clase ViewHolder y hacemos que cada atributo referencie
            //  a un elemento del layout. Esta referencia se mantiene y cuando reutilicemos la vista
            //  convertView ya no tendrá que llamar al método findViewById()
            holder = new ViewHolder();
            holder.tvSubtitle = (TextView) convertView.findViewById(R.id.spinner_subtitle);
            holder.tvAddress = (TextView) convertView.findViewById(R.id.spinner_address);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        LugarItem currentItem = getItem(position);

        holder.tvSubtitle.setText(currentItem.getSubtitulo());
        holder.tvAddress.setText(currentItem.getDireccion());

        return convertView;
    }
}
