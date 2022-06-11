package br.com.ijbuenoapks.minhalistadecompras

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import br.com.ijbuenoapks.minhalistadecompras.configuracoes.RetrofitConfig
import br.com.ijbuenoapks.minhalistadecompras.models.Produto
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    //variaveis para requisição de permição e da camera
    private val PERMISSION_REQUEST_CAMERA = 0
    private lateinit var codeScanner: CodeScanner
    private var codBarScaneado : String = ""

    //variavel para a lista de produtos
    private val listaDeProduto : MutableList<Produto> = mutableListOf()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //variaveis para trabalhar com os campos e botoes
        val txtNomeProduto = findViewById<TextView>(R.id.txtNomeProduto)
        val txtValor = findViewById<TextView>(R.id.txtValorProduto)


        //recupero o scanner view
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView)

        //parametros iniciais pra o scanner
        codeScanner.camera = CodeScanner.CAMERA_BACK //<- seto comente como a de tras inicinalmente
        codeScanner.formats = CodeScanner.ALL_FORMATS //le tanto barcode quando qrcode
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false

        requestCamera()

        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                //Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
                codBarScaneado = it.text

                //busco na base de dados
                val call : Call<Produto?>? = RetrofitConfig().getProdutoService()!!.buscarCodigoDeBarras(
                    codBarScaneado  )
                call?.enqueue(object : Callback<Produto?>{
                    override fun onResponse(call: Call<Produto?>, response: Response<Produto?>) {
                        val produto : Produto = response.body()!!
                        //faco a verificalao para nao adicionar um item vazio ou nulo
                        if(produto != null) {
                            listaDeProduto.add(produto)
                            //System.out.println(produto.toString())
                            txtNomeProduto.text = produto.produto
                            txtValor.text = produto.valor.toString()
                        }
                    }

                    override fun onFailure(call: Call<Produto?>, t: Throwable) {
                        Log.e("PRODUTOSERVICE -> ", "Erro ao buscar o Codigo de Barras + " +
                                t.message )
                    }
                })
            }
        }

        codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
            runOnUiThread {
                Toast.makeText(this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun requestCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            codeScanner.startPreview()
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                )
            ) {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CAMERA
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                codeScanner.startPreview()
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

}