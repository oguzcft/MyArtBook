package com.ouz.myartbookkotlin.fragments

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap

import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

import com.google.android.material.snackbar.Snackbar
import com.ouz.myartbookkotlin.data.ArtDataBase
import com.ouz.myartbookkotlin.data.ImagesEntity


import com.ouz.myartbookkotlin.databinding.FragmentAddBinding

import java.io.IOException
import java.lang.Exception


class AddFragment : Fragment() {
    //binding tanımlama
    private var _binding: FragmentAddBinding? = null
    private val binding get() = _binding!!

    //DB
    private lateinit var imagesDataBase: ArtDataBase
    private lateinit var imageUpdate: ImagesEntity

    //ActivityResultLauncher kullanıcaz çünkü yapacağımız işlemde karşılığında bir dönüt almak için yani gallery gidip resmi almak gibi...
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissinLauncher: ActivityResultLauncher<String>

    private val args: AddFragmentArgs by navArgs()
    var selectedBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        imagesDataBase = ArtDataBase.getImagesDataBase(requireContext())!!
        imageUpdate = imagesDataBase.artDao().getUser(args.id)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (args.info == "update") {
            binding.apply {
                selectedBitmap = Converters().toBitmap(imageUpdate.image)
                addImageView.setImageBitmap(selectedBitmap)
                artNameText.setText(imageUpdate.artName)
                artistNameText.setText(imageUpdate.artistName)
                yearText.setText(imageUpdate.year.toString())
            }

        }
        binding.addImageView.setOnClickListener {
            clickImage(it)
        }
        binding.saveButton.setOnClickListener {
            saveButton(it)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    //İmage boyutlandırma...
    fun makeSmallerBitmap(image: Bitmap, maxSize: Int): Bitmap {
        var width = image.width
        var height = image.height
        val bitmapRatio: Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            //landspace
            width = maxSize
            val scaleHeight = width / bitmapRatio
            height = scaleHeight.toInt()
        } else {
            //potrait
            height = maxSize
            val scaledWidth = height * bitmapRatio
            width = scaledWidth.toInt()
        }
        return Bitmap.createScaledBitmap(image, width, height, true)

    }

    fun clickImage(imageView: View) {
        //dataya ulaşmak için izin isteğini kontrol ediyoruz...
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {//Eğer izni alamamışsak Snackbar ile tekrar izni istemeye çalışıyoruz AMA bunu android in kendisine kalmış bişey oluyor gösterip göstermemek. buna Rationale deniyor....
                Snackbar.make(
                    imageView,
                    "Permission needed for gallery...",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Give Permission", View.OnClickListener {
                    //request permission
                    permissinLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }).show()


            } else {
                //request permission
                permissinLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else { //izin verilmişse dataya gidiyoruz...
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }
//izin ve data yönetimi
    private fun registerLauncher() {
        //burada gittiğimiz galerideki kullanıcının ne yaptığınıa bakıyoruz mesela resmi almdan cancel mı etmiş gibi...
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {//kullanıcı veriyi almış
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        val imageData =
                            intentFromResult.data//image URİ olarak aldık ama veriyi kaydetmek için bitmape çevirmek gerekir...
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(
                                    requireActivity().contentResolver,
                                    imageData!!
                                )
                                selectedBitmap = ImageDecoder.decodeBitmap(source)
                                binding.addImageView.setImageBitmap(selectedBitmap)
                            } else {
                                selectedBitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    imageData
                                )
                                binding.addImageView.setImageBitmap(selectedBitmap)
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }

                    }

                }
            }
        permissinLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permission granted
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {//permission denied
                    Toast.makeText(requireContext(), "Permission needed", Toast.LENGTH_LONG).show()
                }

            }
    }

    private fun saveButton(v: View) {
        val artNameText = binding.artNameText.text.toString()
        val artistNameText = binding.artistNameText.text.toString()
        val yearText = binding.yearText.text.toString().toInt()

        if (selectedBitmap != null) {
            val smallBitmap = makeSmallerBitmap(selectedBitmap!!, 300)

            //image 1 0 olarak byte a dönüştürme ve sonrasında bitmape dönüştürmek
            val byteArrayImage = Converters().fromBitmap(smallBitmap)
            //DATABASE
            try {
                if (args.info == "update") {
                    val updateImage = ImagesEntity(
                        artName = artNameText,
                        artistName = artistNameText,
                        year = yearText,
                        image = byteArrayImage
                    )
                    updateImage.id = args.id
                    imagesDataBase.artDao().updateImages(updateImage)

                    findNavController().navigate(AddFragmentDirections.addToMain())
                } else {
                    imagesDataBase.artDao().addImages(
                        ImagesEntity(
                            artName = artNameText,
                            artistName = artistNameText,
                            year = yearText,
                            image = byteArrayImage
                        )
                    )
                    findNavController().navigate(AddFragmentDirections.addToMain())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Toast.makeText(requireContext(), "Please Select Image", Toast.LENGTH_LONG).show()
        }
    }

}

