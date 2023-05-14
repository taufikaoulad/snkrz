package cat.copernic.taufik.snkrz.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import cat.copernic.taufik.snkrz.R
import cat.copernic.taufik.snkrz.R.id
import cat.copernic.taufik.snkrz.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Clase MainActivity que extiende AppCompatActivity.
 */
class MainActivity : AppCompatActivity(){

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private var bd = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth

    var tipoUsuario = false

    /**
     * Método onCreate que se llama cuando se crea la actividad.
     * @param savedInstanceState Objeto Bundle que contiene el estado previamente guardado de la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        // Inflar el layout de la actividad y asignarlo a la variable binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Establecer el layout como contenido de la actividad
        setContentView(binding.root)

        //Codigo para recupear el valor esAdmin para luego gestionar las vistas
        val email = FirebaseAuth.getInstance().currentUser!!.email
        lifecycleScope.launch {
            try {
                val snapshot = withContext(Dispatchers.IO) {
                    bd.collection("Usuarios").document(email.toString()).get().await()
                }
                val data = snapshot.data
                if (data != null) {

                    val esAdmin = data["esAdmin"] as? Boolean ?: false
                    tipoUsuario = esAdmin

                    if (!tipoUsuario) {
                        binding.navView.menu.findItem(id.crearSneaker)?.isVisible = false
                    }
                }
            } catch (e: Exception) {
                //Toast.makeText(requireActivity(), "Failed!", Toast.LENGTH_LONG).show()
            }
        }

        // Establecer la barra de herramientas
        setSupportActionBar(binding.appBarMain.toolbar)

        // Declarar variables para el cajón de navegación y la vista de navegación
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        // Inicializar el controlador de navegación
        val navController = findNavController(id.nav_host_fragment_content_main)

        // Configurar la barra de aplicaciones con los elementos de la vista y el cajón de navegación
        appBarConfiguration = AppBarConfiguration(
            setOf(
                id.pantallaPrincipalSneakerList,
                id.perfil,
                id.crearSneaker,
                id.politicaPrivacidad
            ), drawerLayout
        )

        // Configurar la barra de aplicaciones con el controlador de navegación
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Configurar la vista de navegación con el controlador de navegación
        navView.setupWithNavController(navController)

        // Configurar el escuchador de selección de elementos de menú de navegación
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.signOut -> {
                    //Cerrar sesión y navegar hacia la actividad de inicio de sesión
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    true
                }
                else -> {
                    // Navegar a la opción de menú seleccionada
                    val navController = findNavController(R.id.nav_host_fragment_content_main)
                    navController.navigate(menuItem.itemId)
                    // Cerrar el cajón de navegación
                    binding.drawerLayout.closeDrawer(GravityCompat.START) // cierra el menu
                    true
                }
            }
        }
    }

    /**
     * Método onSupportNavigateUp que se llama cuando se presiona el botón de navegación hacia arriba en la barra de aplicaciones.
     * @return Devuelve true si la navegación hacia arriba se ha realizado correctamente, de lo contrario, devuelve el valor devuelto por la superclase.
     */
    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}