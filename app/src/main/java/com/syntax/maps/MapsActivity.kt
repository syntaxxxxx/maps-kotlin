package com.syntax.maps

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.places.ui.PlaceAutocomplete
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.syntax.maps.helper.DirectionMapsV2
import com.syntax.maps.helper.GPStrack
import com.syntax.maps.helper.HeroHelper
import com.syntax.maps.model.Distance
import com.syntax.maps.model.Duration
import com.syntax.maps.model.LegsItem
import com.syntax.maps.model.ResponseWaypoint
import com.syntax.maps.model.RoutesItem
import com.syntax.maps.network.RetrofitConfig

import java.io.IOException
import java.util.Locale

import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : FragmentActivity(), OnMapReadyCallback {
    @BindView(R.id.edtawal)
    internal var edtawal: EditText? = null
    @BindView(R.id.edtakhir)
    internal var edtakhir: EditText? = null
    @BindView(R.id.textjarak)
    internal var textjarak: TextView? = null
    @BindView(R.id.textwaktu)
    internal var textwaktu: TextView? = null
    @BindView(R.id.textharga)
    internal var textharga: TextView? = null
    @BindView(R.id.linearLayout)
    internal var linearLayout: LinearLayout? = null
    @BindView(R.id.btnlokasiku)
    internal var btnlokasiku: Button? = null
    @BindView(R.id.btnpanorama)
    internal var btnpanorama: Button? = null
    @BindView(R.id.linearbottom)
    internal var linearbottom: LinearLayout? = null
    @BindView(R.id.spinmode)
    internal var spinmode: Spinner? = null
    @BindView(R.id.relativemap)
    internal var relativemap: RelativeLayout? = null
    @BindView(R.id.frame1)
    internal var frame1: FrameLayout? = null

    private var mMap: GoogleMap? = null
    private var googleApiClient: GoogleApiClient? = null
    private var gps: GPStrack? = null
    private var lat: Double = 0.toDouble()
    private var lon: Double = 0.toDouble()
    private var nama_lokasi: String? = null
    private var lokasisaya: LatLng? = null
    private var latawal: Double = 0.toDouble()
    private var lonawal: Double = 0.toDouble()
    private var lokasiawal: LatLng? = null
    private var latakhir: Double = 0.toDouble()
    private var lonakhir: Double = 0.toDouble()
    private var routes: List<RoutesItem>? = null
    private var legs: List<LegsItem>? = null
    private var distance: Distance? = null
    private var duration: Duration? = null
    private var datapoly: String? = null

    companion object {
        private val REQUEST_LOCATION = 1
        private val REQAWAL = 1
        private val REQAKHIR = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        ButterKnife.bind(this)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        cekstatusGps()
    }

    /**
     * method ini digunakan untuk mengecek apakah status gps kita sudah aktif atau belum
     * */
    private fun cekstatusGps() {
        val manager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps already enabled", Toast.LENGTH_SHORT).show()
            //     finish();
        }
        // Todo 3 Location Already on  ... end

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Gps not enabled", Toast.LENGTH_SHORT).show()
            //todo 4 menampilkan popup untuk mengaktifkan GPS (allow or not)
            enableLoc()
        }
    }

    /**
     * method ini digunakan untuk mengaktifkan location saat ini
     * */
    private fun enableLoc() {
        if (googleApiClient == null) {
            googleApiClient = GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(object : GoogleApiClient.ConnectionCallbacks {
                        override fun onConnected(bundle: Bundle?) {

                        }

                        override fun onConnectionSuspended(i: Int) {
                            googleApiClient!!.connect()
                        }
                    })
                    .addOnConnectionFailedListener { connectionResult -> Log.d("Location error", "Location error " + connectionResult.errorCode) }.build()
            googleApiClient!!.connect()

            val locationRequest = LocationRequest.create()
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            locationRequest.interval = (30 * 1000).toLong()
            locationRequest.fastestInterval = (5 * 1000).toLong()
            val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)

            builder.setAlwaysShow(true)

            val result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build())
            result.setResultCallback { result ->
                val status = result.status
                when (status.statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(this@MapsActivity, REQUEST_LOCATION)
                        finish()
                    } catch (e: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }

                }
            }
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
    override fun onMapReady(googleMap: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), 100
                )
            }
            return

        } else {
            mMap = googleMap
            akseslokasiku()
        }
        mMap!!.clear()
    }

    private fun akseslokasiku() {
        gps = GPStrack(this)
        if (gps!!.canGetLocation() && mMap != null) {
            lat = gps!!.latitude
            lon = gps!!.longitude
            nama_lokasi = convertlocation(lat, lon)
            // get lat long
            Toast.makeText(this, "lat:$lat\nlon:$lon", Toast.LENGTH_SHORT).show()
            lokasisaya = LatLng(lat, lon)
            mMap!!.addMarker(MarkerOptions().position(lokasisaya!!).title(nama_lokasi)).setIcon(
                    BitmapDescriptorFactory.fromResource(R.mipmap.ic_map))
            mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasisaya, 16f))
            mMap!!.uiSettings.isCompassEnabled
            mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            mMap!!.uiSettings.isMyLocationButtonEnabled
            edtawal!!.setText(nama_lokasi)
        }
    }

    private fun convertlocation(lat: Double, lon: Double): String? {
        nama_lokasi = null
        val geocoder = Geocoder(this@MapsActivity, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size > 0) {
                nama_lokasi = list[0].getAddressLine(0) + "" + list[0].countryName

                //fetch data from addresses
            } else {
                Toast.makeText(this, "kosong", Toast.LENGTH_SHORT).show()
                //display Toast message
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return nama_lokasi
    }

    @OnClick(R.id.edtawal, R.id.edtakhir, R.id.btnlokasiku, R.id.btnpanorama)

    /**
     * method ini digunakan untuk place auto complete ketika field location dipilih
     * */
    fun onViewClicked(view: View) {
        when (view.id) {
            R.id.edtawal -> try {
                intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(
                        this@MapsActivity)
                startActivityForResult(intent, REQAWAL)
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            }

            R.id.edtakhir -> try {
                intent = PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(
                        this@MapsActivity)
                startActivityForResult(intent, REQAKHIR)
            } catch (e: GooglePlayServicesNotAvailableException) {
                e.printStackTrace()
            } catch (e: GooglePlayServicesRepairableException) {
                e.printStackTrace()
            }

            R.id.btnlokasiku -> akseslokasiku()
            R.id.btnpanorama -> aksespanorama()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val p = PlaceAutocomplete.getPlace(this, data!!)
        if (requestCode == REQAWAL && resultCode == Activity.RESULT_OK) {
            latawal = p.latLng.latitude
            lonawal = p.latLng.longitude
            nama_lokasi = p.name.toString()
            edtawal!!.setText(nama_lokasi)
            addmarker(latawal, lonawal)
        } else if (requestCode == REQAKHIR && resultCode == Activity.RESULT_OK) {
            latakhir = p.latLng.latitude
            lonakhir = p.latLng.longitude
            nama_lokasi = p.name.toString()
            edtakhir!!.setText(nama_lokasi)
            addmarker(latakhir, lonakhir)
            aksesrute()
        }
    }

    /**
     * method ini digunakan untuk menggambar polyline dari jarak awal sampai jarak akhir
     * */
    private fun aksesrute() {
        val dialog = ProgressDialog.show(this, "Proses", "Loading ... ")
        val apiService = RetrofitConfig.instanceRetrofit
        val api = getText(R.string.google_maps_key).toString()
        val waypointCall = apiService.request_route(
                edtawal!!.text.toString(),
                edtakhir!!.text.toString(),
                api)
        waypointCall.enqueue(object : Callback<ResponseWaypoint> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<ResponseWaypoint>, response: Response<ResponseWaypoint>) {
                dialog.dismiss()
                if (response.isSuccessful) {
                    val status = response.body()!!.status
                    if (status == "OK") {
                        routes = response.body()!!.routes
                        legs = routes!![0].legs
                        distance = legs!![0].distance
                        duration = legs!![0].duration
                        textjarak!!.text = distance!!.text.toString()
                        textwaktu!!.text = duration!!.text.toString()
                        val harga = Math.ceil(java.lang.Double.valueOf((distance!!.value / 1000).toDouble())!!)
                        val total = harga * 1000
                        textharga!!.text = "Rp." + HeroHelper.toRupiahFormat2(total.toString())
                        val mapsV2 = DirectionMapsV2(this@MapsActivity)
                        datapoly = routes!![0].overviewPolyline?.points
                        mapsV2.gambarRoute(mMap, datapoly)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseWaypoint>, t: Throwable) {
                t.message
            }
        })
    }

    /**
     * method ini digunakan untuk menambahkan marker sesuai dengan location awal dan akhir yang kita pilih
     * */
    private fun addmarker(lat: Double, lon: Double) {
        lokasiawal = LatLng(lat, lon)
        nama_lokasi = convertlocation(lat, lon)
        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(lokasiawal, 15f))
        mMap!!.addMarker(MarkerOptions().position(lokasiawal!!).title(nama_lokasi))
    }

    /**
     * method ini digunakan untuk aktifkan mode panorama pada maps
     * */
    private fun aksespanorama() {
        relativemap!!.visibility = View.GONE
        frame1!!.visibility = View.VISIBLE
        val panoramaFragment = supportFragmentManager.findFragmentById(R.id.panorama) as SupportStreetViewPanoramaFragment
        panoramaFragment.getStreetViewPanoramaAsync { streetViewPanorama -> streetViewPanorama.setPosition(lokasisaya) }

    }
}
