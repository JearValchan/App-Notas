package com.example.misnotas

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import com.example.misnotas.Entities.Nota
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.nota_layout.view.*
import java.io.*
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    var notas = ArrayList<Nota>()
    lateinit var adaptador: AdaptadorNotas

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //notasDePrueba()

        fab.setOnClickListener {
            var intent = Intent(this, AgregarNotaActivity::class.java)
            startActivityForResult(intent, 123)
        }

        leerNotas()

        adaptador = AdaptadorNotas(this, notas)
        listview.adapter = adaptador
    }

    fun leerNotas(){
        notas.clear()
        var carpeta = File(ubicacion().absolutePath)

        if(carpeta.exists()){
            var archivos = carpeta.listFiles()
            if (archivos != null){
                for (archivo in archivos){
                    leerArchivo(archivo)
                }
            }
        }
    }

    fun leerArchivo(archivo: File){
        val fis = FileInputStream(archivo)
        val di = DataInputStream(fis)
        val br = BufferedReader(InputStreamReader(di))
        var strLine: String? = br.readLine()
        var myData = ""

        while (strLine != null){
            myData = myData + strLine
            strLine = br.readLine()
        }
        br.close()
        di.close()
        fis.close()

        var nombre = archivo.name.substring(0, archivo.name.length-4)
        var nota = Nota(nombre,myData)
        notas.add(nota)
    }

    private fun ubicacion(): File{
        val folder = File(getExternalFilesDir("/"), "notas")
        if (!folder.exists()){
            folder.mkdir()
        }
        return folder
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 123){
            leerNotas()
            adaptador.notifyDataSetChanged()
        }
    }

    class AdaptadorNotas: BaseAdapter {
        lateinit var context: Context
        var notas = ArrayList<Nota>()

        constructor(context: Context, notas:ArrayList<Nota>) {
            this.context = context
            this.notas = notas
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var inflator = LayoutInflater.from(context)
            var vista = inflator.inflate(R.layout.nota_layout, null)
            var nota = notas[position]

            vista.titulo.text = nota.titulo
            vista.contenido.text = nota.contenido

            vista.borrar.setOnClickListener {
                eliminiar(nota.titulo)
                notas.remove(nota)
                this.notifyDataSetChanged()
            }

            return vista
        }

        override fun getItem(position: Int): Any {
            return notas[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return notas.size
        }

        private fun eliminiar(titulo:String){
            if (titulo == ""){
                Toast.makeText(context, "Error: Titulo vacio", Toast.LENGTH_SHORT).show()
            }else{
                try {
                    val archivo = File(ubicacion(), titulo+".txt")
                    archivo.delete()
                    Toast.makeText(context, "Se eliminó el archivo", Toast.LENGTH_SHORT).show()
                }catch (e:Exception){
                    Toast.makeText(context, "No se pudo eliminar el archivo", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun ubicacion(): String{
            val album = File(context.getExternalFilesDir("/"), "notas")
            if(!album.exists()){
                album.mkdir()
            }
            return album.absolutePath
        }
    }
}
