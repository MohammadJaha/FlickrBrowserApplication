package com.example.flickrbrowserapp

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.muddz.styleabletoast.StyleableToast
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import org.json.JSONObject
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val connection by lazy { FavoriteDatabase.getInstance(this).favoriteDao() }

    private lateinit var list: ArrayList<Data>
    private lateinit var favoriteList: ArrayList<Data>
    private lateinit var rvMain: RecyclerView
    private lateinit var favoriteShowRV: RecyclerView
    private lateinit var rvAdapter: RVAdapter
    private lateinit var rvFavoriteAdapter: RVAdapter
    private lateinit var llBottom: LinearLayout
    private lateinit var etWord: EditText
    private lateinit var btSearch: Button
    private lateinit var moreImage: ImageView
    private lateinit var search: String
    private var count= 10
    private var mode= 1
    private var mode2=1
    private lateinit var photosGrid: GridView
    private lateinit var gridAdapter: GridAdapter
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        return super.onPrepareOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.favorite -> {
                if (mode==1) {
                    this@MainActivity.title= "Favorites"
                    startFavorite()
                    item.setIcon(R.drawable.blank_heart)
                    favoriteShowRV.isVisible= true
                    rvMain.isVisible= false
                    moreImage.isVisible= false
                    llBottom.isVisible= false
                    photosGrid.isVisible= false
                    mode=2
                }
                else{
                    this@MainActivity.title = "Flickr Browser Application"
                    endFavorite()
                    item.setIcon(R.drawable.full_heart)
                    favoriteShowRV.isVisible = false
                    moreImage.isVisible = true
                    llBottom.isVisible = true
                    mode= 1
                    if (mode2==1) {
                        rvMain.isVisible = true
                    }
                    else{
                        photosGrid.isVisible= true
                    }
                }
                return true
            }
            R.id.viewWay -> {
                startFavorite()
                this@MainActivity.title = "Flickr Browser Application"
                endFavorite()
                mode= 1
                favoriteShowRV.isVisible = false
                moreImage.isVisible = true
                llBottom.isVisible = true
                if (mode2==1){
                    item.setIcon(R.drawable.list_view)
                    photosGrid.isVisible= true
                    rvMain.isVisible= false
                    mode2= 2
                }
                else{
                    item.setIcon(R.drawable.grid_view)
                    photosGrid.isVisible= false
                    rvMain.isVisible= true
                    mode2= 1
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun endFavorite() {
        for (image in list){
            image.checkBox= false
        }
        for (image in list)
            for (image2 in favoriteList)
                if (image.photo_id == image2.photo_id && image2.secretNumber == image.secretNumber)
                    image.checkBox= true
        rvAdapter.update()
        gridAdapter.notifyDataSetChanged()
    }

    private fun startFavorite() {
        for (image in list) {
            for (image2 in favoriteList){
                if (image.photo_id == image2.photo_id && image2.secretNumber == image.secretNumber)
                    image2.checkBox = image.checkBox
            }
        }
        favoriteList.removeAll { !it.checkBox }
        rvFavoriteAdapter.update()
        for (image in list){
            if (image.checkBox) {
                var check= false
                for (image2 in favoriteList)
                    if (image.photo_id == image2.photo_id && image2.secretNumber == image.secretNumber)
                        check= true
                if (!check)
                    favoriteList.add(image)
            }
        }
        for (image in favoriteList){
            image.checkBox =true
        }
        rvFavoriteAdapter.update()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        list= arrayListOf()
        favoriteList= arrayListOf()
        rvMain = findViewById(R.id.rvMain)
        favoriteShowRV= findViewById(R.id.favoriteShowRV)
        etWord = findViewById(R.id.etWord)
        btSearch = findViewById(R.id.btSearch)
        moreImage= findViewById(R.id.moreImages)
        llBottom= findViewById(R.id.llBottom)
        photosGrid = findViewById(R.id.imageGrid)

        CoroutineScope(IO).launch {
            val data= async {
                connection.gettingAllData()
            }.await()
            withContext(Main){
                favoriteList.addAll(data)

                rvFavoriteAdapter= RVAdapter(this@MainActivity,favoriteList,2)
                favoriteShowRV.adapter= rvFavoriteAdapter
                favoriteShowRV.layoutManager= LinearLayoutManager(this@MainActivity)

                rvFavoriteAdapter.setOnItemClickListener(object : RVAdapter.OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        val showImage= favoriteList[position]
                        val intent= Intent(this@MainActivity,ImageShow::class.java)
                        intent.putExtra("title",showImage.title)
                        intent.putExtra("serverID",showImage.server_id)
                        intent.putExtra("photoID",showImage.photo_id)
                        intent.putExtra("secretNumber",showImage.secretNumber)
                        startActivity(intent)
                    }
                })
            }
        }

        gridAdapter = GridAdapter(this,list)
        photosGrid.adapter = gridAdapter


        rvAdapter = RVAdapter(this@MainActivity,list,1)
        rvMain.adapter = rvAdapter
        rvMain.layoutManager = LinearLayoutManager(this)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Please Wait:")
        progressDialog.setCancelable(false)

        btSearch.setOnClickListener {
            if(etWord.text.isNotEmpty()) {
                progressDialog.show()
                count= 10
                search= etWord.text.toString().replace(",","%2C")
                search= search.replace(" ","&")
                requestAPI()
                moreImage.isVisible= true
            }
            else{
            Toast.makeText(this, "Please Enter Something", Toast.LENGTH_SHORT).show()
            }
        }

        moreImage.setOnClickListener{
            progressDialog.show()
            count+=10
            requestAPI()
        }

        photosGrid.setOnItemClickListener{
                _, _, position, _ ->
            val showImage= list[position]
            val intent= Intent(this@MainActivity,ImageShow::class.java)
            intent.putExtra("title",showImage.title)
            intent.putExtra("serverID",showImage.server_id)
            intent.putExtra("photoID",showImage.photo_id)
            intent.putExtra("secretNumber",showImage.secretNumber)
            startActivity(intent)
        }

        rvAdapter.setOnItemClickListener(object : RVAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                val showImage= list[position]
                val intent= Intent(this@MainActivity,ImageShow::class.java)
                intent.putExtra("title",showImage.title)
                intent.putExtra("serverID",showImage.server_id)
                intent.putExtra("photoID",showImage.photo_id)
                intent.putExtra("secretNumber",showImage.secretNumber)
                startActivity(intent)
            }
        })

    }

    private fun requestAPI(){
        CoroutineScope(IO).launch {
            val data = withContext(Dispatchers.Default) {
                getData(search,count)
            }
            if(data.isNotEmpty()){
                updateRV(data)
            }
        }
    }

    private fun getData(word: String,number: Int): String{
        Log.d("MyData","https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=60877df427a4d42fe3c1c257e98f9b2e&tags=$word&per_page=$number&format=json&nojsoncallback=2")
        var response = ""
        response = try {
            URL("https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=60877df427a4d42fe3c1c257e98f9b2e&tags=$word&per_page=$number&format=json&nojsoncallback=2").readText(Charsets.UTF_8)
        }catch (e: Exception){
            println("Error: $e")
            "Unable to get data"
        }
        return response
    }

    private suspend fun updateRV(result: String){
        withContext(Main) {
            try {
                list.clear()
                Log.d("MAIN", "DATA: $result")
                val jsonObject= JSONObject(result)
                val photos= jsonObject.getJSONObject("photos")
                val size= photos.getInt("perpage")
                val photo= photos.getJSONArray("photo")
                for (i in 0 until size){
                    val photoID= photo.getJSONObject(i).getString("id")
                    val photoSecret= photo.getJSONObject(i).getString("secret")
                    val photoServer= photo.getJSONObject(i).getString("server")
                    val photoTitle= photo.getJSONObject(i).getString("title")
                    var checkBox= false
                    for (image in favoriteList)
                        if (photoID == image.photo_id && photoSecret == image.secretNumber)
                            checkBox= true
                    list.add(Data(0,photoTitle,photoServer,photoID,photoSecret,checkBox))
                }
                rvAdapter.update()
                gridAdapter.notifyDataSetChanged()
                etWord.text.clear()
                val view: View? = this@MainActivity.currentFocus
                if (view != null) {
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)
                }
                if (list.size > 10)
                    scrollDown()
                progressDialog.dismiss()

            }
            catch (e:Exception){
                Toast.makeText(this@MainActivity,result, Toast.LENGTH_LONG).show()
                progressDialog.dismiss()
            }
        }
    }

    private fun scrollDown() {
        rvMain.scrollToPosition(list.size - 10)
    }

    override fun onStop() {
        val list= arrayListOf<Data>()
        CoroutineScope(IO).launch {
            val data = async {
                connection.gettingAllData()
            }.await()
            withContext(Main) {
                list.addAll(data)
                for (i in list){
                    var check= false
                    for (n in favoriteList)
                        if (i.title==n.title && i.secretNumber==n.secretNumber)
                            check=true
                    if (!check){
                        CoroutineScope(IO).launch {
                            connection.deleteFavorite(i)
                        }
                    }
                }
                for (i in favoriteList){
                    var check= false
                    for (n in list)
                        if (i.title==n.title && i.secretNumber==n.secretNumber)
                            check=true
                    if (!check){
                        CoroutineScope(IO).launch {
                            connection.addNewFavorite(i)
                        }
                    }
                }
            }
        }
        super.onStop()
    }

}