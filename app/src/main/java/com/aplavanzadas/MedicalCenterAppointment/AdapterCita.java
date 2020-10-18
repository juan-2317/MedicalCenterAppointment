package com.aplavanzadas.medicalcenterappointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

/* Esta clase sirve como adaptador para que cada item del listview en la activity perfil usuario
    tenga una apariencia personalizada como está en el archivo listview_item.xml */
public class AdapterCita extends BaseAdapter implements Filterable {

    private Context contexto;
    private ArrayList<CitaItem> listaObjetos;
    private ArrayList<CitaItem> filtroObjetos;
    private CustomFilter filtro;

    public AdapterCita(Context contexto, ArrayList<CitaItem> listaObjetos) {
        super();
        this.contexto = contexto;
        this.listaObjetos = listaObjetos;
        this.filtroObjetos = listaObjetos;
        this.filtro = null;
    }

    @Override
    public int getCount() {
        return listaObjetos.size();
    }

    @Override
    public Object getItem(int position) {
        return listaObjetos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        private TextView tvDoctor;
        private TextView tvLugar;
        private TextView tvDireccion;
        private TextView tvTipoCita;
        private TextView tvConsultorio;
        private TextView tvFecha;
        private TextView tvHora;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            // La vista no está creada, así que la crea. Cuando vuelva a comprobar
            //  si existe, reutilizará el objeto convertView para ahorrarse la creación de un nuevo objeto.
            convertView = LayoutInflater.from(contexto).inflate(R.layout.listview_item, null);

            // Creamos un objeto de la clase ViewHolder y hacemos que cada atributo referencie
            //  a un elemento del layout. Esta referencia se mantiene y cuando reutilicemos la vista
            //  convertView ya no tendrá que llamar al método findViewById()
            holder = new ViewHolder();
            holder.tvDoctor = (TextView) convertView.findViewById(R.id.tvl_doctor);
            holder.tvLugar = (TextView) convertView.findViewById(R.id.tvl_lugar);
            holder.tvDireccion = (TextView) convertView.findViewById(R.id.tvl_direccion);
            holder.tvTipoCita = (TextView) convertView.findViewById(R.id.tvl_tipo_cita);
            holder.tvConsultorio = (TextView) convertView.findViewById(R.id.tvl_consultorio);
            holder.tvFecha = (TextView) convertView.findViewById(R.id.tvl_fecha);
            holder.tvHora = (TextView) convertView.findViewById(R.id.tvl_hora);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CitaItem currentItem = listaObjetos.get(position);
        holder.tvDoctor.setText(currentItem.getDoctor());
        holder.tvLugar.setText(currentItem.getLugar());
        holder.tvDireccion.setText(currentItem.getDireccion());
        holder.tvTipoCita.setText(currentItem.getTipoCita());
        holder.tvConsultorio.setText(Integer.toString(currentItem.getConsultorio()));
        holder.tvFecha.setText(currentItem.getFecha());
        holder.tvHora.setText(currentItem.getHora());
        return convertView;
    }

    // Este método servirá para que mas adelante se puedan hacer busquedas en el listview
    @Override
    public Filter getFilter() {
        if (filtro == null) {
            filtro = new CustomFilter();
        }

        return filtro;
    }

    private class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                // Pasamos a mayusculas
                constraint = constraint.toString().toUpperCase();

                ArrayList<CitaItem> filtro = new ArrayList<>();

                for (Integer i = 0; i < listaObjetos.size(); i++) {
                    // Buscar por doctor o lugar
                    if (listaObjetos.get(i).getLugar().toUpperCase().contains(constraint)
                            || listaObjetos.get(i).getDoctor().toUpperCase().contains(constraint)) {
                        CitaItem d = new CitaItem(listaObjetos.get(i).getId(), listaObjetos.get(i).getDoctor(),
                                listaObjetos.get(i).getLugar(), listaObjetos.get(i).getDireccion(), listaObjetos.get(i).getTipoCita(),
                                listaObjetos.get(i).getConsultorio(), listaObjetos.get(i).getFecha(), listaObjetos.get(i).getHora());

                        filtro.add(d);
                    }
                }
                results.count = filtro.size();
                results.values = filtro;
            } else {
                results.count = filtroObjetos.size();
                results.values = filtroObjetos;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            listaObjetos = (ArrayList<CitaItem>) results.values;
            notifyDataSetChanged();

        }
    }
}
