package com.ulisesdiaz.mapaspoligonos

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    // Variables de los permisos que se van autilizar haciendo referencia a los permisos que se van autilizar
    private val permisoFineLocation = android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permisoCoarseLocation = android.Manifest.permission.ACCESS_COARSE_LOCATION

    // Variable que permite identificar el permiso mediante un nummero
    private val CODIGO_SOLICITUD_PERMISO = 100

    // Variable que permitira hacer el traking de ubicacion
    var locationRequest: LocationRequest? = null

    // Variable que administrara las actualizaciones de la ubicacion
    var callback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    /**
     * Ciclo de Vida onstart
     * Se activa cada vez que se inicie la aplicacion, cambie o la app pase a segundo plano
     * Condicinal donde si hay permisos obtiene la ubicion, caso contrario solicita los permisos
     */
    override fun onStart() {
        super.onStart()

        if (validarPermisosUbicacion()){
            //obtenerUbicacion()
        }else{
            pedirPermisos()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val exitoCambioMapa = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.estilo_mapa))

        if (!exitoCambioMapa){
            // Mencionar que ocurrio un problema al cambiar el tipo de mapa
        }

        // Asignacion de coordenadas de una ciudad
        val xalapaCity = LatLng(19.5426, -96.9137)

        // Se agrega un marcador con las coordenadas de la ciudad
        mMap.addMarker(MarkerOptions().position(xalapaCity).title("Mi ciudad"))

        // Se posiciona la camara deacuerdo a las coordenadas de la ciudad con un zoom de 14 y
        // una animacion de 3 segundos.
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(xalapaCity, 14f), 3000, null)

        // Se establecen coordenadas para dibujar una linea
        val coordenadas = PolylineOptions()
                .add(LatLng(19.541894560478745, -96.92362068742396))
                .add(LatLng(19.55342048875402, -96.91409348112904))
                .add(LatLng(19.53412926046978, -96.91632507897947))
                .add(LatLng(19.529608077651993, -96.91181256784661))
                .pattern(arrayListOf<PatternItem>(Dot(), Gap(10.0F))) // Especificar el tipo de la distanciade en un patron
                .color(Color.CYAN)
                .width(15.0F) // ancho de la liena

        // Se establecen coordenadas para dibujar un Polygon
        val coordenadas2 = PolygonOptions()
                .add(LatLng(19.538263369998656, -96.90305783773776))
                .add(LatLng(19.547160850429506, -96.90546109698333))
                .add(LatLng(19.543601917111015, -96.90872266310231))
                .add(LatLng(19.539881130191077, -96.9107825995985))
                .strokePattern(arrayListOf<PatternItem>(Dash(10.0F), Gap(10.0F))) // Especificar el tipo de la distanciade en un patron
                .strokeColor(Color.YELLOW) // Color de la liena
                .fillColor(Color.GREEN) // Color de relleno
                .strokeWidth(15.0F) // Cambia el grosor de la liena

        // Se establecen coordenada y radio para dibujar un circulo
        val coordenadas3 = CircleOptions()
                .center(LatLng(19.56034439665008, -96.90760686416687))
                .radius(450.0)
                .strokePattern(arrayListOf<PatternItem>(Dash(10.0F), Gap(10.0F))) // Especificar el tipo de la distanciade en un patron
                .strokeColor(Color.MAGENTA) // Color de la liena
                .fillColor(Color.WHITE) // Color de relleno
                .strokeWidth(15.0F) // Cambia el grosor de la liena

        // Se agrega al mapa las coordenadas de la Polyline
        mMap.addPolyline(coordenadas)
        // Se agrega al mapa las coordenadas de la Polygon
        mMap.addPolygon(coordenadas2)
        // Se agrega al mapa la cordenada como punto incial para dibujar el circulo
        mMap.addCircle(coordenadas3)

    }

    /**
     * Esta funcion permite mapear si el usuario otorgo permisos, es llamada una vez que se otorgaron los permisos o se denegaron
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode){
            CODIGO_SOLICITUD_PERMISO -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Obtener la ubicacion
                    //obtenerUbicacion()
                } else {
                    Toast.makeText(this, "No se otorgaron permisos para la ubicacion",
                            Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Funcion que valida si el usuario ya tiene permisos o se necesita pedir
     * Se compara el permiso que deseo usar con los que se declararon en el manifest
     * Regresa verdadero si estan los permisos otorgados
     */
    private fun  validarPermisosUbicacion(): Boolean{
        val hayUbicacionPrecisa = ActivityCompat.checkSelfPermission(
                this, permisoFineLocation) == PackageManager.PERMISSION_GRANTED
        val hayUbicacionOrdinaria =  ActivityCompat.checkSelfPermission(
                this, permisoCoarseLocation) == PackageManager.PERMISSION_GRANTED

        return hayUbicacionPrecisa && hayUbicacionOrdinaria
    }

    /**
     * Si el usuario no tiene permisos o son negados entra a esta funcion para solicitarlos
     * la variable proverContexto recibe un boleano de si se otorgo el permiso o no (true o false)
     * Solo se pide el permiso de Fine Location
     */
    private fun pedirPermisos(){
        val proveerContexto = ActivityCompat.shouldShowRequestPermissionRationale(
                this, permisoFineLocation)
        if (proveerContexto){
            // Mandar mensaje con explicacion adicional
            solicitudPermiso()
        }else{
            solicitudPermiso()
        }
    }

    /**
     * Se llama a requesPermissions para ingresar todos los permisos que funcionaran en la actividad.
     * Los permisos se ingresan por medio de un arreglo
     */
    private fun solicitudPermiso(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(permisoFineLocation, permisoCoarseLocation), CODIGO_SOLICITUD_PERMISO)
        }
    }
}