package br.com.ijbuenoapks.minhalistadecompras

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.ijbuenoapks.minhalistadecompras.adapter.AdapterProduto
import br.com.ijbuenoapks.minhalistadecompras.configuracoes.RetrofitConfig
import br.com.ijbuenoapks.minhalistadecompras.models.Produto
import br.com.ijbuenoapks.minhalistadecompras.models.ProdutoLista
import br.com.ijbuenoapks.minhalistadecompras.services.OnProdutoClickListener
import br.com.ijbuenoapks.minhalistadecompras.utilidades.FormataNumero
import com.budiyev.android.codescanner.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity(){

    //variaveis para requisição de permição e da camera
    private val PERMISSION_REQUEST_CAMERA = 0
    private lateinit var codeScanner: CodeScanner
    private var codBarScaneado : String = ""

    //variavel para a lista de produtos
    private val listaDeProduto : MutableList<ProdutoLista> = mutableListOf()
    lateinit var adapterProduto: AdapterProduto
    lateinit var produto: Produto

    //variaveis dos bjetos da tela
    lateinit var txtNomeProduto : TextView
    lateinit var txtValor : TextView
    lateinit var txtQtd : TextView
    lateinit var cmdAdicionar : Button
    lateinit var cmdMais : Button
    lateinit var cmdMenos : Button
    lateinit var cmdLimparScanAtual : Button
    lateinit var cmdFinalizar : Button
    lateinit var txtGastoAteMomento : TextView
    lateinit var txtOrcamentoInicial : TextView

    //verificar ainda
    lateinit var imm : InputMethodManager
    var orcamento = ""


    //para a listagem
    lateinit var recycler: RecyclerView

    //variavel para trabalho de valores
    var soma : Float = 0F
    var saldoAtual : Float = 0F
    //variavel que tera o valor do orcamento



    private val onProdutoClickListener = object  : OnProdutoClickListener{
        override fun onDelete(model: Produto) {
           adapterProduto.de
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //variaveis para trabalhar com os campos e botoes
        txtNomeProduto = findViewById<TextView>(R.id.txtNomeProduto)
        txtValor = findViewById<TextView>(R.id.txtValorProduto)
        cmdAdicionar = findViewById<Button>(R.id.cmdAdicionar)
        cmdMais = findViewById<Button>(R.id.cmdMais)
        txtQtd = findViewById<TextView>(R.id.txtQtd)
        cmdMenos = findViewById<Button>(R.id.cmdMenos)
        cmdLimparScanAtual = findViewById<Button>(R.id.cmdLimparScanAtual)
        cmdFinalizar = findViewById<Button>(R.id.cmdFinalizar)
        //para trabalhar com o recyclerview
        recycler = findViewById(R.id.rvListaDeItens)
        txtGastoAteMomento = findViewById(R.id.txtGastoAteMomento)
        txtOrcamentoInicial = findViewById(R.id.txtOrcamentoInicial)


        //InputMethodManager imm = (InputMethodManager)getSystemService(context.INPUT_METHOD_SERVICE);
        //           imm.hideSoftInputFromWindow(text.getWindowToken(), 0);

        imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        //parametros iniciais para o recyclerview
        recycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recycler.setHasFixedSize(true)

        //configuração para o adapter
        adapterProduto= AdapterProduto(this, ,listaDeProduto )
        //seto o adapter no recycles
        recycler.adapter = adapterProduto

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
        //limpo a tela inicialmente para dados default
        limparDadosAtuaisScanner()


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
                        produto = response.body()!!

                        //faco a verificalao para nao adicionar um item vazio ou nulo
                        if((produto != null) ||(!produto.id.equals(0)) ) {
                            txtNomeProduto.text = produto.produto
                            txtValor.text = produto.valor.toString().replace('.',',')
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
            inicializarVisualizacaoScanner()
        }

        //funçoes dos botoes
        cmdLimparScanAtual.setOnClickListener{
            limparDadosAtuaisScanner()
        }
        cmdMais.setOnClickListener {
            var t : Int = txtQtd.text.toString().toInt()
            t += 1
            txtQtd.text = t.toString()
        }
        cmdMenos.setOnClickListener {
            var t : Int = txtQtd.text.toString().toInt()
            if(t > 1) t -= 1
            txtQtd.text = t.toString()
        }
        cmdAdicionar.setOnClickListener {
            var produtoLista  = ProdutoLista(
                    txtQtd.text.toString().toInt(),
                    R.drawable.lixeira,
                    produto)
            listaDeProduto.add(produtoLista)
            recycler.adapter = adapterProduto

            val resultado = reallizaCalculos(produto.valor, produtoLista.quantidade, txtOrcamentoInicial.text.toString().toFloat())

            txtGastoAteMomento.text = "Gasto até o momento R$: " + resultado.replace('.', ',')

            limparDadosAtuaisScanner()



            recycler.setOnClickListener {

            }
        }


        fun removeProduct(model : ProdutoLista){
            val posicao = listaDeProduto.indexOf(model)
            listaDeProduto.remove(model)


        }


        //adicionando textwacher
        //txtOrcamentoInicial.addTextChangedListener(textWatcher)
       // txtOrcamentoInicial.filters = arrayOf(FormataNumero(10, 2))
        //txtOrcamentoInicial.addTextChangedListener(textWatcher)

        //txtOrcamentoInicial.setOnClickListener {
            showdialog()
        //}

    }



    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


            var str = s.toString()
            txtOrcamentoInicial.removeTextChangedListener(this)

            if (str.contains('.')){
                str = str.replace('.', ',')
            }
            //Toast.makeText(applicationContext, s, Toast.LENGTH_SHORT).show()
            //Toast.makeText(applicationContext, str, Toast.LENGTH_SHORT).show()
            //txtOrcamentoInicial.setText(str)
            //txtOrcamentoInicial.setSelection(str.length)

            txtOrcamentoInicial.addTextChangedListener(this)
            txtOrcamentoInicial.setOnClickListener{


            }

        }
    }


    fun showdialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Digite o valor do Orçamento")

// Set up the input
        val input = EditText(this)
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setHint("Digite o orçamento")
        input.setRawInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_CLASS_NUMBER)
        input.filters = arrayOf(FormataNumero(10, 2))

        builder.setView(input)

// Set up the buttons
        builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
            // Here you get get input text from the Edittext
            orcamento = input.text.toString()
            txtOrcamentoInicial.text = orcamento.replace('.', ',')
        })
        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

        builder.show()
    }


    private fun   reallizaCalculos(valorProduto : Float, qtdDeItens : Int, valorOrcamento : Float) : String{
        var valor  = "0,00"
        var soma  = valorProduto * qtdDeItens

        saldoAtual += soma

        valor = (valorOrcamento - saldoAtual).toString()

        return valor
    }

    private fun limparDadosAtuaisScanner(){
        txtNomeProduto.text = ""
        txtQtd.text = "1"
        txtValor.text="0,00"
        inicializarVisualizacaoScanner()
        //resseto o produto para um novo produto
        produto = Produto(0,"","",0F,"")
    }

    private fun inicializarVisualizacaoScanner(){
        codeScanner.startPreview()
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