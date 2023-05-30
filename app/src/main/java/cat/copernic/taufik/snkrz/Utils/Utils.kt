package cat.copernic.taufik.snkrz.Utils

import android.view.View
import com.google.android.material.snackbar.Snackbar
import android.app.AlertDialog
import android.content.Context

class Utils {
    companion object {
        fun mostrarMensaje(mensaje: String, rootView: View) {
            val snackbar = Snackbar.make(rootView, mensaje, Snackbar.LENGTH_SHORT)
            snackbar.show()
        }

        fun showAlert(mensaje: String, mensaje2: String, context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(mensaje2)
            builder.setMessage(mensaje)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }

        fun showAlert2(mensaje: String, context: Context) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("ERROR") //siempre sale error tener en cuenta
            builder.setMessage(mensaje)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }
}