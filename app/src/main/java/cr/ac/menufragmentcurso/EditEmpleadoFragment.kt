package cr.ac.menufragmentcurso

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.squareup.picasso.Picasso
import cr.ac.menufragmentcurso.entity.Empleado
import cr.ac.menufragmentcurso.repository.EmpleadoRepository
import java.io.ByteArrayOutputStream

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val PICK_IMAGE=30
/**
 * A simple [Fragment] subclass.
 * Use the [EditEmpleadoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */

class EditEmpleadoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var empleado: Empleado? = null
    lateinit var img_avatar:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            empleado=it.get(ARG_PARAM1) as Empleado?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        var view:View =inflater.inflate(R.layout.fragment_edit_empleado, container, false)

        val nombre=view.findViewById<TextView>(R.id.edit_SNombre)
        nombre.setText(empleado?.nombre)

        val identificacion=view.findViewById<TextView>(R.id.edit_SId)
        identificacion.setText(empleado?.identificacion)

        val puesto=view.findViewById<TextView>(R.id.edit_SPuesto)
        puesto.setText(empleado?.puesto)

        val departamento=view.findViewById<TextView>(R.id.edit_SDepartamento)
        departamento.setText(empleado?.departamento)
        img_avatar= view.findViewById(R.id.edit_foto)




        view.findViewById<Button>(R.id.edit_eliminar).setOnClickListener{ Eliminar(1) }
        view.findViewById<Button>(R.id.edit_editar).setOnClickListener{ Editar(identificacion.text.toString()) }

        if(empleado?.avatar != ""){
            img_avatar.setImageBitmap(empleado?.avatar?.let { decodeImage(it) })
        }

        img_avatar.setOnClickListener{
            var gallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(gallery, PICK_IMAGE)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode== PICK_IMAGE && resultCode==RESULT_OK){
            var imageUri = data?.data

            Picasso.get()
                .load(imageUri)
                .resize(120,120)
                .centerCrop()
                .into(img_avatar)

        }
    }
    private fun encodeImage(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT).replace("\n","")
    }

    private fun decodeImage (b64 : String): Bitmap{
        val imageBytes = Base64.decode(b64, 0)
        return  BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun Eliminar(identificacion:Int){

        val builder = AlertDialog.Builder(context)
        builder.setMessage("¿Desea modificar el registro?")
            .setCancelable(false)
            .setPositiveButton("Sí") { dialog, id ->
                empleado?.let { EmpleadoRepository.instance.delete(it) }
            }
            .setNegativeButton(
                "No"
            ) { dialog, id ->
                // logica del no
            }
        val alert = builder.create()
        alert.show()

        /*var fragmento : Fragment = CamaraFragment.newInstance("Camara" )
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.home_content, fragmento)
            ?.commit()
        activity?.setTitle("Camara")*/
    }
    fun Editar(identificacion:String){


        val edit_nombre = view?.findViewById<TextView>(R.id.edit_SNombre)
        val edit_identificacion = view?.findViewById<TextView>(R.id.edit_SId)
        val edit_puesto = view?.findViewById<TextView>(R.id.edit_SPuesto)
        val edit_departamento = view?.findViewById<TextView>(R.id.edit_SDepartamento)

        //  Rellena los campos para editar
        empleado?.nombre = edit_nombre?.text.toString()
        empleado?.puesto = edit_puesto?.text.toString()
        empleado?.identificacion = edit_identificacion?.text.toString()
        empleado?.departamento = edit_departamento?.text.toString()
        empleado?.avatar = encodeImage(img_avatar.drawable.toBitmap())!!


        empleado?.let { EmpleadoRepository.instance.edit(it) }


        Salir()
    }
    fun Salir(){
        val fragmento: Fragment = CamaraFragment.newInstance("Camara")
        fragmentManager
            ?.beginTransaction()
            ?.replace(R.id.home_content, fragmento)
            ?.commit()
        activity?.setTitle("Camara")
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment EditEmpleadoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(empleado: Empleado) =
            EditEmpleadoFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, empleado)
                }
            }
    }
}